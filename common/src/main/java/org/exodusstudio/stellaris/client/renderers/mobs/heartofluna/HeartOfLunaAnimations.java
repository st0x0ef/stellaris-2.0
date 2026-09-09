package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public final class HeartOfLunaAnimations {
    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(1.6000000F).looping()
        .addAnimation("Left Legs", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(30.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.1500000F, KeyframeAnimations.degreeVec(30.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8000000F, KeyframeAnimations.degreeVec(-30.0000000F, 0.0000000F, 4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8500000F, KeyframeAnimations.degreeVec(-30.0000000F, 0.0000000F, 4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.6000000F, KeyframeAnimations.degreeVec(30.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Right Legs", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-30.0000000F, 0.0000000F, 4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.0500000F, KeyframeAnimations.degreeVec(-30.0000000F, 0.0000000F, 4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8000000F, KeyframeAnimations.degreeVec(30.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.9500000F, KeyframeAnimations.degreeVec(30.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.6000000F, KeyframeAnimations.degreeVec(-30.0000000F, 0.0000000F, 4.0000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-20.0000000F, 0.0000000F, -3.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.1500000F, KeyframeAnimations.degreeVec(-20.0000000F, 0.0000000F, -3.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8000000F, KeyframeAnimations.degreeVec(20.0000000F, 0.0000000F, 3.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.9500000F, KeyframeAnimations.degreeVec(20.0000000F, 0.0000000F, 3.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.6000000F, KeyframeAnimations.degreeVec(-20.0000000F, 0.0000000F, -3.0000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(20.0000000F, 0.0000000F, 3.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.0500000F, KeyframeAnimations.degreeVec(20.0000000F, 0.0000000F, 3.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8000000F, KeyframeAnimations.degreeVec(-20.0000000F, 0.0000000F, -3.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.9500000F, KeyframeAnimations.degreeVec(-20.0000000F, 0.0000000F, -3.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.6000000F, KeyframeAnimations.degreeVec(20.0000000F, 0.0000000F, 3.0000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Upper Full Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.2500000F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, -4.9375000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(-5.3516000F, 0.0000000F, -2.3091000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.7500000F, KeyframeAnimations.degreeVec(4.2939000F, 0.0000000F, 3.2601000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(-5.2684000F, 0.0000000F, 4.5462000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 4.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(5.2179000F, 0.0000000F, 3.3084000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-3.9497000F, 0.0000000F, -2.1833000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.6000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Upper Full Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.1000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4000000F, KeyframeAnimations.posVec(0.0000000F, 0.5000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8000000F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.9000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2000000F, KeyframeAnimations.posVec(0.0000000F, 0.5000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.6000000F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Mouth Lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-7.5000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.degreeVec(-15.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(-7.5000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(-7.5000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(-15.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-7.5000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Heart", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(1.1666700F).looping()
        .addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, -2.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, -2.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(0.0000000F, 1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mouth Lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.degreeVec(-5.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.degreeVec(-5.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, 1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Legs", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, 0.2500000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(0.0000000F, 0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.posVec(0.0000000F, 0.2500000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Legs", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, 0.2500000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(0.0000000F, 0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.posVec(0.0000000F, 0.2500000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg L", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, -0.2500000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(0.0000000F, -0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.posVec(0.0000000F, -0.2500000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg R", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, -0.2500000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(0.0000000F, -0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.posVec(0.0000000F, -0.2500000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, 0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(0.0000000F, 1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.posVec(0.0000000F, 0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_10", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 2.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 5.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 2.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, 0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(0.0000000F, 1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.posVec(0.0000000F, 0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 2.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 5.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 2.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Heart", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    public static final AnimationDefinition BLOCK = AnimationDefinition.Builder.withLength(0.8333300F)
        .addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(0.0000000F, 10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(0.0000000F, 22.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(0.0000000F, 22.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mouth Lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(-5.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(-5.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3750000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4583300F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Legs", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(0.0000000F, -10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(0.0000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(0.0000000F, -30.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(0.0000000F, -30.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Legs", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(0.0000000F, -5.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(0.0000000F, -10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(0.0000000F, -15.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(0.0000000F, -15.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Low Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(0.0000000F, -10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(0.0000000F, -10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(0.0000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(0.0000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Up Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(-7.5000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(-12.9275000F, -34.9548000F, 0.7953000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(-12.9275000F, -34.9548000F, 0.7953000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Foot L", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg L", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(-5.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(-15.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(-15.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg R", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(0.0000000F, -5.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(0.0000000F, -10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(-5.0000000F, -15.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(-5.0000000F, -15.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(15.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(25.2207000F, -9.6560000F, -2.6129000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3750000F, KeyframeAnimations.degreeVec(26.3204000F, -18.6662000F, -7.0922000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(26.3204000F, -18.6662000F, -7.0922000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, -1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, -2.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, -2.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_10", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(19.7196000F, -3.4049000F, 9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(24.9283000F, -7.9434000F, 7.2992000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(24.9283000F, -7.9434000F, 7.2992000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_10", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -4.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -4.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_11", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(19.7197000F, -3.4049000F, 9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(24.9284000F, -7.9434000F, 7.2992000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(24.9284000F, -7.9434000F, 7.2992000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_11", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -4.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -4.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_12", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(19.9299000F, -1.7082000F, 4.6999000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(25.0769000F, -6.2401000F, 2.5820000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(25.0769000F, -6.2401000F, 2.5820000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_12", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -4.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -4.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_13", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(19.8510000F, -1.7280000F, 9.8511000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(19.6394000F, -3.4181000F, 14.5617000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(19.6394000F, -3.4181000F, 14.5617000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_13", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -4.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -4.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(7.5000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_2", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(4.9811000F, -0.4352000F, 4.9811000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(7.4717000F, -0.6518000F, 4.9574000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(7.4717000F, -0.6518000F, 4.9574000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_3", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, 3.7500000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(7.5000000F, 0.0000000F, 5.6250000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3750000F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, 7.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, 7.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_4", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, 3.7500000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(7.5000000F, 0.0000000F, 5.6250000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3750000F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, 7.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(5.0000000F, 0.0000000F, 7.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_5", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Upper Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2083300F, KeyframeAnimations.degreeVec(-7.5000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(-12.9275000F, -34.9548000F, 0.7953000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(-12.9275000F, -34.9548000F, 0.7953000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8333300F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    public static final AnimationDefinition ROAR = AnimationDefinition.Builder.withLength(2.0000000F)
        .addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(20.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.degreeVec(19.7196000F, -3.4049000F, 9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.3333300F, KeyframeAnimations.degreeVec(18.8816000F, -6.7176000F, 18.8818000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.3750000F, KeyframeAnimations.degreeVec(19.7195000F, -3.4049000F, 9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4166700F, KeyframeAnimations.degreeVec(19.9998000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4583300F, KeyframeAnimations.degreeVec(19.7195000F, 3.4049000F, -9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(18.8814000F, 6.7176000F, -18.8818000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5416700F, KeyframeAnimations.degreeVec(19.7193000F, 3.4049000F, -9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5833300F, KeyframeAnimations.degreeVec(19.9996000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6250000F, KeyframeAnimations.degreeVec(19.7196000F, -3.4049000F, 9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6666700F, KeyframeAnimations.degreeVec(18.8816000F, -6.7176000F, 18.8818000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7083300F, KeyframeAnimations.degreeVec(19.7195000F, -3.4049000F, 9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7500000F, KeyframeAnimations.degreeVec(19.9998000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7916700F, KeyframeAnimations.degreeVec(19.7195000F, 3.4049000F, -9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.8333300F, KeyframeAnimations.degreeVec(18.8814000F, 6.7176000F, -18.8818000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.8750000F, KeyframeAnimations.degreeVec(19.7193000F, 3.4049000F, -9.4081000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.9166700F, KeyframeAnimations.degreeVec(19.9996000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mouth Lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(-30.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Legs", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Legs", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Low Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Up Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg L", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg R", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 4.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, -20.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.3750000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, -30.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 2.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_10", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_11", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_12", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_13", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, -4.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 20.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.3750000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 30.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 2.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_2", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_3", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_4", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_5", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Heart", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.scaleVec(1.3000000F, 1.3000000F, 1.3000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.scaleVec(1.4000000F, 1.4000000F, 1.4000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.3333300F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.3750000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4166700F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4583300F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5416700F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5833300F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6250000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6666700F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7083300F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7500000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7916700F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.8333300F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.8750000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.9166700F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.9583300F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Upper Full Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(30.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(-20.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    public static final AnimationDefinition PULSE_RAY = AnimationDefinition.Builder.withLength(2.0000000F)
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(75.0000000F, -15.0000000F, 10.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-55.0000000F, 5.0000000F, -5.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-55.0000000F, 5.0000000F, -5.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(-75.0000000F, 15.0000000F, -10.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(55.0000000F, -5.0000000F, 5.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(55.0000000F, -5.0000000F, 5.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Low Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9166700F, KeyframeAnimations.degreeVec(-5.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(8.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(2.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Low Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9166700F, KeyframeAnimations.posVec(0.0000000F, 1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(0.0000000F, -2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(0.0000000F, -0.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9166700F, KeyframeAnimations.degreeVec(-8.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(12.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(3.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9166700F, KeyframeAnimations.posVec(0.0000000F, 2.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(0.0000000F, -3.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(0.0000000F, -1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Up Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9166700F, KeyframeAnimations.degreeVec(-12.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(18.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(4.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Up Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9166700F, KeyframeAnimations.posVec(0.0000000F, 3.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(0.0000000F, -5.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(0.0000000F, -1.5000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mouth Lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9166700F, KeyframeAnimations.degreeVec(0.8300000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-30.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7500000F, KeyframeAnimations.degreeVec(-15.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Heart", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5000000F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7500000F, KeyframeAnimations.scaleVec(1.3000000F, 1.3000000F, 1.3000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0000000F, KeyframeAnimations.scaleVec(1.4000000F, 1.4000000F, 1.4000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.3333300F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4166700F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5833300F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6666700F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7500000F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.8333300F, KeyframeAnimations.scaleVec(1.2000000F, 1.2000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.9166700F, KeyframeAnimations.scaleVec(1.1000000F, 1.1000000F, 1.1000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Upper Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9166700F, KeyframeAnimations.degreeVec(20.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    public static final AnimationDefinition PUNCH = AnimationDefinition.Builder.withLength(1.5000000F)
        .addAnimation("Mouth Lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(20.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(10.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-5.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Legs", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-0.0000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-0.0000000F, -30.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Legs", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-0.0000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-0.0000000F, -30.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Low Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-0.0000000F, -10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-0.0000000F, -5.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-0.0000000F, 10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-0.0000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-0.0000000F, -10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-0.0000000F, 20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Up Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-0.0000000F, -30.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-0.0000000F, -15.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-0.0000000F, 30.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Foot L", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-0.0000000F, 10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg L", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(10.0000000F, 10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(10.0000000F, 20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Foot R", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(10.0000000F, 10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg R", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(5.0000000F, 10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-0.0000000F, 20.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(6.6000000F, -3.6000000F, 1.8000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(25.3000000F, -13.8000000F, 6.9000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.degreeVec(22.0000000F, -12.0000000F, 6.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-24.3402000F, 11.2696000F, -4.1413000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-24.3402000F, 11.2696000F, -4.1413000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.degreeVec(-12.1701000F, 5.6348000F, -2.0707000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.posVec(-0.0000000F, -0.6000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.posVec(-0.0000000F, -2.0000000F, 4.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, -2.4000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, -2.4000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, -1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_10", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(4.2000000F, 1.8000000F, -1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(16.1000000F, 6.9000000F, -4.6000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.degreeVec(14.0000000F, 6.0000000F, -4.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-32.4823000F, 7.6904000F, 9.2311000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-47.4823000F, 7.6904000F, 9.2311000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.degreeVec(-23.7412000F, 3.8452000F, 4.6156000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_10", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.posVec(-0.0000000F, -0.9000000F, 1.5000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.posVec(-0.0000000F, -3.0000000F, 5.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.posVec(-0.0000000F, -8.8000000F, -3.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(-0.0000000F, -10.8000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.posVec(-0.0000000F, -5.4000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_11", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(2.4000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(9.2000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.degreeVec(8.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-34.9236000F, 21.9975000F, -3.5551000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-36.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.degreeVec(-18.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_11", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.posVec(-0.0000000F, -0.6000000F, 1.2000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.posVec(-0.0000000F, -0.8100000F, 3.6300000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.posVec(-0.0000000F, -2.0000000F, 4.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.posVec(-0.0000000F, -12.0000000F, -2.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(-0.0000000F, -12.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.posVec(-0.0000000F, -6.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_12", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(1.8000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(6.9000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.degreeVec(6.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-49.5635000F, 31.4274000F, 1.9526000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-36.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.degreeVec(-18.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_12", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.posVec(-0.0000000F, -0.6000000F, 0.9000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.posVec(-0.0000000F, -0.8100000F, 2.7200000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.posVec(-0.0000000F, -2.0000000F, 3.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.posVec(-4.0000000F, -10.8000000F, -3.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(-0.0000000F, -10.8000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.posVec(-0.0000000F, -5.4000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_13", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(1.2000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(4.6000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.degreeVec(4.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-12.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-12.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.degreeVec(-6.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_13", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.posVec(-0.0000000F, -0.3000000F, 0.6000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.posVec(-0.0000000F, 0.0900000F, 1.8100000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.posVec(-0.0000000F, -1.0000000F, 2.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.posVec(-0.0000000F, -6.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(-0.0000000F, -6.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2916700F, KeyframeAnimations.posVec(-0.0000000F, -3.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(-0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 10.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 20.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 10.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-5.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-10.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-5.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-5.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-10.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(5.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-5.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-10.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Rock_5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-5.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-10.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(5.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Upper Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3333300F, KeyframeAnimations.degreeVec(-0.0000000F, -30.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-0.0000000F, -15.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-0.0000000F, 10.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(-0.0000000F, -0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    public static final AnimationDefinition DEATH = AnimationDefinition.Builder.withLength(1.7500000F)
        .addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(1.0300000F, -27.3100000F, -25.8700000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.3750000F, KeyframeAnimations.degreeVec(-7.1700000F, -21.8500000F, -20.7000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(-23.0206000F, -26.4934000F, -6.1998000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.2916700F, KeyframeAnimations.posVec(-1.9400000F, -33.4900000F, 1.6600000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.3750000F, KeyframeAnimations.posVec(-2.7700000F, -36.6600000F, 2.3600000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(-2.7700000F, -42.2500000F, 2.3600000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.1250000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4166700F, KeyframeAnimations.degreeVec(20.3200000F, 22.5500000F, -47.5000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(16.2600000F, 18.0400000F, -38.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5833300F, KeyframeAnimations.degreeVec(17.6700000F, 19.6100000F, -41.3100000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.1250000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4166700F, KeyframeAnimations.posVec(-0.5500000F, -31.3500000F, -3.2900000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(-0.7800000F, -34.3200000F, -4.7000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5833300F, KeyframeAnimations.posVec(-0.7800000F, -33.0000000F, -4.7000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.2500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5416700F, KeyframeAnimations.degreeVec(-32.3600000F, 0.3100000F, -54.4500000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.6250000F, KeyframeAnimations.degreeVec(-24.5958000F, -8.3511000F, -61.6787000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.7083300F, KeyframeAnimations.degreeVec(-21.8195000F, -14.2924000F, -75.7337000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5416700F, KeyframeAnimations.posVec(-2.1100000F, -31.3500000F, 1.0500000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.6250000F, KeyframeAnimations.posVec(-3.0100000F, -38.3200000F, 1.5000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.7083300F, KeyframeAnimations.posVec(-3.0100000F, -41.3200000F, 1.5000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Up Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.3750000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.6666700F, KeyframeAnimations.degreeVec(-4.8300000F, -16.0700000F, 10.2700000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.7916700F, KeyframeAnimations.degreeVec(-25.8700000F, -12.8600000F, 8.2100000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(-30.5100000F, -13.9800000F, 8.9300000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Up Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.3750000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.6666700F, KeyframeAnimations.posVec(2.1700000F, -30.4000000F, -3.4500000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.7916700F, KeyframeAnimations.posVec(3.0900000F, -33.2800000F, -4.9400000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.8750000F, KeyframeAnimations.posVec(3.0900000F, -32.0000000F, -4.9400000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Heart", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.5000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Heart", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.5000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.9166700F, KeyframeAnimations.posVec(0.0000000F, -20.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0416700F, KeyframeAnimations.posVec(0.0000000F, -23.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("Mid Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.6250000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.9166700F, KeyframeAnimations.degreeVec(-18.7900000F, -23.4200000F, -46.3800000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0416700F, KeyframeAnimations.degreeVec(-15.0300000F, -18.7300000F, -37.1000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.1250000F, KeyframeAnimations.degreeVec(-16.3400000F, -20.3600000F, -40.3300000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.6250000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.9166700F, KeyframeAnimations.posVec(2.4300000F, -22.8000000F, 0.7300000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0416700F, KeyframeAnimations.posVec(3.4700000F, -24.9600000F, 1.0400000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.1250000F, KeyframeAnimations.posVec(3.4700000F, -24.0000000F, 1.0400000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Low Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.7500000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0833300F, KeyframeAnimations.degreeVec(35.3200000F, 13.2100000F, 4.1700000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.1666700F, KeyframeAnimations.degreeVec(28.2600000F, 10.5700000F, 3.3300000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2500000F, KeyframeAnimations.degreeVec(30.7100000F, 11.4900000F, 3.6200000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Low Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.7500000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0833300F, KeyframeAnimations.posVec(3.3100000F, -15.2000000F, -0.8500000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.1666700F, KeyframeAnimations.posVec(4.7300000F, -16.6400000F, -1.2100000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2500000F, KeyframeAnimations.posVec(4.7300000F, -16.0000000F, -1.2100000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg R", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.8750000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2083300F, KeyframeAnimations.degreeVec(5.9800000F, 18.9400000F, 13.6300000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2916700F, KeyframeAnimations.degreeVec(4.7900000F, 15.1500000F, 10.9000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.3750000F, KeyframeAnimations.degreeVec(5.2000000F, 16.4700000F, 11.8500000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg R", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.8750000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2083300F, KeyframeAnimations.posVec(2.5300000F, -3.8000000F, 0.5400000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2916700F, KeyframeAnimations.posVec(3.6200000F, -4.1600000F, 0.7700000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.3750000F, KeyframeAnimations.posVec(3.6200000F, -4.0000000F, 0.7700000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg L", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(1.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.3333300F, KeyframeAnimations.degreeVec(23.5300000F, -26.1200000F, -31.2900000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.4166700F, KeyframeAnimations.degreeVec(18.8200000F, -20.8900000F, -25.0300000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.5000000F, KeyframeAnimations.degreeVec(20.4600000F, -22.7100000F, -27.2100000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mid Leg L", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(1.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.3333300F, KeyframeAnimations.posVec(-1.4700000F, -3.8000000F, -2.9400000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.4166700F, KeyframeAnimations.posVec(-2.1100000F, -4.1600000F, -4.2000000F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.5000000F, KeyframeAnimations.posVec(-2.1100000F, -4.0000000F, -4.2000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mouth Lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.degreeVec(10.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Mouth Lower", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2916700F, KeyframeAnimations.posVec(0.0000000F, 1.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Eyes", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Eyes", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0000000F, KeyframeAnimations.scaleVec(1.0000000F, 1.0000000F, 1.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2500000F, KeyframeAnimations.scaleVec(0.9000000F, 1.0000000F, 0.9000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Lower Full Body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0000000F, KeyframeAnimations.degreeVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.degreeVec(15.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("Lower Full Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0000000F, KeyframeAnimations.posVec(0.0000000F, 0.0000000F, 0.0000000F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9583300F, KeyframeAnimations.posVec(0.0000000F, -6.0000000F, 4.0000000F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    private HeartOfLunaAnimations() {}
}
