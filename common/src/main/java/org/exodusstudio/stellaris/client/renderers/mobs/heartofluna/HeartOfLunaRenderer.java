package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaPose;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public final class HeartOfLunaRenderer extends MobRenderer<HeartOfLunaBossEntity, HeartOfLunaRenderState, LunaBoss> {
    private static final Identifier TEXTURE = IdentifierUtils.texture("entity/heart_of_luna");
    private static final Identifier GLOW = IdentifierUtils.texture("entity/heart_of_luna_emissive");
    private static final Identifier OVERCHARGE = IdentifierUtils.texture("entity/heart_of_luna_overcharge");

    public HeartOfLunaRenderer(EntityRendererProvider.Context context) {
        super(context, new LunaBoss(context.bakeLayer(LunaBoss.LAYER_LOCATION)), 1.4F);
        addLayer(new LivingEntityEmissiveLayer<>(this, state -> GLOW, (state, age) -> state.glow,
                new LunaBoss(context.bakeLayer(LunaBoss.LAYER_LOCATION)), RenderTypes::entityTranslucentEmissive, false));
        addLayer(new LivingEntityEmissiveLayer<>(this, state -> OVERCHARGE, (state, age) -> state.overcharge,
                new LunaBoss(context.bakeLayer(LunaBoss.LAYER_LOCATION)), RenderTypes::entityTranslucentEmissive, false));
    }

    @Override
    public HeartOfLunaRenderState createRenderState() { return new HeartOfLunaRenderState(); }

    @Override
    protected void scale(HeartOfLunaRenderState state, PoseStack pose) { pose.scale(HeartOfLunaPose.SCALE, HeartOfLunaPose.SCALE, HeartOfLunaPose.SCALE); }

    @Override
    public Identifier getTextureLocation(HeartOfLunaRenderState state) { return TEXTURE; }

    @Override
    protected AABB getBoundingBoxForCulling(HeartOfLunaBossEntity boss) {
        AABB bounds = boss.getBoundingBox().inflate(2);
        if (boss.state() == HeartOfLunaBossEntity.State.PULSE_RAY) bounds = bounds.minmax(new AABB(boss.anchor(HeartOfLunaPose.EYE, 0), boss.rayEnd()).inflate(5));
        if (boss.state() == HeartOfLunaBossEntity.State.INTRO || boss.state() == HeartOfLunaBossEntity.State.ROAR || boss.state() == HeartOfLunaBossEntity.State.DEATH) bounds = bounds.inflate(32, 0, 32).expandTowards(0, 44, 0);
        if (boss.cloudTicks() > 0) bounds = bounds.minmax(new AABB(boss.cloudCenter(), boss.cloudCenter().add(0, 3, 0)).inflate(7, 0, 7));
        return bounds;
    }

    @Override
    public void extractRenderState(HeartOfLunaBossEntity boss, HeartOfLunaRenderState state, float partial) {
        super.extractRenderState(boss, state, partial);
        var frame = boss.animationFrame(partial);
        state.action = boss.state();
        state.actionTicks = boss.effectTicks(partial);
        state.bodyRot = Mth.rotLerp(partial, boss.yRotO, boss.getYRot());
        state.animation = frame.animation();
        state.animationTicks = frame.ticks();
        state.previousAnimation = frame.previousAnimation();
        state.previousTicks = frame.previousTicks();
        state.blend = frame.blend();
        state.buffed = boss.buffed();
        state.health = boss.getHealth() / HeartOfLunaBossEntity.MAX_HEALTH;
        state.blockFlash = boss.blockFlash();
        state.eye = boss.anchor(HeartOfLunaPose.EYE, partial).subtract(boss.position());
        state.heart = boss.anchor(HeartOfLunaPose.HEART, partial).subtract(boss.position());
        state.leftEye = boss.anchor(HeartOfLunaPose.LEFT_EYE, partial).subtract(boss.position());
        state.rightEye = boss.anchor(HeartOfLunaPose.RIGHT_EYE, partial).subtract(boss.position());
        state.fist = boss.anchor(HeartOfLunaPose.LEFT_HAND, partial).subtract(boss.position());
        state.endpoint = boss.rayEnd().subtract(boss.position());
        state.cloudCenter = boss.cloudCenter().subtract(state.x, state.y, state.z);
        state.cloudTicks = boss.cloudTicks();
        state.cloudRings = HeartOfLunaCloudSurface.sample(boss);
        state.viewerDistance = state.distanceToCameraSq;
        float beat = (float) Math.pow(Math.max(0, Math.sin(state.ageInTicks * (state.buffed ? 0.5 : 0.14 + (1 - state.health) * 0.2))), 5);
        state.glow = 0.2F + beat * 0.5F + (state.buffed ? 0.25F : 0);
        if (state.action == HeartOfLunaBossEntity.State.ROAR || state.action == HeartOfLunaBossEntity.State.PULSE_RAY) state.glow = 0.65F + 0.35F * Mth.sin(state.actionTicks * 1.6F);
        if (state.action == HeartOfLunaBossEntity.State.INTRO) state.glow *= Mth.clamp(state.actionTicks / 100, 0.04F, 1);
        if (state.action == HeartOfLunaBossEntity.State.DEATH) state.glow = state.actionTicks >= 50 ? 0 : (0.3F + 0.7F * Math.abs(Mth.sin(state.actionTicks * 0.87F))) * Math.min(1, (50 - state.actionTicks) / 10);
        if (boss.hurtTime > 0 || state.blockFlash > 0) state.glow = Math.max(0.9F, state.glow);
        state.overcharge = switch (state.action) {
            case PULSE_RAY -> Mth.clamp((state.actionTicks - 15) / 8, 0, 1) * Mth.clamp((40-state.actionTicks)/8,0,1) * 0.9F;
            case ROAR -> Mth.clamp(1-Math.abs(state.actionTicks-18)/9,0,1)*0.8F;
            case INTRO -> Mth.clamp(1-Math.abs(state.actionTicks-118)/5,0,1);
            case DEATH -> state.actionTicks > 48 ? 0 : Mth.clamp((state.actionTicks-35)/13,0,1);
            default -> boss.hurtTime > 7 ? 0.25F : 0;
        };
    }

    @Override
    public void submit(HeartOfLunaRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, pose, collector, camera);
        HeartOfLunaWorldEffects.submit(state, pose, collector, camera);
    }
}
