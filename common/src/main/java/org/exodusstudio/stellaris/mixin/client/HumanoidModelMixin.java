package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.exodusstudio.stellaris.common.items.RocketItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
    // models/item/rocket.json's third-person transforms are solved against this exact angle
    @Unique
    private static final float stellaris$ROCKET_HOLD_X_ROT = -2.566F;

    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void stellaris$holdRocketOverhead(HumanoidRenderState state, CallbackInfo ci) {
        if (state instanceof AvatarRenderState
                && (state.rightHandItemStack.getItem() instanceof RocketItem || state.leftHandItemStack.getItem() instanceof RocketItem)) {
            this.rightArm.setRotation(stellaris$ROCKET_HOLD_X_ROT, 0.0F, 0.0F);
            this.leftArm.setRotation(stellaris$ROCKET_HOLD_X_ROT, 0.0F, 0.0F);
        }
    }
}
