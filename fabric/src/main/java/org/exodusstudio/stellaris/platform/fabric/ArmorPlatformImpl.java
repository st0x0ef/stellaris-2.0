package org.exodusstudio.stellaris.platform.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.platform.ArmorPlatform;

public class ArmorPlatformImpl {
    public static void registerArmor(ModelLayerLocation layer, ArmorPlatform.ArmorFactory factory, Identifier texture, Item... items) {
        ArmorRenderer.register((poseStack, nodeCollector, stack, state, slot, packedLight, original) -> {
            ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(layer);
            HumanoidModel<HumanoidRenderState> model = factory.create(root, slot, stack, original);
            nodeCollector.submitModel(model, state, poseStack, RenderTypes.armorTranslucent(texture), packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }, items);
    }
}
