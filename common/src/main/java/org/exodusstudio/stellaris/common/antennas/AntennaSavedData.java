package org.exodusstudio.stellaris.common.antennas;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.exodusstudio.stellaris.Stellaris;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AntennaSavedData extends SavedData {

    public Map<UUID, Antenna> antennas = new HashMap<>();


    public static final Codec<Map<UUID, Antenna>> MAP_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Antenna.CODEC);
    public static final Codec<AntennaSavedData> CODEC = MAP_CODEC.xmap(
            AntennaSavedData::new,
            data -> data.antennas
    );


    private static final SavedDataType<@NotNull AntennaSavedData> TYPE = new SavedDataType<>(
            "antennas",
            AntennaSavedData::new,
            CODEC,
            null
    );


    public AntennaSavedData() {
    }

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
    public Map.Entry<UUID, Antenna> getAntenna(Antenna antenna) {
        return this.getAntenna(antenna.dimension, antenna.blockPos);
    }

    @Nullable
    public Map.Entry<UUID, Antenna> getAntenna(ResourceKey<Level> dimension, BlockPos pos) {
        return this.antennas.entrySet().stream()
                .filter(entry -> entry.getValue().dimension.equals(dimension) && entry.getValue().blockPos.equals(pos))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public Map.Entry<UUID, Antenna> getAntenna(String name) {
        return  this.antennas.entrySet().stream()
                .filter(entry -> entry.getValue().name.equals(name))
                .findFirst().orElse(null);

    }

    public void whitelistPlayers(UUID antennaUUID, Collection<ResolvableProfile> gameProfiles) {
        Antenna antenna = this.getAntenna(antennaUUID);
        if(antenna == null) return;

        List<UUID> newUUIDs = new ArrayList<>(antenna.whitelist);
        newUUIDs.addAll(gameProfiles.stream().map(profile -> profile.partialProfile().id()).toList());
        antenna.whitelist = newUUIDs;
        this.setDirty();
    }

    public boolean isPlayerOwner(UUID antennaUUID, Player player) {
        Antenna antenna = this.getAntenna(antennaUUID);
        //If this is true, this means that the antenna hasn't been configured
        if(antenna == null) return true;

        return antenna.ownerUUID.equals(player.getGameProfile().id());

    }

    @Nullable
    public Antenna getAntenna(UUID uuid) {
        return this.antennas.get(uuid);
    }

    public void removeAntenna(Antenna antenna) {
        Map.Entry<UUID, Antenna> existing = this.getAntenna(antenna);
        if (existing == null) {
            return;
        }

        this.antennas.remove(existing.getKey());
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

        Map<UUID, Antenna> antennaList = new HashMap<>();

        for(Map.Entry<UUID, Antenna> entry : this.antennas.entrySet()) {
            if(entry.getValue().ownerUUID.equals(player)) {
                antennaList.put(entry.getKey(), entry.getValue());
            }
        }
        return antennaList;
    }


    public static AntennaSavedData getSavedAntennas(MinecraftServer server) {


        // This could be either the overworld or another dimension.
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);

        if (level == null) {
            Stellaris.LOG.info("level is null");
            return new AntennaSavedData(); // Return a new instance if the level is null.
        }

        // The first time the following 'computeIfAbsent' function is called, it creates a new 'SavedBlockData'
        // instance and stores it inside the 'DimensionDataStorage'.
        // Subsequent calls to 'computeIfAbsent' returns the saved 'SavedBlockData' NBT on disk to the Codec in our type,
        // using the Codec to decode the NBT into our saved data.
        return level.getDataStorage().computeIfAbsent(TYPE);
    }



}
