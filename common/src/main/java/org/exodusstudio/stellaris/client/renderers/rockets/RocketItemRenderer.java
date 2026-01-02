package org.exodusstudio.stellaris.client.renderers.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.client.models.rockets.RocketModel;
import org.exodusstudio.stellaris.client.models.rockets.RocketModelState;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.module.rocket.RocketModules;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Set;

public record RocketItemRenderer(ResourceLocation texture, RocketModel model) implements SpecialModelRenderer<List<RocketModule>> {

    @Override
    public void render(@Nullable List<RocketModule> patterns, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        RocketModelState modelState = RocketModelState.create(patterns);
        ResourceLocation defaultTexture = texture();
        poseStack.pushPose();

        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(0.3F, 0.3F, 0.3F);
        poseStack.translate(1.5D, 1.0D, -1.0D);

        //Items in GUI and FIXED (Item frame) context need special positioning
        if(displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.FIXED) {
            poseStack.translate(-0.5D, -2.0D, -0.5D);
            poseStack.scale(0.6F, 0.6F, 0.6F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        }

        this.model.setDefaultModel();

        modelState.preRenderModules(new RocketRenderer.RenderingContext(poseStack, bufferSource, packedLight, this.model, defaultTexture));

        RenderType renderType = modelState.getRenderType(new RocketRenderer.RenderingContext(poseStack, bufferSource, packedLight, this.model, defaultTexture));
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        modelState.renderModules(new RocketRenderer.RenderingContext(poseStack, bufferSource, packedLight, this.model, defaultTexture));

        poseStack.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        this.model.root().getExtentsForGui(poseStack, output);
    }

    @Override
    public @Nullable List<RocketModule> extractArgument(ItemStack stack) {
        return stack.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty()).modules;
    }

    public record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(RocketItemRenderer.Unbaked::texture)
                ).apply(instance, RocketItemRenderer.Unbaked::new)
        );

        @Override
        public @NotNull RocketItemRenderer bake(EntityModelSet modelSet) {
            return new RocketItemRenderer(this.texture,
                    new RocketModel(modelSet.bakeLayer(RocketModel.LAYER_LOCATION))
            );
        }

        @Override
        public MapCodec<RocketItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}