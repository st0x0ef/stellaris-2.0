package org.exodusstudio.stellaris.client.renderers.launchpad;

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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.RocketLaunchPadBlock;
import org.exodusstudio.stellaris.common.blocks.entities.RocketLaunchPadBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jspecify.annotations.Nullable;

public class RocketLaunchPadBlockRenderer<T extends RocketLaunchPadBlockEntity> implements BlockEntityRenderer<T, RocketLaunchPadRenderState> {
    public static final Identifier TEXTURE = IdentifierUtils.texture("block/rocket_launch_pad");

    private final RocketLaunchPadModel model;

    private final SpriteGetter sprites;

    SpriteId material = new SpriteId(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/rocket_launch_pad"));


    public RocketLaunchPadBlockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelPart = context.bakeLayer(RocketLaunchPadModel.LAYER_LOCATION);
        this.model = new RocketLaunchPadModel(modelPart);
        this.sprites = context.sprites();
    }

    @Override
    public void extractRenderState(T blockEntity, RocketLaunchPadRenderState renderState, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTicks, cameraPosition, breakProgress);

        BlockState state = blockEntity.getBlockState();

        renderState.facing = state.getValue(RocketLaunchPadBlock.FACING);
        renderState.towers = state.getValue(RocketLaunchPadBlock.TOWERS);
        renderState.antenna = state.getValue(RocketLaunchPadBlock.ANTENNA);
        renderState.barAngle = blockEntity.getBarAngle();
    }

    @Override
    public void submit(RocketLaunchPadRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.625D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing.toYRot()));

        nodeCollector.submitModel(this.model, renderState, poseStack,
                material.renderType(RenderTypes::entityCutout), renderState.lightCoords,
                OverlayTexture.NO_OVERLAY, -1, sprites.get(material), 0, null);

        poseStack.popPose();
    }

    @Override
    public RocketLaunchPadRenderState createRenderState() {
        return new RocketLaunchPadRenderState();
    }

    @Override
    public boolean shouldRender(T blockEntity, Vec3 cameraPosition) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public AABB getRenderBoundingBox(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - 3, pos.getY() - 1, pos.getZ() - 3,
                pos.getX() + 4, pos.getY() + 10, pos.getZ() + 4);
    }
}
