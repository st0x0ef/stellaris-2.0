package org.exodusstudio.stellaris.common.menus;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface IVehicleMenu {
    int getFuel();

    static boolean isVehicleInInteractionRange(@Nullable Entity vehicle, Player player) {
        if (vehicle == null) {
            return player.level().isClientSide();
        }

        return vehicle.isAlive() && player.isWithinEntityInteractionRange(vehicle, 4.0);
    }
}
