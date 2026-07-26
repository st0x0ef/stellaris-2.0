package org.exodusstudio.stellaris.common.assistant;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.data.assistant.AssistantData;
import org.exodusstudio.stellaris.common.data.assistant.AssistantLine;
import org.exodusstudio.stellaris.common.data.assistant.AssistantMessage;
import org.exodusstudio.stellaris.common.data.assistant.AssistantTrigger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class AssistantManager {

    private record PendingLine(UUID player, long dueTick, Component text) {}

    private static final List<PendingLine> PENDING = new ArrayList<>();

    public static void fire(ServerPlayer player, AssistantTrigger trigger, Object... args) {
        if (!Stellaris.CONFIG.assistantConfig.enableAssistant) return;

        MinecraftServer server = player.level().getServer();

        Identifier dimension = player.level().dimension().identifier();

        for (AssistantData.Entry entry : AssistantData.get(trigger)) {
            AssistantMessage message = entry.message();

            if (message.dimension().isPresent() && !message.dimension().get().equals(dimension)) continue;
            if (message.oncePerPlayer() && !AssistantPlayerData.markMessageShown(player, entry.id())) continue;

            for (AssistantLine line : message.lines()) {
                PENDING.add(new PendingLine(
                        player.getUUID(),
                        server.getTickCount() + Math.max(0, line.delay()),
                        format(message.speakerOrDefault(), resolve(line.text(), args))
                ));
            }
        }
    }

    public static void onPlanetLanding(ServerPlayer player) {
        Planet planet = PlanetsData.getPlanet(player.level().dimension());
        if (planet == null) return;

        if (AssistantPlayerData.markPlanetVisited(player, planet.dimension())) {
            fire(player, AssistantTrigger.PLANET_FIRST_VISIT);
        }
    }

    public static void tick(MinecraftServer server) {
        if (PENDING.isEmpty()) return;

        long tick = server.getTickCount();
        Iterator<PendingLine> iterator = PENDING.iterator();

        while (iterator.hasNext()) {
            PendingLine line = iterator.next();
            if (line.dueTick() > tick) continue;

            iterator.remove();

            ServerPlayer player = server.getPlayerList().getPlayer(line.player());
            if (player != null) {
                player.sendSystemMessage(line.text());
            }
        }
    }

    public static void clear() {
        PENDING.clear();
    }

    private static Component resolve(Component text, Object[] args) {
        if (args.length == 0
                || !(text.getContents() instanceof TranslatableContents translatable)
                || translatable.getArgs().length > 0) {
            return text;
        }

        MutableComponent resolved = Component.translatableWithFallback(translatable.getKey(), translatable.getFallback(), args);
        resolved.setStyle(text.getStyle());
        text.getSiblings().forEach(resolved::append);
        return resolved;
    }

    private static Component format(Component speaker, Component text) {
        return Component.empty()
                .append(Component.literal("[").append(speaker).append("] ").withStyle(ChatFormatting.AQUA))
                .append(text);
    }
}
