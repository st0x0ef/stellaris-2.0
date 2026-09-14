package org.exodusstudio.stellaris.client.renderers.trophy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record BossTrophyItemRenderer(TrophyBoss boss, ModelPart part, TrophyFit fit) implements NoDataSpecialModelRenderer {

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.fit.apply(poseStack);

        nodeCollector.submitModelPart(this.part, poseStack, RenderTypes.entityCutout(this.boss.texture()),
                packedLight, packedOverlay, null);

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        // Must mirror submit() exactly, or the GUI frames the item against a box the model never occupies.
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.fit.apply(poseStack);
        this.part.getExtentsForGui(poseStack, output);
    }

    public record Unbaked(TrophyBoss boss) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<BossTrophyItemRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        TrophyBoss.CODEC.fieldOf("boss").forGetter(BossTrophyItemRenderer.Unbaked::boss)
                ).apply(instance, BossTrophyItemRenderer.Unbaked::new)
        );

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            ModelPart part = this.boss.bake(context.entityModelSet()::bakeLayer);
            return new BossTrophyItemRenderer(this.boss, part, TrophyFit.centred(part, this.boss.ignoredParts()));
        }

        @Override
        public MapCodec<BossTrophyItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
