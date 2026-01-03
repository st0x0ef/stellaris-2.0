package org.exodusstudio.stellaris.client.registry;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.network.packets.KeyHandlerPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class KeyMappingsRegistry {

    public static KeyMapping.Category CATEGORY = new KeyMapping.Category(IdentifierUtils.id("default"));

    public static KeyMapping ROCKET_START = new KeyMapping("key." + Stellaris.MOD_ID + ".rocket_start", InputConstants.KEY_SPACE, CATEGORY);

    public static void clientTick(Minecraft minecraft) {
        Player player = minecraft.player;

        if (player == null) {
            return;
        }
        if (player.getVehicle() != null && player.getVehicle() instanceof RocketEntity) {
            while (ROCKET_START.consumeClick()) {
                NetworkManager.sendToServer(new KeyHandlerPacket("start_rocket", true));
            }
        }
    }

    public static void init() {
        KeyMappingRegistry.register(ROCKET_START);
    }
}
