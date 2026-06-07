package org.exodusstudio.stellaris.client.renderers.rockets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.client.renderers.rockets.models.RocketModelRegistry;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.List;
import java.util.function.Consumer;

public record RocketItemRenderer(Identifier texture, boolean gui) implements SpecialModelRenderer<List<RocketModule>> {
    @Override
    public void submit(@Nullable List<RocketModule> rocketModules, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        RocketRenderState modelState = RocketRenderState.create(rocketModules);
        poseStack.pushPose();

        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(0.3F, 0.3F, 0.3F);
        poseStack.translate(1.5D, 1.0D, -1.0D);

        //Items in GUI and FIXED (Item frame) context need special positioning
        if(gui) {
            poseStack.translate(-0.5D, -2.0D, -0.5D);
            poseStack.scale(0.45F, 0.45F, 0.45F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        }

        boolean rocketModelPresent = false;
        for (RocketModule module : modelState.modules) {
            if (module.getRocketFeature() == RocketModule.RocketFeature.MODEL) {
                rocketModelPresent = true;
            }
        }

        RocketRenderer.RenderingContext renderingContext = new RocketRenderer.RenderingContext(rocketModules, poseStack, packedLight);
        RenderType renderType = RocketRenderer.getRenderType(renderingContext);
        if  (!rocketModelPresent) {
            nodeCollector.submitModelPart(RocketModelRegistry.create("tiny", Minecraft.getInstance().getEntityModels()).root(), poseStack, renderType, packedLight, OverlayTexture.NO_OVERLAY, null);
        }
        modelState.preRenderModules(nodeCollector, poseStack, renderingContext, renderType);
        modelState.renderModules(renderingContext);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.scale(1.0F, -1.0F, -1.0F);
    }

    @Override
    public @Nullable List<RocketModule> extractArgument(ItemStack stack) {
        return stack.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty()).modules;
    }

    public record Unbaked(Identifier texture, boolean gui) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(RocketItemRenderer.Unbaked::texture),
                        Codec.BOOL.optionalFieldOf("gui", false).forGetter(RocketItemRenderer.Unbaked::gui)
                ).apply(instance, RocketItemRenderer.Unbaked::new)
        );

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new RocketItemRenderer(this.texture, this.gui);
        }

        @Override
        public MapCodec<RocketItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
