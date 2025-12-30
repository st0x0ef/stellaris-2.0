package org.exodusstudio.stellaris.client.renderers.blocks.gravity_manipulator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.GravityManipulatorBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class GravityManipulatorBlockRenderer<T extends GravityManipulatorBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation TEXTURE = ResourceLocationUtils.texture("block/machines/gravity_manipulator");

    private final GravityManipulatorModel<GravityManipulatorBlockEntity> model;

    public GravityManipulatorBlockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelPart = context.bakeLayer(GravityManipulatorModel.LAYER_LOCATION);
        this.model = new GravityManipulatorModel<>(modelPart);
    }

    @Override
    public void render(GravityManipulatorBlockEntity gravityManipulatorBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        Direction direction = gravityManipulatorBlockEntity.getBlockState().getValue(GravityManipulatorBlock.FACING);

        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));

        if (gravityManipulatorBlockEntity.isActive()) {
            this.model.animateBlockCore(partialTick, gravityManipulatorBlockEntity.getGravity());
        }

        this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutout(TEXTURE)), packedLight, packedOverlay);
        poseStack.popPose();
    }
}
