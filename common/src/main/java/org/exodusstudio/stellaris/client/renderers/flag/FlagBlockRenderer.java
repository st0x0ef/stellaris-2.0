package org.exodusstudio.stellaris.client.renderers.flag;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.FlagBlock;
import org.exodusstudio.stellaris.common.blocks.entities.FlagBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.joml.Vector4d;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FlagBlockRenderer implements BlockEntityRenderer<FlagBlockEntity, BlockEntityRenderState> {

    private final ModelPart model;

    private final Map<SkullBlock.Type, FlagHeadModel> MODELS;

    private final MaterialSet materialSet;

    private final PlayerSkinRenderCache playerSkinRenderCache;

    public FlagBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = context.bakeLayer(FlagBlockModel.LAYER_LOCATION);
        this.materialSet = context.materials();
        this.MODELS = createModels();
        this.playerSkinRenderCache = context.playerSkinRenderCache();
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

    public void submitSkull(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, RenderType renderType, float yRot) {
        FlagHeadModel genericHeadModel = this.MODELS.get(SkullBlock.Types.PLAYER);

        poseStack.pushPose();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        nodeCollector.submitModelPart(genericHeadModel.head, poseStack, renderType, renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(renderState.blockPos) instanceof FlagBlockEntity blockEntity) {
            Material material = new Material(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/flag/flag_" + blockEntity.getColor().getName()));

            Direction direction = renderState.blockState.getValue(FlagBlock.FACING);

            poseStack.pushPose();

            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.translate(-0.5D, -1.5, 0.5D);

            if(direction == Direction.WEST || direction == Direction.EAST) {
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
            }

            nodeCollector.submitModelPart(model, poseStack, material.renderType(RenderTypes::entityCutout), renderState.lightCoords, OverlayTexture.NO_OVERLAY, materialSet.get(material));

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

                ResolvableProfile defaultProfile = ResolvableProfile.createResolved(new GameProfile(UUID.fromString("fe40f09c-fdaa-497f-8e2b-bed31180bfbd"), "TATHAN_06"));

                ResolvableProfile profile = blockEntity.getGameProfile() != null ? blockEntity.getGameProfile() : defaultProfile;

                RenderType renderType = playerSkinRenderCache.getOrDefault(profile).renderType();

                submitSkull(renderState, poseStack, nodeCollector, renderType, (float) directionInfo.x);

                poseStack.popPose();
            }
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