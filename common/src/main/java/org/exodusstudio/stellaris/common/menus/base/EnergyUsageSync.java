package org.exodusstudio.stellaris.common.menus.base;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.network.packets.SyncEnergyUsagePacket;
import org.jetbrains.annotations.Nullable;

public final class EnergyUsageSync {

    @Nullable
    private final BaseEnergyContainerBlockEntity machine;
    @Nullable
    private final ServerPlayer player;
    private boolean sent;
    private int current;
    private int max;

    private EnergyUsageSync(@Nullable BaseEnergyContainerBlockEntity machine, @Nullable ServerPlayer player) {
        this.machine = machine;
        this.player = player;
    }

    public static EnergyUsageSync create(@Nullable BaseEnergyContainerBlockEntity machine, Inventory inventory) {
        ServerPlayer player = inventory.player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        return new EnergyUsageSync(player != null ? machine : null, player);
    }

    public void broadcastChanges(int containerId) {
        if (machine == null || player == null) {
            return;
        }

        int newCurrent = machine.getCurrentEnergyUsage();
        int newMax = machine.getMaxEnergyUsage();
        if (sent && newCurrent == current && newMax == max) {
            return;
        }

        sent = true;
        current = newCurrent;
        max = newMax;
        NetworkManager.sendToPlayer(player, new SyncEnergyUsagePacket(containerId, current, max));
    }

    public void set(int current, int max) {
        this.current = current;
        this.max = max;
    }

    public int current() {
        return current;
    }

    public int max() {
        return max;
    }
}
