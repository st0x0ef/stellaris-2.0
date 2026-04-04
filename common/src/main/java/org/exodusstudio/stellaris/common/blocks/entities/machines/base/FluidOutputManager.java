package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.network.packets.SyncOutputManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class is used to manage the fluid outputs of a machine.
 * It saved which fluid is outputted in which direction and can distribute the fluids to nearby blocks.
 * It also handles saving and loading the fluid outputs and syncing with the client.
 */
public class FluidOutputManager {

    public final FluidOutputable blockEntity;
    public final HashMap<Direction, @Nullable FluidStack> outputs = new HashMap<>();

    public FluidOutputManager(FluidOutputable blockEntity) {
        this.blockEntity = blockEntity;
    }

    /**
     * Use to set the default fluid outputs of a machine. This should be called in the constructor of the block entity.
     * We can't use FluidProvider.BLOCK#getFluidTank because at the start the tanks are empty
     * @param entries the default fluid outputs to set. The direction of the output is determined by the direction field of the entry.
     */
    public void setDefault(FluidOutputEntry... entries) {
        if(!outputs.isEmpty()) return;

        for (FluidOutputEntry entry : entries) {
            outputs.put(entry.direction, entry.fluid);
        }
    }

    public void distributeFluids(Level level, BlockPos pos) {


        for (Direction direction : Direction.values()) {
            List<UniversalFluidStorage> storages = blockEntity.getOutputFluidsTank();
            FluidStack currentFluid = outputs.get(direction);

            if(currentFluid == null) continue;

            for(UniversalFluidStorage storage : storages) {

                for(int i = 0; i < storage.getTanks(); i++) {
                    if(storage.getFluidInTank(i).isFluidEqual(currentFluid)) {
                        FluidUtil.distributeFluidNearby(level, pos, storage.getFluidInTank(i), List.of(direction));
                    }
                }


            }
        }
    }


    public void save(ValueOutput output) {
        ValueOutput.TypedOutputList<FluidOutputEntry> fluidOutput = output.list("fluid-output", FluidOutputEntry.CODEC);

        for(Direction direction : outputs.keySet()) {
            FluidStack fluid = outputs.get(direction);
            if(fluid != null) {
                fluidOutput.add(new FluidOutputEntry(direction, fluid));
            }
        }
    }

    public void load(ValueInput input) {
       Optional<ValueInput.TypedInputList<FluidOutputEntry>>  fluidOutput = input.list("fluid-output", FluidOutputEntry.CODEC);
        fluidOutput.ifPresent(list -> {
             for(FluidOutputEntry entry : list) {
                 outputs.put(entry.direction, entry.fluid);
             }
       });
    }

    public void syncWithPlayer(ServerPlayer player, BlockEntity blockEntity) {
        for(Direction direction : outputs.keySet()) {
            FluidStack fluid = outputs.get(direction);
            if(fluid != null) {
                NetworkManager.sendToPlayer(player, new SyncOutputManager.S2C(blockEntity.getBlockPos(), direction, fluid));
                continue;
            }
            NetworkManager.sendToPlayer(player, new SyncOutputManager.S2C(blockEntity.getBlockPos(), direction, FluidStack.empty()));

        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Direction direction : outputs.keySet()) {
            FluidStack fluid = outputs.get(direction);
            if(fluid != null) {
                builder.append(direction.toString()).append(": ").append(fluid.getName().getString()).append("\n");
            }
        }
        return builder.toString();
    }

    public record FluidOutputEntry(Direction direction, FluidStack fluid) {

        public static Codec<FluidOutputEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Direction.CODEC.fieldOf("direction").forGetter(FluidOutputEntry::direction),
                FluidStack.CODEC.fieldOf("fluid").forGetter(FluidOutputEntry::fluid)
        ).apply(instance, FluidOutputEntry::new));
    }

}
