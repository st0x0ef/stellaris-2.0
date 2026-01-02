package org.exodusstudio.stellaris.client.renderers.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.module.rocket.RocketModules;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.List;
import java.util.function.Consumer;

public record RocketItemRenderer(Identifier texture, RocketModel model) implements SpecialModelRenderer<List<RocketModule>> {
    @Override
    public void submit(@Nullable List<RocketModule> patterns, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        RocketRenderState modelState = RocketRenderState.create(patterns);
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

        RocketRenderer.RenderingContext renderingContext = new RocketRenderer.RenderingContext(poseStack, modelState.lightCoords, this.model, texture());

        modelState.preRenderModules(renderingContext);

        RenderType renderType = modelState.getRenderType(renderingContext);

        nodeCollector.submitModelPart(this.model.root(), poseStack, renderType, modelState.lightCoords, OverlayTexture.NO_OVERLAY, null);

        modelState.renderModules(renderingContext);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        this.model.root().getExtentsForGui(poseStack, output);
    }

    @Override
    public @Nullable List<RocketModule> extractArgument(ItemStack stack) {
        return stack.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty()).modules;
    }

    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(RocketItemRenderer.Unbaked::texture)
                ).apply(instance, RocketItemRenderer.Unbaked::new)
        );

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new RocketItemRenderer(this.texture,
                    new RocketModel(context.entityModelSet().bakeLayer(RocketModel.LAYER_LOCATION))
            );
        }

        @Override
        public MapCodec<RocketItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}