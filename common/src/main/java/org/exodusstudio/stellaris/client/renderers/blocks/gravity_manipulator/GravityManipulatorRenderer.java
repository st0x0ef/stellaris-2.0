package org.exodusstudio.stellaris.client.renderers.blocks.gravity_manipulator;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;

public class GravityManipulatorRenderer<T extends GravityManipulatorBlockEntity> implements BlockEntityRenderer<T> {

    public  GravityManipulatorRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {

    }
}
