package org.exodusstudio.stellaris.client.renderers.flag;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.FlagBlock;
import org.exodusstudio.stellaris.common.blocks.entities.FlagBlockEntity;
import org.joml.Vector4d;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FlagBlockRenderer implements BlockEntityRenderer<FlagBlockEntity> {

    private final ModelPart model;

    private final Map<SkullBlock.Type, FlagHeadModel> MODELS;

    public FlagBlockRenderer(BlockEntityRendererProvider.Context context) {
        model = context.bakeLayer(FlagBlockModel.LAYER_LOCATION);

        this.MODELS = createModels();
    }

    public static Map<SkullBlock.Type, FlagHeadModel> createModels() {
        Map<SkullBlock.Type, FlagHeadModel> models = Maps.newHashMap();
        Minecraft minecraft = Minecraft.getInstance();
        Map<String, ModelPart> map = Map.of("head", new FlagHeadModel(minecraft.getEntityModels().bakeLayer(FlagHeadModel.HUMANOID_LAYER_LOCATION)).head);
        ModelPart modelPart = new ModelPart(Collections.emptyList(), map);

        FlagHeadModel genericheadmodel = new FlagHeadModel(modelPart);

        models.put(SkullBlock.Types.PLAYER, genericheadmodel);
        return models;
    }

    public void renderSkull(float yRot, float mouthAnimation, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, SkullBlock.Type type, RenderType renderType) {
        FlagHeadModel genericheadmodel = this.MODELS.get(type);

        poseStack.pushPose();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

        genericheadmodel.setupAnim(mouthAnimation, yRot, 0.0F);
        genericheadmodel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        poseStack.popPose();
    }

    @Override
    public void render(FlagBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        Direction direction = blockEntity.getBlockState().getValue(FlagBlock.FACING);

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("stellaris", "textures/block/flag/flag_" + blockEntity.getColor().getName() + ".png");
        poseStack.pushPose();

        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(-0.5D, -1.5, 0.5D);

        if(direction == Direction.WEST || direction == Direction.EAST) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }

        model.render(poseStack, bufferSource.getBuffer(RenderType.entityCutout(texture)), packedLight, packedOverlay);

        poseStack.popPose();

        Map<Direction, Vector4d> rotationMap = Map.of(
                Direction.NORTH, new Vector4d(-180, 1.13D, 2.05D, 0.7D),
                Direction.SOUTH, new Vector4d(-180, 1.13D, 2.05D, 0.8D),

                Direction.WEST, new Vector4d(90, 0.7D, 2D, 1.2D),
                Direction.EAST, new Vector4d(90, 0.8D, 2D, 1.2D)
        );


        for (Direction dir : List.of(direction.getOpposite(), direction)) {
            poseStack.pushPose();

            Vector4d directionInfo = rotationMap.get(dir);
            poseStack.translate(directionInfo.y, directionInfo.z, directionInfo.w);

            ResolvableProfile testProfile = new ResolvableProfile(new GameProfile(UUID.fromString("fe40f09c-fdaa-497f-8e2b-bed31180bfbd"), "TATHAN_06"));

            ResolvableProfile profile = blockEntity.getGameProfile() != null ? blockEntity.getGameProfile() : testProfile;

            RenderType renderType = SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, profile);

            renderSkull((float) directionInfo.x, 0.0F, poseStack, bufferSource, packedLight, SkullBlock.Types.PLAYER, renderType);

            poseStack.popPose();
        }
    }

    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(FlagBlockEntity flag, Vec3 pos) {
        return Vec3.atCenterOf(flag.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(pos.multiply(1.0D, 0.0D, 1.0D), this.getViewDistance());
    }
}