package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FluidOutputManager {

    public final FluidOutputable blockEntity;
    public final HashMap<UniversalFluidStorage, Set<Direction>> outputs = new HashMap<>();

    public FluidOutputManager(FluidOutputable blockEntity) {
        this.blockEntity = blockEntity;
        for (Direction direction : Direction.values()) {
            UniversalFluidStorage storage = blockEntity.getFluidTank(direction);
            if (storage != null) {
                outputs.computeIfAbsent(storage, k -> new java.util.HashSet<>()).add(direction);
            }
        }
    }

    public void distributeFluids() {
        for (UniversalFluidStorage storage : outputs.keySet()) {
            Set<Direction> directions = outputs.get(storage);
            for (Direction direction : directions) {
                // Implement fluid distribution logic here, e.g., try to push fluids to adjacent blocks
            }
        }
    }

    public void save(ValueOutput output) {
        ValueOutput.TypedOutputList<FluidOutputEntry> fluidOutput = output.list("fluid-output", FluidOutputEntry.CODEC);

        for(UniversalFluidStorage storage : outputs.keySet()) {
            int id = getIdFromStorage(storage);
            if(id == -1) continue;
            for(Direction direction : outputs.get(storage)) {
                fluidOutput.add(new FluidOutputEntry(id, direction));
            }
        }
    }

    public void load(ValueInput input) {
       Optional<ValueInput.TypedInputList<FluidOutputEntry>>  fluidOutput = input.list("fluid-output", FluidOutputEntry.CODEC);
       fluidOutput.ifPresent(list -> {
             for(FluidOutputEntry entry : list) {
               UniversalFluidStorage storage = this.getStorageFromId(entry.storageId);
               if(storage != null) {
                    outputs.computeIfAbsent(storage, k -> new java.util.HashSet<>()).add(entry.direction);
               }
             }
       });
    }

    public final int getIdFromStorage(UniversalFluidStorage storage) {
        List<UniversalFluidStorage> storages = this.blockEntity.getIndexedStorages();
        for (int i = 0; i < storages.size(); i++) {
            if (storages.get(i) == storage) {
                return i;
            }
        }
        return -1;
    }

    public final @Nullable UniversalFluidStorage getStorageFromId(int id) {
        List<UniversalFluidStorage> storages = this.blockEntity.getIndexedStorages();
        if (id < 0 || id >= storages.size()) {
            return null;
        }
        return storages.get(id);
    }

    public record FluidOutputEntry(int storageId, Direction direction) {

        public static Codec<FluidOutputEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("storageId").forGetter(FluidOutputEntry::storageId),
                Direction.CODEC.fieldOf("direction").forGetter(FluidOutputEntry::direction)
        ).apply(instance, FluidOutputEntry::new));
    }

}
