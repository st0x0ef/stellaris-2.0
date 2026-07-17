package org.exodusstudio.stellaris.client.renderers.mobs.starcrawler;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

// I tweaked this a little to better fit his walking
public final class StarCrawlerAnimations {
    private static final float WALK_LENGTH = 1.2F;

    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(WALK_LENGTH).looping()
            .addAnimation("root", rootWalkPosition())
            .addAnimation("root", rootWalkRotation())
            .addAnimation("body", bodyWalkPosition())
            .addAnimation("body", bodyWalkRotation())

            .addAnimation("arm1", shoulderWalk(false, 1.0F))
            .addAnimation("limb", lowerWalk(false, 1.0F))
            .addAnimation("hand", handWalk(false, 1.0F))

            .addAnimation("arm2", shoulderWalk(true, -1.0F))
            .addAnimation("limb2", lowerWalk(true, -1.0F))
            .addAnimation("hand2", handWalk(true, -1.0F))

            .addAnimation("arm3", shoulderWalk(false, -1.0F))
            .addAnimation("limb3", lowerWalk(false, -1.0F))
            .addAnimation("hand3", handWalk(false, -1.0F))

            .addAnimation("arm4", shoulderWalk(true, 1.0F))
            .addAnimation("limb4", lowerWalk(true, 1.0F))
            .addAnimation("hand4", handWalk(true, 1.0F))
            .build();

    public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(0.75F)
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("arm1", attackRotation(12.5F, -2.5F))
            .addAnimation("limb", attackRotation(17.5F, -2.5F))
            .addAnimation("hand", attackRotation(17.5F, -5.0F))
            .addAnimation("arm2", attackRotation(12.5F, -2.5F))
            .addAnimation("limb2", attackRotation(17.5F, -2.5F))
            .addAnimation("hand2", attackRotation(17.5F, -5.0F))
            .addAnimation("arm3", attackRotation(12.5F, -2.5F))
            .addAnimation("limb3", attackRotation(17.5F, -2.5F))
            .addAnimation("hand3", attackRotation(17.5F, -5.0F))
            .addAnimation("arm4", attackRotation(12.5F, -2.5F))
            .addAnimation("limb4", attackRotation(17.5F, -2.5F))
            .addAnimation("hand4", attackRotation(17.5F, -5.0F))
            .build();

    private StarCrawlerAnimations() {
    }

    private static AnimationChannel rootWalkPosition() {
        return new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(-0.24F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.16F, KeyframeAnimations.posVec(-0.17F, 0.04F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, 0.08F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.44F, KeyframeAnimations.posVec(0.17F, 0.04F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.posVec(0.24F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.76F, KeyframeAnimations.posVec(0.17F, 0.04F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.posVec(0.0F, 0.08F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.04F, KeyframeAnimations.posVec(-0.17F, 0.04F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(WALK_LENGTH, KeyframeAnimations.posVec(-0.24F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM));
    }

    private static AnimationChannel rootWalkRotation() {
        return new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(-0.4F, -0.65F, 1.65F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.65F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(-0.4F, 0.65F, -1.65F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.65F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(WALK_LENGTH, KeyframeAnimations.degreeVec(-0.4F, -0.65F, 1.65F), AnimationChannel.Interpolations.CATMULLROM));
    }

    private static AnimationChannel bodyWalkPosition() {
        return new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.posVec(0.0F, 0.34F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.posVec(0.0F, 0.34F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(WALK_LENGTH, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM));
    }

    private static AnimationChannel bodyWalkRotation() {
        return new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(-1.35F, -0.4F, -0.75F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F, KeyframeAnimations.degreeVec(0.8F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(-1.35F, 0.4F, 0.75F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F, KeyframeAnimations.degreeVec(0.8F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(WALK_LENGTH, KeyframeAnimations.degreeVec(-1.35F, -0.4F, -0.75F), AnimationChannel.Interpolations.CATMULLROM));
    }

    private static AnimationChannel shoulderWalk(boolean shifted, float braceSign) {
        return scuttleRotation(shifted, -11.0F, 12.0F, 5.5F, 1.6F * braceSign);
    }

    private static AnimationChannel lowerWalk(boolean shifted, float braceSign) {
        return scuttleRotation(shifted, 16.0F, -18.0F, 8.5F, 2.0F * braceSign);
    }

    private static AnimationChannel handWalk(boolean shifted, float braceSign) {
        return scuttleRotation(shifted, 20.0F, -24.0F, 10.5F, 2.4F * braceSign);
    }

    private static AnimationChannel scuttleRotation(
            boolean shifted,
            float reachX,
            float pushX,
            float sweepY,
            float braceZ
    ) {
        float startX = shifted ? pushX : reachX;
        float oppositeX = shifted ? reachX : pushX;
        float neutralX = (reachX + pushX) * 0.08F;

        return new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F,
                        KeyframeAnimations.degreeVec(startX, 0.0F, braceZ),
                        AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.16F,
                        KeyframeAnimations.degreeVec(startX * 0.82F, sweepY * 0.55F, braceZ * 0.65F),
                        AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.3F,
                        KeyframeAnimations.degreeVec(neutralX, sweepY, 0.0F),
                        AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.44F,
                        KeyframeAnimations.degreeVec(oppositeX * 0.82F, sweepY * 0.55F, -braceZ * 0.65F),
                        AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F,
                        KeyframeAnimations.degreeVec(oppositeX, 0.0F, -braceZ),
                        AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.76F,
                        KeyframeAnimations.degreeVec(oppositeX * 0.82F, -sweepY * 0.55F, -braceZ * 0.65F),
                        AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.9F,
                        KeyframeAnimations.degreeVec(neutralX, -sweepY, 0.0F),
                        AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.04F,
                        KeyframeAnimations.degreeVec(startX * 0.82F, -sweepY * 0.55F, braceZ * 0.65F),
                        AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(WALK_LENGTH,
                        KeyframeAnimations.degreeVec(startX, 0.0F, braceZ),
                        AnimationChannel.Interpolations.CATMULLROM));
    }

    private static AnimationChannel attackRotation(float tuckedDegrees, float releasedDegrees) {
        return new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25F, KeyframeAnimations.degreeVec(tuckedDegrees, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(releasedDegrees, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR));
    }
}