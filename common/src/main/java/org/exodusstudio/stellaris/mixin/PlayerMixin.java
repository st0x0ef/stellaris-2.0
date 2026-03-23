package org.exodusstudio.stellaris.mixin;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.network.packets.SyncPlanetMenuState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.exodusstudio.stellaris.common.utils.CustomPlayerData;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements CustomPlayerData {

    @Unique
    private boolean stellaris$isPlanetMenuOpened = false;


    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void stellaris$setPlanetMenuOpen(boolean open, Player player, boolean sync) {
        stellaris$isPlanetMenuOpened = open;
        if (sync && player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new SyncPlanetMenuState(open));
        }
    }

    @Override
    public boolean stellaris$isPlanetMenuOpen() {
        return stellaris$isPlanetMenuOpened;
    }
}
