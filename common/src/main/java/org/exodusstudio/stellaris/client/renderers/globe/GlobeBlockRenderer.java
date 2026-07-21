package org.exodusstudio.stellaris.client.renderers.globe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.blocks.GlobeBlock;
import org.exodusstudio.stellaris.common.blocks.entities.GlobeBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class GlobeBlockRenderer implements BlockEntityRenderer<GlobeBlockEntity, BlockEntityRenderState> {

    public static final SpriteId EARTH_GLOBE = new SpriteId(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/globes/earth_globe"));
    public static final SpriteId MOON_GLOBE = new SpriteId(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/globes/moon_globe"));

    private final GlobeModel model;
    private final SpriteGetter sprites;

    public GlobeBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new GlobeModel(context.bakeLayer(GlobeModel.LAYER_LOCATION));
        this.sprites = context.sprites();
    }

    private static SpriteId spriteFor(BlockState state) {
        if (state.is(BlocksRegistry.MOON_GLOBE.block().get())) {
            return MOON_GLOBE;
        }
        return EARTH_GLOBE;
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (Minecraft.getInstance().level == null
                || !(Minecraft.getInstance().level.getBlockEntity(renderState.blockPos) instanceof GlobeBlockEntity globe)) {
            return;
        }

        BlockState state = globe.getBlockState();
        if (!(state.getBlock() instanceof GlobeBlock)) {
            return;
        }

        Direction direction = state.getValue(GlobeBlock.FACING);
        SpriteId material = spriteFor(state);

        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));

        // Static holder.
        nodeCollector.submitModelPart(this.model.stand(), poseStack, material.renderType(RenderTypes::entityCutout),
                renderState.lightCoords, OverlayTexture.NO_OVERLAY, sprites.get(material));

        // Spinning planet: the rotation is carried by the PoseStack (snapshotted per submit) rather than
        // a shared ModelPart field, so each globe renders its own yaw. Rotate around the planet pivot.
        poseStack.pushPose();
        poseStack.translate(0.0F, GlobeModel.PLANET_PIVOT_Y / 16.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation(globe.getYaw()));
        poseStack.translate(0.0F, -GlobeModel.PLANET_PIVOT_Y / 16.0F, 0.0F);
        nodeCollector.submitModelPart(this.model.planet(), poseStack, material.renderType(RenderTypes::entityCutout),
                renderState.lightCoords, OverlayTexture.NO_OVERLAY, sprites.get(material));
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public AABB getRenderBoundingBox(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1,
                pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
    }
}
