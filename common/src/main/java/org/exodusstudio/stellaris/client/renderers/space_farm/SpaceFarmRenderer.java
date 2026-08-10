package org.exodusstudio.stellaris.client.renderers.space_farm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.entities.machines.SpaceFarmBlockEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class SpaceFarmRenderer implements BlockEntityRenderer<SpaceFarmBlockEntity, SpaceFarmRenderState> {


    public SpaceFarmRenderer(BlockEntityRendererProvider.Context context) {
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
        poseStack.translate(0.5, 1, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(1/2f, 1/2f, 1/2f);


        ArrayList<BlockStateModelPart> parts = new ArrayList<>();

        ModelManager modelManager = Minecraft.getInstance().getModelManager();

        modelManager.getBlockStateModelSet().get(state.cropState).collectParts(Minecraft.getInstance().level.getRandom(), parts);
        submitNodeCollector.submitBlockModel(
                poseStack,
                RenderTypes.entitySolid(Identifier.parse("minecraft:block/dirt")),
                parts,
                new int[0],
                0xF000F0,
                0xF000F0,
                0xF000F0
        );

        poseStack.popPose();


    }

    @Override
    public void extractRenderState(SpaceFarmBlockEntity blockEntity, @NonNull SpaceFarmRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.cropState = blockEntity.cropState;
    }

}
