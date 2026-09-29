package org.exodusstudio.stellaris.client.renderers.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseFacadeBlockEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FacadeBlockRenderer implements BlockEntityRenderer<BaseFacadeBlockEntity, FacadeBlockRenderer.FacadeBlockRenderState> {

    private final BlockModelResolver blockModelResolver;
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();


    public FacadeBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
    }


    @Override
    public FacadeBlockRenderer.FacadeBlockRenderState createRenderState() {

        return new FacadeBlockRenderState();
    }

    @Override
    public void submit(FacadeBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {

        if(state.facadeState == null) {
            return;
        }


        poseStack.pushPose();
        //poseStack.translate(0.15, 1.05, 0.15);
        //poseStack.scale(0.75f, 0.75f, 0.75f);
        state.facadeRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();


    }

    @Override
    public void extractRenderState(BaseFacadeBlockEntity blockEntity, FacadeBlockRenderer.FacadeBlockRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.facadeState = blockEntity.getFacadeState();

        if(state.facadeState != null) {
            blockModelResolver.update(state.facadeRenderState, state.facadeState, BLOCK_DISPLAY_CONTEXT);
        }
    }

    public static class FacadeBlockRenderState extends BlockEntityRenderState {
        public final BlockModelRenderState facadeRenderState = new BlockModelRenderState();

        public BlockState facadeState = null;
    }

}
