package org.exodusstudio.stellaris.client.renderers.blender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.client.renderers.gravity_manipulator.GravityManipulatorModel;
import org.exodusstudio.stellaris.common.blocks.BlenderBlock;
import org.exodusstudio.stellaris.common.blocks.GravityManipulatorBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class BlenderBlockRenderer implements BlockEntityRenderer<BlenderBlockEntity, BlenderBlockRenderer.BlenderState> {

    private final ItemModelResolver itemModelResolver;

    private final Map<Item, Vector3f> rotationCache = new HashMap<>();
    private float rotationSpeed = 0.1f; // Adjust this value to control the rotation speed
    private float rotation = 0.0f;

    private static float MAX_ROTATION_SPEED = 4.0f; // Maximum rotation speed

    private final BlenderBlockModel model;
    private final SpriteGetter sprites;

    SpriteId material = new SpriteId(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/machines/blender"));


    public BlenderBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();

        ModelPart modelPart = context.bakeLayer(BlenderBlockModel.LAYER_LOCATION);
        this.model = new BlenderBlockModel(modelPart);
        this.sprites = context.sprites();
    }


    @Override
    public @NonNull BlenderState createRenderState() {
        return new BlenderState();
    }

    @Override
    public void submit(BlenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.facing.toYRot()));

        submitNodeCollector.submitModel(this.model, state, poseStack,
                material.renderType(RenderTypes::entityCutout), state.lightCoords,
                OverlayTexture.NO_OVERLAY, -1, sprites.get(material), 0, null);

        poseStack.popPose();



        if(state.ingredientsStates.isEmpty()) {
            return;
        }

        poseStack.pushPose();


        int itemCount = state.ingredientsStates.size();

        int columns = itemCount <= 2 ? itemCount : 3;
        int rows = (itemCount + columns - 1) / columns;

        float scale = switch (itemCount) {
            case 1 -> 0.6f;
            case 2 -> 0.5f;
            default -> 0.3f;
        };
        float spacing = switch (itemCount) {
            case 1 -> 0.0f;
            case 2 -> 0.28f;
            default -> 0.19f;
        };
        poseStack.rotateAround(Axis.YN.rotation(rotation / 100.0f), 0.5f, 1.1f, 0.5f);

        for (int index = 0; index < itemCount; index++) {

            IngredientRenderData renderData = state.ingredientsStates.get(index);

            int column = index % columns;
            int row = index / columns;

            float gridX = (column - (columns - 1) / 2.0f) * spacing;
            float gridZ = (row - (rows - 1) / 2.0f) * spacing;

            Random random = new Random(31L * renderData.slot() + renderData.stack().getItem().hashCode());

            float jitter = itemCount <= 2 ? 0.035f : 0.02f;
            float offsetX = (random.nextFloat() - 0.5f) * jitter;
            float offsetZ = (random.nextFloat() - 0.5f) * jitter;

            poseStack.pushPose();


            poseStack.translate(0.5f + gridX + offsetX, 1.1f, 0.5f + gridZ + offsetZ);
            poseStack.scale(scale, scale, scale);

            Vector3f rotation = this.rotationCache.get(renderData.stack().getItem());
            poseStack.mulPose(Axis.XN.rotation(rotation.x));
            poseStack.mulPose(Axis.YN.rotation(rotation.y));
            poseStack.mulPose(Axis.ZN.rotation(rotation.z));

            renderData.renderState().submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        state.bladeRotation = this.rotation;
        if(state.isBlending) {
            this.rotationSpeed = Mth.lerp(0.005f, this.rotationSpeed, MAX_ROTATION_SPEED); // Smoothly increase rotation speed when blending
        } else {
            this.rotationSpeed = Mth.lerp(0.005f, this.rotationSpeed, 0.1f); // Smoothly reset to normal speed when not blending
        }
        this.rotation += this.rotationSpeed;
        poseStack.popPose();

    }

    @Override
    public void extractRenderState(BlenderBlockEntity blockEntity, @NonNull BlenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.ingredientsStates.clear();
        state.isBlending = blockEntity.isBlending();
        state.facing = blockEntity.getBlockState().getValue(BlenderBlock.FACING);
        Random random = new Random();

        for(int i = 0; i < BlenderBlockEntity.INPUT_SLOT_COUNT; i++) {
            ItemStack stack = blockEntity.getItem(i);
            if(stack.isEmpty()) continue;

            IngredientRenderData ingredientRenderData = new IngredientRenderData(new ItemStackRenderState(), stack, i);
            this.itemModelResolver.updateForTopItem(ingredientRenderData.renderState(), stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 1);
            state.ingredientsStates.add(ingredientRenderData);
            rotationCache.computeIfAbsent(stack.getItem(),
                    item -> new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()));
        }

    }

    public record IngredientRenderData(ItemStackRenderState renderState, ItemStack stack, int slot) {}

    public class BlenderState extends BlockEntityRenderState {

        public final List<IngredientRenderData> ingredientsStates = new ArrayList<>();
        public boolean isBlending = false;
        public float bladeRotation = 0.0f;
        public Direction facing = Direction.NORTH;


    }

}
