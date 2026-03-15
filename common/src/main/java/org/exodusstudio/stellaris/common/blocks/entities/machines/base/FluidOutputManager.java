package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
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


    public record FluidOutputEntry(Direction direction, FluidStack fluid) {

        public static Codec<FluidOutputEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Direction.CODEC.fieldOf("direction").forGetter(FluidOutputEntry::direction),
                FluidStack.CODEC.fieldOf("fluid").forGetter(FluidOutputEntry::fluid)
        ).apply(instance, FluidOutputEntry::new));
    }

}
