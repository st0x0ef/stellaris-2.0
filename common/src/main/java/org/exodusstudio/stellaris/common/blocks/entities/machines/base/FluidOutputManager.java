package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class FluidOutputManager {

    public final FluidOutputable blockEntity;
    public final HashMap<Direction, @Nullable UniversalFluidStorage> outputs = new HashMap<>();

    public FluidOutputManager(FluidOutputable blockEntity) {
        this.blockEntity = blockEntity;
        this.loadDefaultConfiguration();
    }

    public void loadDefaultConfiguration() {
        for (Direction direction : Direction.values()) {
            UniversalFluidStorage storage = blockEntity.getFluidTank(direction);
            outputs.putIfAbsent(direction, storage);
        }
    }

    public void distributeFluids() {
        for (Direction direction : outputs.keySet()) {
            UniversalFluidStorage storage = outputs.get(direction);
            if(storage != null) {
                //
            }
        }
    }

    public void save(ValueOutput output) {
        ValueOutput.TypedOutputList<FluidOutputEntry> fluidOutput = output.list("fluid-output", FluidOutputEntry.CODEC);

        for(Direction direction : outputs.keySet()) {
            UniversalFluidStorage storage = outputs.get(direction);
            if(storage != null) {
                int id = getIdFromStorage(storage);
                if(id != -1) {
                    fluidOutput.add(new FluidOutputEntry(id, direction));
                }
            }
        }
    }

    public void load(ValueInput input) {
       Optional<ValueInput.TypedInputList<FluidOutputEntry>>  fluidOutput = input.list("fluid-output", FluidOutputEntry.CODEC);
       fluidOutput.ifPresentOrElse(list -> {
             for(FluidOutputEntry entry : list) {
               UniversalFluidStorage storage = this.getStorageFromId(entry.storageId);
               if(storage != null) {
                    outputs.put(entry.direction, storage);
               }
             }
       }, this::loadDefaultConfiguration);
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
