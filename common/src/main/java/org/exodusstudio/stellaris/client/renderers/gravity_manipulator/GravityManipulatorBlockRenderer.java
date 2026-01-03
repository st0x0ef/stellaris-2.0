package org.exodusstudio.stellaris.client.renderers.gravity_manipulator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.blocks.GravityManipulatorBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class GravityManipulatorBlockRenderer<T extends GravityManipulatorBlockEntity> implements BlockEntityRenderer<T, BlockEntityRenderState> {
    public static final Identifier TEXTURE = IdentifierUtils.texture("block/machines/gravity_manipulator");

    private final GravityManipulatorModel model;

    private final MaterialSet materialSet;

    Material material = new Material(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/machines/gravity_manipulator"));


    public GravityManipulatorBlockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelPart = context.bakeLayer(GravityManipulatorModel.LAYER_LOCATION);
        this.model = new GravityManipulatorModel(modelPart);
        this.materialSet = context.materials();
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(renderState.blockPos) instanceof  GravityManipulatorBlockEntity gravityManipulatorBlockEntity) {
            Direction direction = gravityManipulatorBlockEntity.getBlockState().getValue(GravityManipulatorBlock.FACING);

            poseStack.pushPose();

            poseStack.translate(0.5D, 1.5D, 0.5D);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));

            if (gravityManipulatorBlockEntity.isActive()) {
                this.model.animateBlockCore(1f / Minecraft.getInstance().getFps(), gravityManipulatorBlockEntity.getGravity());
            }

            nodeCollector.submitModelPart(this.model.root(), poseStack, material.renderType(RenderTypes::entityCutout), renderState.lightCoords, OverlayTexture.NO_OVERLAY, materialSet.get(material));
            poseStack.popPose();
        }
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }
}
