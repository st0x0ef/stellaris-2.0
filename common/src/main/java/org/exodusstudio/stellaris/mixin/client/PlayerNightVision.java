package org.exodusstudio.stellaris.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class PlayerNightVision extends AbstractClientPlayer  {


    public PlayerNightVision(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    public void stellaris$onPlayerTick(CallbackInfo ci) {
        ItemStack helmetStack = this.getItemBySlot(EquipmentSlot.HEAD);
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;

        Identifier postProcessing = IdentifierUtils.id("night_vision");

        if(helmetStack.getOrDefault(DataComponentsRegistry.NIGHT_VISION.get(), false)) {

            if(renderer.currentPostEffect() == null || !renderer.currentPostEffect().equals(postProcessing) ) {
                renderer.setPostEffect(postProcessing);
            }
        } else {
            if(renderer.currentPostEffect() != null && renderer.currentPostEffect().equals(postProcessing)) {
                renderer.clearPostEffect();
            }
        }
    }
}
