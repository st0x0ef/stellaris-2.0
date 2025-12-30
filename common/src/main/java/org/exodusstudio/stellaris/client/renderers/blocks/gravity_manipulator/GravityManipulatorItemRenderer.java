package org.exodusstudio.stellaris.client.renderers.blocks.gravity_manipulator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Set;

public record GravityManipulatorItemRenderer(ResourceLocation texture, GravityManipulatorModel<GravityManipulatorBlockEntity> model) implements NoDataSpecialModelRenderer {

    @Override
    public void render(ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        model.animateItemCore(Minecraft.getInstance().getFrameTimeNs() / 1000000f);

        this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutout(GravityManipulatorBlockRenderer.TEXTURE)), packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> output) {

    }

    public record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<GravityManipulatorItemRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(GravityManipulatorItemRenderer.Unbaked::texture)
                ).apply(instance, GravityManipulatorItemRenderer.Unbaked::new)
        );

        @Override
        public @NotNull NoDataSpecialModelRenderer bake(EntityModelSet modelSet) {
            return new GravityManipulatorItemRenderer(
                    this.texture,
                    new GravityManipulatorModel<>(modelSet.bakeLayer(GravityManipulatorModel.LAYER_LOCATION))
            );
        }

        @Override
        public MapCodec<GravityManipulatorItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
