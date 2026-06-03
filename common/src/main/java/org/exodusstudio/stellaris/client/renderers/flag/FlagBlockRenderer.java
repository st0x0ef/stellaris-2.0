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
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
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

    private final SpriteGetter sprites;

    private final PlayerSkinRenderCache playerSkinRenderCache;

    public FlagBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = context.bakeLayer(FlagBlockModel.LAYER_LOCATION);
        this.sprites = context.sprites();
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
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(state.blockPos) instanceof FlagBlockEntity blockEntity) {
            SpriteId material = new SpriteId(TextureAtlas.LOCATION_BLOCKS, IdentifierUtils.id("block/flag/flag_" + blockEntity.getColor().getName()));

            Direction direction = state.blockState.getValue(FlagBlock.FACING);

            poseStack.pushPose();

            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.translate(-0.5D, -1.5, 0.5D);

            poseStack.mulPose(Axis.YP.rotationDegrees(direction.getOpposite().toYRot()));

            submitNodeCollector.submitModelPart(model, poseStack, material.renderType(RenderTypes::entityCutout), state.lightCoords, OverlayTexture.NO_OVERLAY, sprites.get(material));

            poseStack.popPose();

            int flip = (direction.equals(Direction.SOUTH) || direction.equals(Direction.WEST)) ? -1 : 1;
            int offset = flip == 1 ? 0 : 1;

            Map<Direction, Vector4d> rotationMap = Map.of(
                    Direction.NORTH, new Vector4d(-180, flip * 1.2D + offset, 2D, 0.7D),
                    Direction.SOUTH, new Vector4d(-180, flip * 1.2D + offset, 2D, 0.8D),

                    Direction.WEST, new Vector4d(90, 0.7D, 2D, flip * 1.2D + offset),
                    Direction.EAST, new Vector4d(90, 0.8D, 2D, flip * 1.2D + offset)
            );


            for (Direction dir : List.of(direction.getOpposite(), direction)) {
                poseStack.pushPose();

                Vector4d directionInfo = rotationMap.get(dir);

                poseStack.translate(directionInfo.y, directionInfo.z, directionInfo.w);

                ResolvableProfile defaultProfile = ResolvableProfile.createResolved(new GameProfile(UUID.fromString("fe40f09c-fdaa-497f-8e2b-bed31180bfbd"), "TATHAN_06"));

                ResolvableProfile profile = blockEntity.getGameProfile() != null ? blockEntity.getGameProfile() : defaultProfile;

                RenderType renderType = playerSkinRenderCache.getOrDefault(profile).renderType();

                submitSkull(state, poseStack, submitNodeCollector, renderType, (float) directionInfo.x);

                poseStack.popPose();
            }
        }
    }
}