package org.exodusstudio.stellaris.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.client.renderers.space_suit.SpaceSuitModel;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class ArmRendererMixin {
    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, Identifier skinTexture, ModelPart arm, boolean renderSleeve, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);

        if (Utils.isSpaceSuitPart(stack)) {
            ModelLayerLocation layer = SpaceSuitModel.LAYER_LOCATION;
            Identifier texture = SpaceSuitModel.TEXTURE;
            ModelPart rootPart = Minecraft.getInstance().getEntityModels().bakeLayer(layer);
            SpaceSuitModel model = new SpaceSuitModel(rootPart, EquipmentSlot.CHEST, stack, null);

            nodeCollector.submitModelPart(model.getArm(player.getMainArm()), poseStack, RenderTypes.armorTranslucent(texture), packedLight, OverlayTexture.NO_OVERLAY, null);
            ci.cancel();
        }
    }


}
