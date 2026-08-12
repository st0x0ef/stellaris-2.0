package org.exodusstudio.stellaris.client.renderers.space_farm;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.entities.machines.SpaceFarmBlockEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SpaceFarmRenderer implements BlockEntityRenderer<SpaceFarmBlockEntity, SpaceFarmRenderState> {

    private final BlockModelResolver blockModelResolver;
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();


    public SpaceFarmRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();

    }


    @Override
    public @NonNull SpaceFarmRenderState createRenderState() {

        return new SpaceFarmRenderState();
    }

    @Override
    public void submit(SpaceFarmRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {

        if(state.cropState == null) {
            return;
        }


        poseStack.pushPose();
        poseStack.translate(0.15, 1.05, 0.15);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        state.cropRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();


    }

    @Override
    public void extractRenderState(SpaceFarmBlockEntity blockEntity, @NonNull SpaceFarmRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.cropState = blockEntity.cropState;

        //state.cropState = Blocks.WHEAT.defaultBlockState();

        if(state.cropState != null) {

            blockModelResolver.update(state.cropRenderState, state.cropState, BLOCK_DISPLAY_CONTEXT);
        }
    }

}
