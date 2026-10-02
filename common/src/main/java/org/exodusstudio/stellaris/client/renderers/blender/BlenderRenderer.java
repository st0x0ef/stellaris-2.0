package org.exodusstudio.stellaris.client.renderers.blender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class BlenderRenderer implements BlockEntityRenderer<BlenderBlockEntity, BlenderRenderer.BlenderState> {

    private final BlockModelResolver blockModelResolver;
    private final ItemModelResolver itemModelResolver;
    private final List<ItemStack> itemStacksSnapshot = new ArrayList<>();


    private final Map<Item, Vector3f> rotationCache = new HashMap<>();


    public BlenderRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
        this.itemModelResolver = context.itemModelResolver();

    }


    @Override
    public @NonNull BlenderState createRenderState() {

        return new BlenderState();
    }

    @Override
    public void submit(BlenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {

        if(state.ingredientsStates.isEmpty()) {
            return;
        }


        int i = 1;
        int renderIndex = 0;
        float y = 1.1f;

        float z = 0.3f;

        for(ItemStackRenderState renderState : state.ingredientsStates) {
            poseStack.pushPose();


            z += i % 4 == 0 ? 0.25f : 0f;
            poseStack.translate(0.3 + (i - 1) * 0.25f, y, z);
            poseStack.scale(0.4f, 0.4f, 0.4f);

            i = i % 4 == 0 ? 1 : i + 1;
            Vector3f rotation = state.ingredientsRotations.get(renderIndex++);
            poseStack.mulPose(Axis.XN.rotation(rotation.x));
            poseStack.mulPose(Axis.YN.rotation(rotation.y));
            poseStack.mulPose(Axis.ZN.rotation(rotation.z));

            renderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }



//
//        for(ItemStackRenderState itemStackRenderState : state.ingredientsStates) {
//            poseStack.pushPose();
//            poseStack.translate(0.15 + x, 1.05 + y, 0.15 + x);
//            poseStack.scale(0.75f, 0.75f, 0.75f);
//            itemStackRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
//            poseStack.popPose();
//
//            x +=  0.25f;
//            i++;
//            if(i % 3 == 0) {
//                y += 0.25f;
//            }
//        }

    }

    @Override
    public void extractRenderState(BlenderBlockEntity blockEntity, @NonNull BlenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);


        Random random = new Random();
        state.ingredientsStates.clear();
        state.ingredientsRotations.clear();
        itemStacksSnapshot.clear();

        for(int i = 0; i < BlenderBlockEntity.INPUT_SLOT_COUNT; i++) {
            ItemStack stack = blockEntity.getItem(i);
            itemStacksSnapshot.add(stack.copy());
            if(stack.isEmpty()) continue;

            ItemStackRenderState itemStackRender = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(itemStackRender, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 1);
            state.ingredientsStates.add(itemStackRender);
            state.ingredientsRotations.add(rotationCache.computeIfAbsent(stack.getItem(),
                    item -> new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat())));
        }
    }

    public class BlenderState extends BlockEntityRenderState {

        public final List<ItemStackRenderState> ingredientsStates = new ArrayList<>();
        public final List<Vector3f> ingredientsRotations = new ArrayList<>();


    }

}
