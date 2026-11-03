package org.exodusstudio.stellaris.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.items.RocketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {
    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void stellaris$hideItemBesideRocket(ArmedEntityRenderState state, ItemStackRenderState itemState, ItemStack stack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, CallbackInfo ci) {
        if (!(state instanceof AvatarRenderState)) {
            return;
        }

        ItemStack otherStack = arm == HumanoidArm.RIGHT ? state.leftHandItemStack : state.rightHandItemStack;
        if (otherStack.getItem() instanceof RocketItem && (!(stack.getItem() instanceof RocketItem) || arm != state.mainArm)) {
            ci.cancel();
        }
    }
}
