package org.exodusstudio.stellaris.client.renderers.trophy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.data.trophy.BossTrophyData;
import org.exodusstudio.stellaris.common.data.trophy.BossTrophy;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record BossTrophyItemRenderer(BossTrophy boss, ModelPart part, TrophyFit fit) implements NoDataSpecialModelRenderer {

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

    public static class Unbaked implements SpecialModelRenderer.Unbaked {


        public static final MapCodec<BossTrophyItemRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("id").forGetter(unbaked -> unbaked.bossId)
                ).apply(instance, Unbaked::new)
        );

        private final Identifier bossId;

        public Unbaked(Identifier bossId) {
            this.bossId = bossId;
        }

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            BossTrophy boss = BossTrophyData.TROPHY_BOSSES.getOrDefault(this.bossId, BossTrophyData.HEART_OF_LUNA);
            ModelPart part = boss.bake(context.entityModelSet()::bakeLayer);
            return new BossTrophyItemRenderer(boss, part, TrophyFit.centred(part, boss.ignoredParts()));
        }

        @Override
        public MapCodec<BossTrophyItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
