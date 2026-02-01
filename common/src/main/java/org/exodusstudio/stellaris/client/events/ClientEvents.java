package org.exodusstudio.stellaris.client.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.networking.NetworkManager;
import org.exodusstudio.stellaris.common.keybinds.KeyVariables;
import org.exodusstudio.stellaris.common.network.packets.KeyHandlerPacket;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {
    public static void init() {
        ClientRawInputEvent.KEY_PRESSED.register(((minecraft, action, keyEvent) -> {
            if(minecraft.player == null) return EventResult.pass();

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
    }
}
