package org.exodusstudio.stellaris.client.events;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import org.exodusstudio.stellaris.client.cinematic.HeartOfLunaCinematic;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossIntroController;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.exodusstudio.stellaris.common.entities.vehicles.base.AbstractRoverBase;
import org.exodusstudio.stellaris.common.keybinds.KeyVariables;
import org.exodusstudio.stellaris.common.network.packets.KeyHandlerPacket;

import java.util.HashMap;
import java.util.Map;

public class ClientEvents {
    private static final Map<String, Boolean> SENT_KEY_STATES = new HashMap<>();

    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            SENT_KEY_STATES.clear();
            if (player != null) {
                KeyVariables.clearPlayer(player);
            }
        });

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            boolean locked = StarCrawlerBossIntroController.isAuthoritativelyLocked()
                    || StarCrawlerBossDeathController.isAuthoritativelyLocked()
                    || HeartOfLunaCinematic.isAuthoritativelyLocked();

            if (minecraft.player != null && minecraft.player.getVehicle() instanceof AbstractRoverBase rover && rover.getDriver() == minecraft.player) {
                if (locked) {
                    rover.updateControls(false, false, false, false);
                } else {
                    rover.updateControls(minecraft.options.keyUp.isDown(), minecraft.options.keyDown.isDown(),
                            minecraft.options.keyLeft.isDown(), minecraft.options.keyRight.isDown());
                }
            }

            syncMovementKeys(minecraft, locked);
        });
    }

    private static void syncMovementKeys(Minecraft minecraft, boolean locked) {
        if (minecraft.player == null) {
            return;
        }

        KeyVariables.getKey(minecraft).forEach((key, name) -> {
            boolean down = !locked && key.isDown();
            if (SENT_KEY_STATES.getOrDefault(name, false) == down) {
                return;
            }

            SENT_KEY_STATES.put(name, down);
            KeyVariables.setKeyVariable(name, minecraft.player.getUUID(), down);
            NetworkManager.sendToServer(new KeyHandlerPacket(name, down));
        });
    }
}
