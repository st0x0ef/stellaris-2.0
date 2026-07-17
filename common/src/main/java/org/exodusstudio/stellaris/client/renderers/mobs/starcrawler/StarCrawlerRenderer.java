package org.exodusstudio.stellaris.client.renderers.mobs.starcrawler;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawler.StarCrawlerEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public class StarCrawlerRenderer extends MobRenderer<StarCrawlerEntity, StarCrawlerRenderState, StarCrawlerModel> {
    private static final Identifier TEXTURE = IdentifierUtils.texture("entity/star_crawler");

    public StarCrawlerRenderer(EntityRendererProvider.Context context) {
        super(context, new StarCrawlerModel(context.bakeLayer(StarCrawlerModel.LAYER_LOCATION)), 0.8F);
    }

    @Override
    public @NotNull StarCrawlerRenderState createRenderState() {
        return new StarCrawlerRenderState();
    }

    @Override
    public void extractRenderState(StarCrawlerEntity entity, StarCrawlerRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        state.ageInTicks = entity.tickCount + partialTick;
        state.attackState = entity.getAttackState();
        state.attackStateTicks = entity.getAttackStateTicks() + partialTick;
        state.spinAngle = entity.getSpinAngle(partialTick);
        state.spinVelocity = entity.getSpinVelocity(partialTick);
        state.leanX = entity.getHorizontalLeanX(partialTick);
        state.leanZ = entity.getHorizontalLeanZ(partialTick);
        state.movementSpeed = entity.getSmoothedHorizontalSpeed(partialTick);
        state.attackAnimationState.copyFrom(entity.attackAnimationState);
    }

    @Override
    public @NotNull Identifier getTextureLocation(StarCrawlerRenderState state) {
        return TEXTURE;
    }

    @Override
    protected AABB getBoundingBoxForCulling(StarCrawlerEntity entity) {
        return entity.getBoundingBox().inflate(2.0D);
    }
}
