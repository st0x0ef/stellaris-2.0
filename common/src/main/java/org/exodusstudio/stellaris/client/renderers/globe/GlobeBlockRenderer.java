package org.exodusstudio.stellaris.client.renderers.globe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.GlobeBlock;
import org.exodusstudio.stellaris.common.blocks.entities.GlobeBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jspecify.annotations.Nullable;

public class GlobeBlockRenderer implements BlockEntityRenderer<GlobeBlockEntity, GlobeBlockRenderState> {

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
    public void submit(GlobeBlockRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (Minecraft.getInstance().level == null
                || !(Minecraft.getInstance().level.getBlockEntity(renderState.blockPos) instanceof GlobeBlockEntity globe)) {
            return;
        }

        BlockState state = globe.getBlockState();
        if (!(state.getBlock() instanceof GlobeBlock)) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing.toYRot()));

        // Static holder.
        nodeCollector.submitModelPart(this.model.stand(), poseStack, renderState.material.renderType(RenderTypes::entityCutout),
                renderState.lightCoords, OverlayTexture.NO_OVERLAY, sprites.get(renderState.material));

        // Spinning planet: the rotation is carried by the PoseStack (snapshotted per submit) rather than
        // a shared ModelPart field, so each globe renders its own yaw. Rotate around the planet pivot.
        poseStack.pushPose();
        poseStack.translate(0.0F, GlobeModel.PLANET_PIVOT_Y / 16.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation(renderState.yaw));
        poseStack.translate(0.0F, -GlobeModel.PLANET_PIVOT_Y / 16.0F, 0.0F);
        nodeCollector.submitModelPart(this.model.planet(), poseStack, renderState.material.renderType(RenderTypes::entityCutout),
                renderState.lightCoords, OverlayTexture.NO_OVERLAY, sprites.get(renderState.material));
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public void extractRenderState(GlobeBlockEntity blockEntity, GlobeBlockRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.yaw = Mth.lerp(partialTicks,
                blockEntity.getYaw0(),
                blockEntity.getYaw());
        BlockState bState = blockEntity.getBlockState();
        state.facing = bState.getValue(GlobeBlock.FACING);
        state.material = spriteFor(bState);
    }

    @Override
    public GlobeBlockRenderState createRenderState() {
        return new GlobeBlockRenderState();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

}
