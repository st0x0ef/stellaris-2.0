package org.exodusstudio.stellaris.client.renderers.launchpad;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.blocks.GravityManipulatorBlock;
import org.exodusstudio.stellaris.common.blocks.RocketLaunchPadBlock;
import org.exodusstudio.stellaris.common.blocks.entities.RocketLaunchPadBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class RocketLaunchPadBlockRenderer<T extends RocketLaunchPadBlockEntity> implements BlockEntityRenderer<T, BlockEntityRenderState> {
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
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(renderState.blockPos) instanceof  RocketLaunchPadBlockEntity rocketLaunchPadBlockEntity) {
            Direction direction = rocketLaunchPadBlockEntity.getBlockState().getValue(GravityManipulatorBlock.FACING);
            boolean towers = rocketLaunchPadBlockEntity.getBlockState().getValue(RocketLaunchPadBlock.TOWERS);

            model.setTowersVisible(towers);
            model.setBaseVisible(true);
            model.setBarsAngle(rocketLaunchPadBlockEntity.getBarAngle());

            poseStack.pushPose();

            poseStack.translate(0.5D, 1.625D, 0.5D);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));

            nodeCollector.submitModelPart(this.model.root(), poseStack, material.renderType(RenderTypes::entityCutout), renderState.lightCoords, OverlayTexture.NO_OVERLAY, sprites.get(material));
            poseStack.popPose();
        }
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }
}
