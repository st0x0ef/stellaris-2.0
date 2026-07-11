package org.exodusstudio.stellaris.client.renderers.globe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record GlobeItemRenderer(Identifier texture, GlobeModel model) implements NoDataSpecialModelRenderer {

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        var renderType = RenderTypes.entityCutoutCull(IdentifierUtils.texture(this.texture.getPath()));

        // Static holder.
        nodeCollector.submitModelPart(this.model.stand(), poseStack, renderType,
                packedLight, OverlayTexture.NO_OVERLAY, null);

        // Spinning planet: rotation carried by the PoseStack (not the shared baked model), driven by a
        // continuous wall-clock angle (~1 rad/s) so every rendered globe item spins smoothly on its own.
        float angle = (float) ((System.currentTimeMillis() / 1000.0) % (Math.PI * 2.0));
        poseStack.pushPose();
        poseStack.translate(0.0F, GlobeModel.PLANET_PIVOT_Y / 16.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation(angle));
        poseStack.translate(0.0F, -GlobeModel.PLANET_PIVOT_Y / 16.0F, 0.0F);
        nodeCollector.submitModelPart(this.model.planet(), poseStack, renderType,
                packedLight, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<GlobeItemRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(GlobeItemRenderer.Unbaked::texture)
                ).apply(instance, GlobeItemRenderer.Unbaked::new)
        );

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new GlobeItemRenderer(
                    this.texture,
                    new GlobeModel(context.entityModelSet().bakeLayer(GlobeModel.LAYER_LOCATION))
            );
        }

        @Override
        public MapCodec<GlobeItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
