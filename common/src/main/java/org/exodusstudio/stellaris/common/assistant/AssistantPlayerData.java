package org.exodusstudio.stellaris.common.assistant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record AssistantPlayerData(Set<Identifier> visitedPlanets, Set<Identifier> shownMessages) {

    public static final Identifier KEY = IdentifierUtils.id("assistant_data");

    private static final Codec<Set<Identifier>> IDENTIFIER_SET_CODEC =
            Identifier.CODEC.listOf().xmap(Set::copyOf, List::copyOf);

    public static final Codec<AssistantPlayerData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    IDENTIFIER_SET_CODEC.optionalFieldOf("visited_planets", Set.of()).forGetter(AssistantPlayerData::visitedPlanets),
                    IDENTIFIER_SET_CODEC.optionalFieldOf("shown_messages", Set.of()).forGetter(AssistantPlayerData::shownMessages)
            ).apply(instance, AssistantPlayerData::new)
    );

    public static AssistantPlayerData empty() {
        return new AssistantPlayerData(Set.of(), Set.of());
    }

    public static AssistantPlayerData get(Player player) {
        if (player.stellaris$hasDataAttachments(KEY)) {
            return player.stellaris$getDataAttachments(KEY, AssistantPlayerData.class);
        } else {
            player.stellaris$saveDataAttachments(KEY, empty());
        }

        return empty();
    }

    public static void save(Player player, AssistantPlayerData data) {
        player.stellaris$saveDataAttachments(KEY, data);
    }

    public static boolean markPlanetVisited(Player player, Identifier dimension) {
        AssistantPlayerData data = get(player);
        if (data.visitedPlanets().contains(dimension)) {
            return false;
        }

        Set<Identifier> visited = new HashSet<>(data.visitedPlanets());
        visited.add(dimension);
        save(player, new AssistantPlayerData(visited, data.shownMessages()));
        return true;
    }

    public static boolean markMessageShown(Player player, Identifier messageId) {
        AssistantPlayerData data = get(player);
        if (data.shownMessages().contains(messageId)) {
            return false;
        }

        Set<Identifier> shown = new HashSet<>(data.shownMessages());
        shown.add(messageId);
        save(player, new AssistantPlayerData(data.visitedPlanets(), shown));
        return true;
    }
}
