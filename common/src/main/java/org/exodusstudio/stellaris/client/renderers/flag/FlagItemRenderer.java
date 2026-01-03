package org.exodusstudio.stellaris.client.renderers.flag;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record FlagItemRenderer(Identifier texture, FlagBlockModel baseModel) implements SpecialModelRenderer<Identifier> {
    private static final Identifier DEFAULT_TEXTURE = IdentifierUtils.texture("block/flag/flag");


    @Override
    public void submit(Identifier texture, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(-0.5D, -1.5, 0.5D);

        nodeCollector.submitModelPart(baseModel.root(), poseStack, RenderTypes.entityCutout(texture), packedLight, packedOverlay, null);

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        baseModel.root().getExtentsForGui(poseStack, output);
    }

    @Override
    public @Nullable Identifier extractArgument(ItemStack stack) {
        if (stack.has(DataComponents.BASE_COLOR)) {
            return IdentifierUtils.texture("block/flag/flag_" + stack.get(DataComponents.BASE_COLOR).getName());
        }
        return DEFAULT_TEXTURE;
    }

    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<FlagItemRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(FlagItemRenderer.Unbaked::texture)
                ).apply(instance, FlagItemRenderer.Unbaked::new)
        );

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new FlagItemRenderer(
                    this.texture,
                    new FlagBlockModel(context.entityModelSet().bakeLayer(FlagBlockModel.LAYER_LOCATION))
            );
        }

        @Override
        public MapCodec<FlagItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
