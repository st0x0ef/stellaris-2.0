package org.exodusstudio.stellaris.common.antennas;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AntennaSavedData extends SavedData {

    public Map<UUID, Antenna> antennas = new HashMap<UUID, Antenna>();


    public static final Codec<Map<UUID, Antenna>> MAP_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Antenna.CODEC);
    public static final Codec<AntennaSavedData> CODEC = MAP_CODEC.xmap(
            AntennaSavedData::new,
            data -> data.antennas
    );


    private static final SavedDataType<@NotNull AntennaSavedData> TYPE = new SavedDataType<>(
            "antennas", // The unique name for this saved data.
            AntennaSavedData::new, // If there's no 'SavedBlockData', yet create one and refresh fields.
            CODEC, // The codec used for serialization/deserialization.
            null // A data fixer, which is not needed here.
    );


    public AntennaSavedData() {
        // ...
    }

    // Data constructor
    public AntennaSavedData(Map<UUID, Antenna> antennas) {
        this.antennas = new HashMap<>(antennas);
    }

    public UUID addAntenna(Antenna antenna) {
        UUID uuid = UUID.randomUUID();
        this.antennas.put(uuid, antenna);
        this.setDirty();
        return uuid;
    }

    @Nullable
    public Map.Entry<UUID, Antenna> getAntenna(String name) {
        return  this.antennas.entrySet().stream()
                .filter(entry -> entry.getKey().equals(name))
                .findFirst().orElse(null);

    }

    @Nullable
    public Antenna getAntenna(UUID uuid) {
        return this.antennas.get(uuid);
    }

    public void removeAntenna(Antenna antenna) {
        this.antennas.entrySet().removeIf(entry -> entry.getValue().equals(antenna));
        this.setDirty();
    }

    public void modifyAntenna(UUID uuid, Antenna antenna) {
        this.antennas.put(uuid, antenna);
        this.setDirty();
    }

    public void removeAntenna(UUID uuid) {
        this.antennas.remove(uuid);
        this.setDirty();
    }

    public Map<UUID, Antenna> getAntennas(@Nullable UUID player) {
        if(player == null) {
            return this.antennas;
        }

        Map<UUID, Antenna> antennaList = new HashMap<UUID, Antenna>();

        for(Map.Entry<UUID, Antenna> entry : this.antennas.entrySet()) {
            if(entry.getValue().ownerUUID().equals(player)) {
                antennaList.put(entry.getKey(), entry.getValue());
            }
        }
        return antennaList;
    }


    public static AntennaSavedData getSavedBlockData(MinecraftServer server) {

        // This could be either the overworld or another dimension.
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);

        if (level == null) {
            return new AntennaSavedData(); // Return a new instance if the level is null.
        }

        // The first time the following 'computeIfAbsent' function is called, it creates a new 'SavedBlockData'
        // instance and stores it inside the 'DimensionDataStorage'.
        // Subsequent calls to 'computeIfAbsent' returns the saved 'SavedBlockData' NBT on disk to the Codec in our type,
        // using the Codec to decode the NBT into our saved data.
        return level.getDataStorage().computeIfAbsent(TYPE);
    }



}
