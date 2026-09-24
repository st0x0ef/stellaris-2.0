package org.exodusstudio.stellaris.client.renderers.sliding_door;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.SlidingDoorBlock;
import org.exodusstudio.stellaris.common.blocks.entities.SlidingDoorBlockEntity;
import org.jspecify.annotations.Nullable;

public class SlidingDoorRenderer implements BlockEntityRenderer<SlidingDoorBlockEntity, SlidingDoorRenderState> {
    private static final float WIDTH = 16, HEIGHT = 32, Z_MIN = 7, Z_MAX = 9;

    private static final float[] UV_NORTH = {0.5F, 0.5F, 4.5F, 8.5F};
    private static final float[] UV_EAST = {0, 0.5F, 0.5F, 8.5F};
    private static final float[] UV_SOUTH = {9, 0.5F, 5, 8.5F};
    private static final float[] UV_WEST = {4.5F, 0.5F, 5, 8.5F};
    private static final float[] UV_UP = {4.5F, 0.5F, 0.5F, 0};
    private static final float[] UV_DOWN = {8.5F, 0, 4.5F, 0.5F};

    private final SpriteGetter sprites;

    public SlidingDoorRenderer(BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
    }

    @Override
    public SlidingDoorRenderState createRenderState() {
        return new SlidingDoorRenderState();
    }

    @Override
    public void extractRenderState(SlidingDoorBlockEntity door, SlidingDoorRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(door, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = door.getBlockState();
        state.facing = blockState.getValue(DoorBlock.FACING);
        state.slidesLeft = door.getLevel() != null && SlidingDoorBlock.getSlideDirection(door.getLevel(), door.getBlockPos(), blockState) == state.facing.getCounterClockWise();
        state.openness = door.getOpenness(partialTicks);
        state.sprite = new SpriteId(TextureAtlas.LOCATION_BLOCKS, BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).withPrefix("block/"));
    }

    @Override
    public void submit(SlidingDoorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float slide = state.openness * WIDTH;
        if (slide >= WIDTH) return;

        TextureAtlasSprite sprite = sprites.get(state.sprite);
        boolean left = state.slidesLeft;
        float shift = left ? -slide : slide;
        float visibleMin = left ? slide : 0;
        float visibleMax = left ? WIDTH : WIDTH - slide;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - state.facing.toYRot()));
        poseStack.translate(-0.5F, 0, -0.5F);
        poseStack.scale(1 / 16F, 1 / 16F, 1 / 16F);

        collector.submitCustomGeometry(poseStack, state.sprite.renderType(RenderTypes::entityCutout), (pose, vertices) ->
                panel(pose, vertices, sprite, state.lightCoords, visibleMin, visibleMax, shift));

        poseStack.popPose();
    }

    private static void panel(PoseStack.Pose pose, VertexConsumer out, TextureAtlasSprite sprite, int light, float min, float max, float shift) {
        float x0 = min + shift, x1 = max + shift;
        float uMinX = (min / WIDTH), uMaxX = (max / WIDTH);

        Face north = new Face(pose, out, sprite, light, UV_NORTH, 0, 0, -1);
        north.vertex(x1, HEIGHT, Z_MIN, 1 - uMaxX, 0);
        north.vertex(x1, 0, Z_MIN, 1 - uMaxX, 1);
        north.vertex(x0, 0, Z_MIN, 1 - uMinX, 1);
        north.vertex(x0, HEIGHT, Z_MIN, 1 - uMinX, 0);

        Face south = new Face(pose, out, sprite, light, UV_SOUTH, 0, 0, 1);
        south.vertex(x0, HEIGHT, Z_MAX, uMinX, 0);
        south.vertex(x0, 0, Z_MAX, uMinX, 1);
        south.vertex(x1, 0, Z_MAX, uMaxX, 1);
        south.vertex(x1, HEIGHT, Z_MAX, uMaxX, 0);

        Face east = new Face(pose, out, sprite, light, UV_EAST, 1, 0, 0);
        east.vertex(x1, HEIGHT, Z_MAX, 0, 0);
        east.vertex(x1, 0, Z_MAX, 0, 1);
        east.vertex(x1, 0, Z_MIN, 1, 1);
        east.vertex(x1, HEIGHT, Z_MIN, 1, 0);

        Face west = new Face(pose, out, sprite, light, UV_WEST, -1, 0, 0);
        west.vertex(x0, HEIGHT, Z_MIN, 0, 0);
        west.vertex(x0, 0, Z_MIN, 0, 1);
        west.vertex(x0, 0, Z_MAX, 1, 1);
        west.vertex(x0, HEIGHT, Z_MAX, 1, 0);

        Face up = new Face(pose, out, sprite, light, UV_UP, 0, 1, 0);
        up.vertex(x0, HEIGHT, Z_MIN, uMinX, 0);
        up.vertex(x0, HEIGHT, Z_MAX, uMinX, 1);
        up.vertex(x1, HEIGHT, Z_MAX, uMaxX, 1);
        up.vertex(x1, HEIGHT, Z_MIN, uMaxX, 0);

        Face down = new Face(pose, out, sprite, light, UV_DOWN, 0, -1, 0);
        down.vertex(x1, 0, Z_MIN, uMaxX, 1);
        down.vertex(x1, 0, Z_MAX, uMaxX, 0);
        down.vertex(x0, 0, Z_MAX, uMinX, 0);
        down.vertex(x0, 0, Z_MIN, uMinX, 1);
    }

    private record Face(PoseStack.Pose pose, VertexConsumer out, TextureAtlasSprite sprite, int light, float[] uv, float nx, float ny, float nz) {
        void vertex(float x, float y, float z, float tu, float tv) {
            float u = uv[0] + (uv[2] - uv[0]) * tu;
            float v = uv[1] + (uv[3] - uv[1]) * tv;
            out.addVertex(pose, x, y, z)
                    .setColor(-1)
                    .setUv(sprite.getU(u / 16F), sprite.getV(v / 16F))
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(pose, nx, ny, nz);
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public AABB getRenderBoundingBox(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }
}
