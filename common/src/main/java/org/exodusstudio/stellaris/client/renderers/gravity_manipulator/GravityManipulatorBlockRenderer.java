package org.exodusstudio.stellaris.client.renderers.gravity_manipulator;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.GravityManipulatorBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jspecify.annotations.Nullable;

public class GravityManipulatorBlockRenderer<T extends GravityManipulatorBlockEntity> implements BlockEntityRenderer<T, GravityManipulatorRenderState> {
    public static final Identifier TEXTURE = IdentifierUtils.texture("block/machines/gravity_manipulator");

    private final GravityManipulatorModel model;

    private final SpriteGetter sprites;

    SpriteId material = new SpriteId(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/machines/gravity_manipulator"));


    public GravityManipulatorBlockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelPart = context.bakeLayer(GravityManipulatorModel.LAYER_LOCATION);
        this.model = new GravityManipulatorModel(modelPart);
        this.sprites = context.sprites();
    }

    @Override
    public void extractRenderState(T blockEntity, GravityManipulatorRenderState renderState, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTicks, cameraPosition, breakProgress);

        Level level = blockEntity.getLevel();

        renderState.facing = blockEntity.getBlockState().getValue(GravityManipulatorBlock.FACING);
        renderState.active = level != null && blockEntity.isActive();

        if (renderState.active) {
            GravityManipulatorModel.animateCore(renderState, level.getGameTime() + partialTicks, blockEntity.getGravity());
        } else {
            GravityManipulatorModel.restCore(renderState);
        }
    }

    @Override
    public void submit(GravityManipulatorRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing.toYRot()));

        nodeCollector.submitModel(this.model, renderState, poseStack,
                material.renderType(RenderTypes::entityCutout), renderState.lightCoords,
                OverlayTexture.NO_OVERLAY, -1, sprites.get(material), 0, null);

        poseStack.popPose();
    }

    @Override
    public GravityManipulatorRenderState createRenderState() {
        return new GravityManipulatorRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public AABB getRenderBoundingBox(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1,
                pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
    }
}
