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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.exodusstudio.stellaris.common.utils.CustomPlayerData;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.Stellaris;
import net.minecraft.world.level.material.FluidState;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements CustomPlayerData {

    @Unique
    private boolean stellaris$isPlanetMenuOpened = false;

    @Unique
    private int stellaris$parasiteTimer = -100;

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

    @Inject(method = "tick", at = @At("HEAD"))
    private void stellaris$onTick(CallbackInfo ci) {
        if (!this.level().isClientSide() && Stellaris.CONFIG.parasiteConfig.enableParasiteDrop) {
            FluidState fluidState = this.level().getFluidState(this.blockPosition());
            boolean inBlueLiquid = fluidState.is(FluidsRegistry.BLUE_LIQUID_STILL.get()) || fluidState.is(FluidsRegistry.BLUE_LIQUID_FLOWING.get());

            if (inBlueLiquid) {
                if (stellaris$parasiteTimer == -100) {
                    int minTicks = Stellaris.CONFIG.parasiteConfig.minDropIntervalTicks;
                    int maxRandomTicks = Stellaris.CONFIG.parasiteConfig.randomDropIntervalMaxTicks;
                    stellaris$parasiteTimer = minTicks + this.random.nextInt(maxRandomTicks + 1);
                }

                else if (stellaris$parasiteTimer <= 0) {
                    Player player = (Player) (Object) this;
                    player.getInventory().placeItemBackInInventory(new ItemStack(ItemsRegistry.PARASITE.get()));

                    int minTicks = Stellaris.CONFIG.parasiteConfig.minDropIntervalTicks;
                    int maxRandomTicks = Stellaris.CONFIG.parasiteConfig.randomDropIntervalMaxTicks;
                    stellaris$parasiteTimer = minTicks + this.random.nextInt(maxRandomTicks + 1);
                }

                stellaris$parasiteTimer--;
            } else {
                stellaris$parasiteTimer = -100;
            }
        }
    }
}
