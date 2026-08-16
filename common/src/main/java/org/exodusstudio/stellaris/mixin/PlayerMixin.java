package org.exodusstudio.stellaris.mixin;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.network.packets.SyncPlanetMenuState;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.utils.CustomPlayerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements CustomPlayerData {

    @Unique
    private boolean stellaris$isPlanetMenuOpened = false;

    @Unique
    private int stellaris$parasiteTimer = -100;

    @Unique
    private int stellaris$nextFluidCheck = 20;

    @Unique
    @Final
    private final int stellaris$fluidTickInterval = 20;

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


    @Unique
    private boolean stellaris$isEyeInBlueLiquid() {
        double eyeY = this.getEyeY();
        BlockPos eyePos = BlockPos.containing(this.getX(), eyeY, this.getZ());
        FluidState fluidState = this.level().getFluidState(eyePos);
        if (!fluidState.is(TagsRegistry.FluidTags.BLUE_LIQUID)) {
            return false;
        }

        return eyeY < eyePos.getY() + fluidState.getHeight(this.level(), eyePos);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void stellaris$onTick(CallbackInfo ci) {

        if (!this.level().isClientSide()) {
            if (Stellaris.CONFIG.parasiteConfig.enableParasiteDrop && stellaris$nextFluidCheck <= 0) {
                if (stellaris$isEyeInBlueLiquid()) {
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

                    stellaris$parasiteTimer -= stellaris$fluidTickInterval;
                } else {
                    stellaris$parasiteTimer = -100;
                }

                stellaris$nextFluidCheck = stellaris$fluidTickInterval;
            }

            stellaris$nextFluidCheck--;
        }
    }
}
