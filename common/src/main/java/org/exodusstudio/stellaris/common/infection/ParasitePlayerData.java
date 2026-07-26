package org.exodusstudio.stellaris.common.infection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public record ParasitePlayerData(boolean everInfected, int infectionCount, long lastInfectedGameTime) {

    public static final Identifier KEY = IdentifierUtils.id("parasite_data");

    public static final Codec<ParasitePlayerData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("ever_infected", false).forGetter(ParasitePlayerData::everInfected),
                    Codec.INT.optionalFieldOf("infection_count", 0).forGetter(ParasitePlayerData::infectionCount),
                    Codec.LONG.optionalFieldOf("last_infected_game_time", -1L).forGetter(ParasitePlayerData::lastInfectedGameTime)
            ).apply(instance, ParasitePlayerData::new)
    );

    public static ParasitePlayerData empty() {
        return new ParasitePlayerData(false, 0, -1L);
    }

    public static ParasitePlayerData get(Player player) {
        if (player.stellaris$hasDataAttachments(KEY)) {
            return player.stellaris$getDataAttachments(KEY, ParasitePlayerData.class);
        } else {
            player.stellaris$saveDataAttachments(KEY, empty());
        }

        return empty();
    }

    public static void save(Player player, ParasitePlayerData data) {
        player.stellaris$saveDataAttachments(KEY, data);
    }

    public static void recordInfection(Player player, long gameTime) {
        ParasitePlayerData data = get(player);
        save(player, new ParasitePlayerData(true, data.infectionCount() + 1, gameTime));
    }

    public static boolean hasEverBeenInfected(Player player) {
        return get(player).everInfected();
    }
}
