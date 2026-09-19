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
import org.exodusstudio.stellaris.common.data.trophy.BossTrophyData;
import org.exodusstudio.stellaris.common.data.trophy.BossTrophy;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BossTrophyBlockRenderer implements BlockEntityRenderer<BossTrophyBlockEntity, BossTrophyRenderState> {

    private final BlockEntityRendererProvider.Context context;

    public BossTrophyBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    private final Map<BossTrophy, BakedTrophy> bakedTrophies = new HashMap<>();

    @Override
    public void submit(BossTrophyRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing.toYRot()));

        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.boss.rotation().y));
        poseStack.mulPose(Axis.ZP.rotationDegrees(renderState.boss.rotation().z));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.boss.rotation().x));

        BakedTrophy bakedTrophy = this.bakedTrophies.computeIfAbsent(renderState.boss, boss -> {
            ModelPart part = boss.bake(context::bakeLayer);
            return new BakedTrophy(part, TrophyFit.resting(part, boss.ignoredParts()));
        });

        // One renderer instance serves every trophy in the world, so the per-block transform rides the
        // PoseStack; mutating the shared ModelPart would leak between blocks once the submit is flushed.
        bakedTrophy.fit().apply(poseStack);
        nodeCollector.submitModelPart(bakedTrophy.part(), poseStack,
                RenderTypes.entityCutout(renderState.boss.texture()),
                renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }

    @Override
    public void extractRenderState(BossTrophyBlockEntity blockEntity, BossTrophyRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.getValue(BossTrophyBlock.FACING);
        state.boss = BossTrophyData.TROPHY_BOSSES.getOrDefault(blockState.getBlock().arch$registryName(), BossTrophyData.HEART_OF_LUNA);

    }

    @Override
    public BossTrophyRenderState createRenderState() {
        return new BossTrophyRenderState();
    }

    private record BakedTrophy(ModelPart part, TrophyFit fit) {
    }
}
