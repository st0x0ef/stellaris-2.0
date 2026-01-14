package org.exodusstudio.stellaris.neoforge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.exodusstudio.stellaris.client.renderers.space_suit.SpaceSuitModel;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorLayerMixin {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At("HEAD"))
    private void renderArmor(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, HumanoidRenderState humanoidRenderState, float f, float g, CallbackInfo ci) {
        // JET SUIT
        if (Utils.isSpaceSuitPart(humanoidRenderState.feetEquipment)) {
            stellaris$renderArmorPiece(poseStack, nodeCollector, humanoidRenderState.feetEquipment, EquipmentSlot.FEET, packedLight, humanoidRenderState);
        }

        if (Utils.isSpaceSuitPart(humanoidRenderState.legsEquipment)) {
            stellaris$renderArmorPiece(poseStack, nodeCollector, humanoidRenderState.legsEquipment, EquipmentSlot.LEGS, packedLight, humanoidRenderState);
        }

        if (Utils.isSpaceSuitPart(humanoidRenderState.chestEquipment)) {
            stellaris$renderArmorPiece(poseStack, nodeCollector, humanoidRenderState.chestEquipment, EquipmentSlot.CHEST, packedLight, humanoidRenderState);
        }

        if (Utils.isSpaceSuitPart(humanoidRenderState.headEquipment)) {
            stellaris$renderArmorPiece(poseStack, nodeCollector, humanoidRenderState.headEquipment, EquipmentSlot.HEAD, packedLight, humanoidRenderState);
        }
    }

    @Unique
    private void stellaris$renderArmorPiece(PoseStack poseStack, SubmitNodeCollector nodeCollector, ItemStack stack, EquipmentSlot slot, int packedLight, HumanoidRenderState renderState) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && HumanoidArmorLayer.shouldRender(stack, slot)) {
            ModelPart rootPart = Minecraft.getInstance().getEntityModels().bakeLayer(SpaceSuitModel.LAYER_LOCATION);

            SpaceSuitModel model = new SpaceSuitModel(rootPart, slot, stack, null);
            nodeCollector.submitModel(model, renderState, poseStack, RenderTypes.armorTranslucent(SpaceSuitModel.TEXTURE), packedLight, OverlayTexture.NO_OVERLAY, renderState.outlineColor, null);
        }
    }
}
