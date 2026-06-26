package org.exodusstudio.stellaris.client.renderers.launchpad;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record RocketLaunchPadItemRenderer(Identifier texture, RocketLaunchPadModel model, boolean towers) implements NoDataSpecialModelRenderer {

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();

        if (this.towers) {
            poseStack.translate(0.5D, 0.6D, 0.5D);
            poseStack.scale(-0.375F, -0.375F, 0.375F);
        } else {
            poseStack.translate(0.5D, 1D, 0.5D);
            poseStack.scale(-0.45F, -0.45F, 0.45F);
        }

        this.model.setTowersVisible(this.towers);
        this.model.setBaseVisible(!this.towers);

        nodeCollector.submitModelPart(this.model.root(), poseStack, RenderTypes.entityCutoutCull(RocketLaunchPadBlockRenderer.TEXTURE), packedLight, packedOverlay, null);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 1.5D, 0.0D);

        if (this.towers) {
            model.getTowerExtents(poseStack, output);
        } else {
            model.platform().getExtentsForGui(poseStack, output);
        }
    }

    public record Unbaked(Identifier texture, boolean towers) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
                        Codec.BOOL.optionalFieldOf("towers", false).forGetter(Unbaked::towers)
                ).apply(instance, Unbaked::new)
        );

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new RocketLaunchPadItemRenderer(
                    this.texture,
                    new RocketLaunchPadModel(context.entityModelSet().bakeLayer(RocketLaunchPadModel.LAYER_LOCATION)),
                    this.towers
            );
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
