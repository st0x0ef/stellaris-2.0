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

public class FluidOutputManager {

    public final FluidOutputable blockEntity;
    public final HashMap<Direction, @Nullable FluidStack> outputs = new HashMap<>();

    public FluidOutputManager(FluidOutputable blockEntity) {
        this.blockEntity = blockEntity;
    }

    //TODO replace this with an interface or something
    public void setDefault(FluidOutputEntry... entries) {
        if(!outputs.isEmpty()) return;

        for (FluidOutputEntry entry : entries) {
            outputs.put(entry.direction, entry.fluid);
        }
    }

    public void distributeFluids(Level level, BlockPos pos) {
        for (Direction direction : outputs.keySet()) {
            UniversalFluidStorage  storage = blockEntity.getFluidTank(direction);
            if(storage == null) return;

            FluidUtil.distributeFluidNearby(level, pos, storage.getFluidInTank(0), List.of(direction));
        }
    }


    public void save(ValueOutput output) {
        ValueOutput.TypedOutputList<FluidOutputEntry> fluidOutput = output.list("fluid-output", FluidOutputEntry.CODEC);

        for(Direction direction : outputs.keySet()) {
            FluidStack fluid = outputs.get(direction);
            if(fluid != null) {
                System.err.println("Saving Fluid: " + fluid.getName().getString() + " at direction: " + direction);
                fluidOutput.add(new FluidOutputEntry(direction, fluid));
            }
        }
    }

    public void load(ValueInput input) {
       Optional<ValueInput.TypedInputList<FluidOutputEntry>>  fluidOutput = input.list("fluid-output", FluidOutputEntry.CODEC);
        Stellaris.LOG.error("Before load \n " + this);

        fluidOutput.ifPresent(list -> {
             for(FluidOutputEntry entry : list) {
                 outputs.put(entry.direction, entry.fluid);
             }
           Stellaris.LOG.error("After load \n " + this);
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
