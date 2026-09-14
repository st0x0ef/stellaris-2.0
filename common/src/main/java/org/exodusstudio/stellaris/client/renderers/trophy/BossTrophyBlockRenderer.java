package org.exodusstudio.stellaris.client.renderers.trophy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.BossTrophyBlock;
import org.exodusstudio.stellaris.common.blocks.entities.BossTrophyBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class BossTrophyBlockRenderer implements BlockEntityRenderer<BossTrophyBlockEntity, BossTrophyRenderState> {

    private final Map<TrophyBoss, ModelPart> parts = new EnumMap<>(TrophyBoss.class);
    private final Map<TrophyBoss, TrophyFit> fits = new EnumMap<>(TrophyBoss.class);

    public BossTrophyBlockRenderer(BlockEntityRendererProvider.Context context) {
        for (TrophyBoss boss : TrophyBoss.values()) {
            ModelPart part = boss.bake(context::bakeLayer);
            this.parts.put(boss, part);
            this.fits.put(boss, TrophyFit.resting(part, boss.ignoredParts()));
        }
    }

    @Override
    public void submit(BossTrophyRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing.toYRot()));

        // One renderer instance serves every trophy in the world, so the per-block transform rides the
        // PoseStack; mutating the shared ModelPart would leak between blocks once the submit is flushed.
        this.fits.get(renderState.boss).apply(poseStack);
        nodeCollector.submitModelPart(this.parts.get(renderState.boss), poseStack,
                RenderTypes.entityCutout(renderState.boss.texture()),
                renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }

    @Override
    public void extractRenderState(BossTrophyBlockEntity blockEntity, BossTrophyRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.getValue(BossTrophyBlock.FACING);
        state.boss = blockState.is(BlocksRegistry.STAR_CRAWLER_BOSS_TROPHY.block().get())
                ? TrophyBoss.STAR_CRAWLER_BOSS
                : TrophyBoss.HEART_OF_LUNA;
    }

    @Override
    public BossTrophyRenderState createRenderState() {
        return new BossTrophyRenderState();
    }
}
