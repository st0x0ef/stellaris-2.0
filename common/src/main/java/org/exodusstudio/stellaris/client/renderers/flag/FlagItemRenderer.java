package org.exodusstudio.stellaris.client.renderers.flag;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public record FlagItemRenderer(ResourceLocation texture, FlagBlockModel baseModel) implements SpecialModelRenderer<ResourceLocation> {
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocationUtils.texture("block/flag/flag");

    @Override
    public void render(ResourceLocation texture, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(-0.5D, -1.5, 0.5D);

        baseModel.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutout(texture)), packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        baseModel.root().getExtentsForGui(poseStack, output);
    }

    @Override
    public @Nullable ResourceLocation extractArgument(ItemStack stack) {
        if (stack.has(DataComponents.BASE_COLOR)) {
            return ResourceLocationUtils.texture("block/flag/flag_" + stack.get(DataComponents.BASE_COLOR).getName());
        }
        return DEFAULT_TEXTURE;
    }

    public record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<FlagItemRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(FlagItemRenderer.Unbaked::texture)
                ).apply(instance, FlagItemRenderer.Unbaked::new)
        );

        @Override
        public @NotNull SpecialModelRenderer<ResourceLocation> bake(EntityModelSet modelSet) {
            return new FlagItemRenderer(
                    this.texture,
                    new FlagBlockModel(modelSet.bakeLayer(FlagBlockModel.LAYER_LOCATION))
            );
        }

        @Override
        public MapCodec<FlagItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
