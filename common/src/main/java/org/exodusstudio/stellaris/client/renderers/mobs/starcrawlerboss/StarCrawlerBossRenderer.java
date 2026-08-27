package org.exodusstudio.stellaris.client.renderers.mobs.starcrawlerboss;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.CombatState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.IntroState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.DeathCinematicState;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public class StarCrawlerBossRenderer extends MobRenderer<StarCrawlerBossEntity, StarCrawlerBossRenderState, StarCrawlerBossModel> {
    private static final Identifier TEXTURE = IdentifierUtils.texture("entity/star_crawler_boss");

    public StarCrawlerBossRenderer(EntityRendererProvider.Context context) {
        super(context, new StarCrawlerBossModel(context.bakeLayer(StarCrawlerBossModel.LAYER_LOCATION)), 1.65F);

        this.addLayer(new LivingEntityEmissiveLayer<>(
                this,
                state -> TEXTURE,
                StarCrawlerBossRenderer::crystalGlowAlpha,
                new StarCrawlerBossModel(context.bakeLayer(StarCrawlerBossModel.LAYER_LOCATION), true),
                RenderTypes::entityTranslucentEmissive,
                false
        ));
    }

    @Override
    public @NotNull StarCrawlerBossRenderState createRenderState() {
        return new StarCrawlerBossRenderState();
    }

    @Override
    public void extractRenderState(StarCrawlerBossEntity entity, StarCrawlerBossRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        state.ageInTicks = entity.tickCount + partialTick;
        state.combatState = entity.getCombatState();
        state.introState = entity.getIntroState();
        state.introTicks = entity.getIntroTicks() + partialTick;
        state.deathCinematicState = entity.getDeathCinematicState();
        state.deathCinematicTicks =
                StarCrawlerBossDeathController.getDeathVisualTicks(entity, partialTick);
        state.crystalEnergy = Mth.clamp(entity.getCrystalEnergy(), 0.0F, 1.0F);
        state.walkingAnimationState.copyFrom(entity.walkingAnimationState);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.chargeAnimationState.copyFrom(entity.chargeAnimationState);
        state.jumpSlamAnimationState.copyFrom(entity.jumpSlamAnimationState);
        state.groundSmashAnimationState.copyFrom(entity.groundSmashAnimationState);
        state.healingAnimationState.copyFrom(entity.healingAnimationState);
    }

    @Override
    public @NotNull Identifier getTextureLocation(StarCrawlerBossRenderState state) {
        return TEXTURE;
    }

    @Override
    protected AABB getBoundingBoxForCulling(StarCrawlerBossEntity entity) {
        return entity.getBoundingBox().inflate(4.0D);
    }

    private static float crystalGlowAlpha(StarCrawlerBossRenderState state, float ageInTicks) {
        if (state.deathCinematicState == DeathCinematicState.FINALIZED) {
            return 0.0F;
        }

        if (state.deathCinematicState == DeathCinematicState.DYING) {
            float ticks = state.deathCinematicTicks;
            float irregular = Mth.sin(ticks * 1.73F) * 0.5F
                    + Mth.sin(ticks * 0.57F + 1.8F) * 0.32F
                    + Mth.sin(ticks * 2.91F + 0.4F) * 0.18F;
            float instability = (irregular + 1.0F) * 0.5F;
            float weakening = 1.0F - smoothstep(Mth.clamp((ticks - 38.0F) / 70.0F, 0.0F, 1.0F));
            float base = Mth.lerp(weakening, 0.08F, 0.72F);
            float flicker = instability * Mth.lerp(weakening, 0.08F, 0.38F);
            float finalIgnition = 1.0F - smoothstep(Mth.clamp(Math.abs(ticks - 108.0F) / 8.0F, 0.0F, 1.0F));
            float extinguish = 1.0F - smoothstep(Mth.clamp((ticks - 112.0F) / 14.0F, 0.0F, 1.0F));
            return Mth.clamp((base + flicker + finalIgnition * 0.88F) * extinguish, 0.0F, 1.0F);
        }

        if (state.introState != IntroState.COMPLETE) {
            if (state.introState == IntroState.NOT_STARTED) {
                return 0.16F;
            }

            float reveal = smoothstep(Mth.clamp((state.introTicks - 18.0F) / 52.0F, 0.0F, 1.0F));
            float base = Mth.lerp(reveal, 0.16F, 0.80F);

            float recognitionIn = smoothstep(Mth.clamp((state.introTicks - 72.0F) / 7.0F, 0.0F, 1.0F));
            float recognitionOut = 1.0F - smoothstep(Mth.clamp((state.introTicks - 84.0F) / 6.0F, 0.0F, 1.0F));
            float recognitionHush = Math.min(recognitionIn, recognitionOut);
            base = Mth.lerp(recognitionHush, base, 0.26F);

            float ignition = smoothstep(Mth.clamp((state.introTicks - 84.0F) / 6.0F, 0.0F, 1.0F));
            base = Mth.lerp(ignition, base, 0.94F);

            float flashDistance = Math.abs(state.introTicks - 90.0F) / 4.5F;
            float flash = 1.0F - smoothstep(Mth.clamp(flashDistance, 0.0F, 1.0F));
            float pulseStrength = Mth.lerp(recognitionHush, 0.032F, 0.003F);
            float pulse = (Mth.sin(ageInTicks * 0.37F) + 1.0F) * pulseStrength;
            return Mth.clamp(base + flash * 0.62F + pulse, 0.0F, 1.0F);
        }

        boolean healing = state.combatState == CombatState.HEALING_PHASE_2
                || state.combatState == CombatState.HEALING_PHASE_3;
        float pulseStrength = healing ? 0.12F : 0.025F;
        float pulseSpeed = healing ? 0.42F : 0.11F;
        float pulse = (Mth.sin(ageInTicks * pulseSpeed) + 1.0F) * 0.5F * pulseStrength;
        return Mth.clamp(state.crystalEnergy + pulse, 0.0F, 1.0F);
    }

    private static float smoothstep(float value) {
        return value * value * (3.0F - 2.0F * value);
    }
}
