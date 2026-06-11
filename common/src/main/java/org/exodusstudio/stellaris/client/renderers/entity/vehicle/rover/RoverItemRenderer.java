package org.exodusstudio.stellaris.client.renderers.entity.vehicle.rover;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record RoverItemRenderer(RoverModel model) implements SpecialModelRenderer<Void> {

    private static final Identifier TEXTURE = IdentifierUtils.texture("entity/vehicle/rover");

    @Override
    public void submit(@Nullable Void state, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        nodeCollector.submitModelPart(model.root(), poseStack, RenderTypes.entityCutout(TEXTURE), packedLight, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        model.root().getExtentsForGui(poseStack, output);
    }

    @Override
    public @Nullable Void extractArgument(ItemStack stack) {
        return null;
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new RoverItemRenderer(
                    new RoverModel(context.entityModelSet().bakeLayer(RoverModel.LAYER_LOCATION))
            );
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
