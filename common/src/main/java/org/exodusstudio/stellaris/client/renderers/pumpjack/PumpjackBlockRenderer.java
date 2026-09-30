package org.exodusstudio.stellaris.client.renderers.pumpjack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.PumpjackBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.PumpjackBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jspecify.annotations.Nullable;

public class PumpjackBlockRenderer implements BlockEntityRenderer<PumpjackBlockEntity, PumpjackRenderState> {
    private static final float SPEED_SMOOTHING_TICKS = 20.0F;
    private static final float MAX_FRAME_TICKS = 20.0F;

    private static final SpriteId SPRITE = new SpriteId(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/machines/pumpjack"));

    private final PumpjackModel model;
    private final SpriteGetter sprites;

    public PumpjackBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new PumpjackModel(context.bakeLayer(PumpjackModel.LAYER_LOCATION));
        this.sprites = context.sprites();
    }

    @Override
    public void extractRenderState(PumpjackBlockEntity blockEntity, PumpjackRenderState renderState, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTicks, cameraPosition, breakProgress);

        BlockState state = blockEntity.getBlockState();
        Level level = blockEntity.getLevel();
        float now = (level == null ? 0L : level.getGameTime()) + partialTicks;

        advanceAnimation(blockEntity, state.getValue(PumpjackBlock.LIT), now);
        renderState.animationMillis = (long) (blockEntity.animationTicks * 50.0F);
        renderState.facing = state.getValue(PumpjackBlock.FACING);
    }

    private static void advanceAnimation(PumpjackBlockEntity blockEntity, boolean lit, float now) {
        float delta = Mth.clamp(now - blockEntity.lastAnimationFrame, 0.0F, MAX_FRAME_TICKS);
        blockEntity.lastAnimationFrame = now;

        float target = lit ? 1.0F : 0.0F;
        blockEntity.animationSpeed += (target - blockEntity.animationSpeed) * (1.0F - (float) Math.exp(-delta / SPEED_SMOOTHING_TICKS));
        blockEntity.animationTicks = (blockEntity.animationTicks + blockEntity.animationSpeed * delta) % (PumpjackAnimations.WORKING.lengthInSeconds() * 20.0F);
    }

    @Override
    public void submit(PumpjackRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - renderState.facing.toYRot()));
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        nodeCollector.submitModel(this.model, renderState, poseStack, SPRITE.renderType(RenderTypes::entityCutoutCull), renderState.lightCoords, OverlayTexture.NO_OVERLAY, -1, sprites.get(SPRITE), 0, renderState.breakProgress);

        poseStack.popPose();
    }

    @Override
    public PumpjackRenderState createRenderState() {
        return new PumpjackRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public AABB getRenderBoundingBox(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
    }
}
