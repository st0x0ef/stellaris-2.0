package org.exodusstudio.stellaris.client.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossIntroController;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.exodusstudio.stellaris.common.entities.vehicles.base.AbstractRoverBase;
import org.exodusstudio.stellaris.common.keybinds.KeyVariables;
import org.exodusstudio.stellaris.common.network.packets.KeyHandlerPacket;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {
    public static void init() {
        ClientRawInputEvent.KEY_PRESSED.register(((minecraft, action, keyEvent) -> {
            if(minecraft.player == null) return EventResult.pass();
            if (StarCrawlerBossIntroController.isAuthoritativelyLocked()
                    || StarCrawlerBossDeathController.isAuthoritativelyLocked()) return EventResult.pass();

            KeyVariables.getKey(minecraft).forEach((key, name) -> {
                if (key.getDefaultKey().getValue() == keyEvent.key() && action == GLFW.GLFW_RELEASE) {
                    KeyVariables.setKeyVariable(name, minecraft.player.getUUID(), false);
                    NetworkManager.sendToServer(new KeyHandlerPacket(name, false));
                }

                else if (key.getDefaultKey().getValue() == keyEvent.key() && action == GLFW.GLFW_PRESS) {
                    KeyVariables.setKeyVariable(name, minecraft.player.getUUID(), true);
                    NetworkManager.sendToServer(new KeyHandlerPacket(name, true));
                }
            });
            return EventResult.pass();
        }));

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            if (minecraft.player != null && minecraft.player.getVehicle() instanceof AbstractRoverBase rover && rover.getDriver() == minecraft.player) {
                if (StarCrawlerBossIntroController.isAuthoritativelyLocked()
                        || StarCrawlerBossDeathController.isAuthoritativelyLocked()) {
                    rover.updateControls(false, false, false, false, minecraft.player);
                    return;
                }

                boolean forward = minecraft.options.keyUp.isDown();
                boolean backward = minecraft.options.keyDown.isDown();
                boolean left = minecraft.options.keyLeft.isDown();
                boolean right = minecraft.options.keyRight.isDown();
                rover.updateControls(forward, backward, left, right, minecraft.player);
            }
        });
    }
}
