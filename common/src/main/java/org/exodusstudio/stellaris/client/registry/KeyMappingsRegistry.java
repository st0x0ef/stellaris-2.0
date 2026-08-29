package org.exodusstudio.stellaris.client.registry;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.network.packets.KeyHandlerPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class KeyMappingsRegistry {

    public static KeyMapping.Category CATEGORY = KeyMapping.Category.register(IdentifierUtils.id("stellaris"));

    public static KeyMapping ROCKET_START = new KeyMapping("key." + Stellaris.MOD_ID + ".rocket_start", InputConstants.KEY_SPACE, CATEGORY);
    public static KeyMapping JET_SWITCH_MODE = new KeyMapping("key." + Stellaris.MOD_ID + ".jet_switch_mode", InputConstants.KEY_V, CATEGORY);
    public static KeyMapping ENABLE_NIGHT_VISION = new KeyMapping("key." + Stellaris.MOD_ID + ".night_vision", InputConstants.KEY_M, CATEGORY);

    public static final List<KeyMapping> ALL = List.of(ROCKET_START, JET_SWITCH_MODE, ENABLE_NIGHT_VISION);


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
        while (ENABLE_NIGHT_VISION.consumeClick()) {
            NetworkManager.sendToServer(new KeyHandlerPacket("night_vision", true));

        }
    }

    public static void init() {
        ALL.forEach(KeyMappingRegistry::register);
    }
}
