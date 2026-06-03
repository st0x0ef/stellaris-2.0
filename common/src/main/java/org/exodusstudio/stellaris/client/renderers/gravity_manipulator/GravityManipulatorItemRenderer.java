package org.exodusstudio.stellaris.client.renderers.gravity_manipulator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record GravityManipulatorItemRenderer(Identifier texture, GravityManipulatorModel model) implements NoDataSpecialModelRenderer {

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        model.animateItemCore(1f / Minecraft.getInstance().getFps());

        nodeCollector.submitModelPart(this.model.root(), poseStack, RenderTypes.entityCutoutCull(GravityManipulatorBlockRenderer.TEXTURE), packedLight, packedOverlay, null);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<GravityManipulatorItemRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(GravityManipulatorItemRenderer.Unbaked::texture)
                ).apply(instance, GravityManipulatorItemRenderer.Unbaked::new)
        );

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new GravityManipulatorItemRenderer(
                    this.texture,
                    new GravityManipulatorModel(context.entityModelSet().bakeLayer(GravityManipulatorModel.LAYER_LOCATION))
            );
        }

        @Override
        public MapCodec<GravityManipulatorItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
