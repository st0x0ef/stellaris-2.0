package org.exodusstudio.stellaris.client.registry;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.TestScreen;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.network.packets.KeyHandlerPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class KeyMappingsRegistry {

    public static KeyMapping.Category CATEGORY = new KeyMapping.Category(IdentifierUtils.id("default"));

    public static KeyMapping ROCKET_START = new KeyMapping("key." + Stellaris.MOD_ID + ".rocket_start", InputConstants.KEY_SPACE, CATEGORY);
    public static KeyMapping JET_SWITCH_MODE = new KeyMapping("key." + Stellaris.MOD_ID + ".jet_switch_mode", InputConstants.KEY_V, CATEGORY);
    public static KeyMapping TEST_SCREEN = new KeyMapping("key." + Stellaris.MOD_ID + ".test_screen", InputConstants.KEY_P, CATEGORY);


    public static void clientTick(Minecraft minecraft) {
        Player player = minecraft.player;

        if (player == null) {
            return;
        }
        boolean inRocket = player.getVehicle() instanceof RocketEntity;
        while (ROCKET_START.consumeClick()) {
            if (inRocket) {
                NetworkManager.sendToServer(new KeyHandlerPacket("start_rocket", true));
            }
        }
        while (JET_SWITCH_MODE.consumeClick()) {
            NetworkManager.sendToServer(new KeyHandlerPacket("switch_jet_mode", true));
        }
        while (TEST_SCREEN.consumeClick()) {
            if(!(minecraft.screen instanceof TestScreen)) {
                minecraft.setScreen(new TestScreen());

            }
        }
    }

    public static void init() {
        KeyMappingRegistry.register(ROCKET_START);
        KeyMappingRegistry.register(JET_SWITCH_MODE);
        KeyMappingRegistry.register(TEST_SCREEN);
    }
}
