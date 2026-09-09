package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaPose;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaAnimationClock;
import org.exodusstudio.stellaris.client.cinematic.HeartOfLunaCameraPath;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity.State;
import org.joml.Vector3f;

public final class HeartOfLunaModelVerification {
    public static void main(String[] args) {
        verifyPlayback();
        verifyHudMotion();
        verifyCamera();
        HeartOfLunaEffectsVerification.verify();
        ModelPart root = LunaBoss.createBodyLayer().bakeRoot();
        HeartOfLunaAnimations.ROAR.bake(root).apply(1000, 1);
        if (Math.abs(root.getChild("Upper Full Body").xRot - Math.PI / 6) > 0.00001) throw new AssertionError("Roar rotation disagrees with the supplied Blockbench keyframe");
        Vec3 strike = HeartOfLunaPose.local(5, 21, HeartOfLunaPose.LEFT_HAND);
        if (strike.z < 1 || strike.y < 0 || strike.y > 2.5) throw new AssertionError("Punch must cross the forward melee area at impact");
        root.getAllParts().forEach(ModelPart::resetPose);
        AnimationDefinition[] animations = {HeartOfLunaAnimations.WALK, HeartOfLunaAnimations.IDLE,
                HeartOfLunaAnimations.BLOCK, HeartOfLunaAnimations.ROAR, HeartOfLunaAnimations.PULSE_RAY,
                HeartOfLunaAnimations.PUNCH, HeartOfLunaAnimations.DEATH};
        String[][] paths = {
                {"Upper Full Body", "Body", "Heart", "Pulsing heart"},
                {"Upper Full Body", "Upper Body", "Head", "Eyes", "Central Eye"},
                {"Upper Full Body", "Upper Body", "Left Arm", "Rock_10", "Rock_11", "Rock_12", "Rock_13"},
                {"Upper Full Body", "Upper Body", "Right Arm", "Rock_2", "Rock_3", "Rock_4", "Rock_5"},
                {"Upper Full Body", "Upper Body", "Head", "Mouth Lower"},
                {"Upper Full Body", "Upper Body", "Head", "Eyes", "Left Eye"},
                {"Upper Full Body", "Upper Body", "Head", "Eyes", "Right Eye"}
        };
        float[][] offsets = {{-1.5F,0.25F,0},{-5.5F,-3.5F,0.75F},{0,2,0},{0,2,0},{0,0,-4.75F},{7,-50.75F,-6.25F},{-7,-50.75F,-6.25F}};
        int assertions = 0;
        double maximumError = 0;
        for (int animation=0; animation<animations.length; animation++) {
            var baked = animations[animation].bake(root);
            for (int half=0; half<=Math.floor(animations[animation].lengthInSeconds()*40); half++) {
                float ticks=half/2F;
                root.getAllParts().forEach(ModelPart::resetPose);
                baked.apply(half*25L,1);
                for (int anchor=0; anchor<paths.length; anchor++) {
                    PoseStack pose=new PoseStack();
                    pose.mulPose(Axis.YP.rotationDegrees(180));
                    pose.scale(-1,-1,1);
                    pose.scale(1.6F,1.6F,1.6F);
                    pose.translate(0,-1.501F,0);
                    ModelPart part=root;
                    for(String name:paths[anchor]) { part=part.getChild(name); part.translateAndRotate(pose); }
                    float[] offset=offsets[anchor];
                    Vector3f expected=pose.last().pose().transformPosition(offset[0]/16,offset[1]/16,offset[2]/16,new Vector3f());
                    Vec3 actual=HeartOfLunaPose.local(animation,ticks,anchor);
                    double error=actual.distanceTo(new Vec3(expected));
                    maximumError=Math.max(maximumError,error);
                    if(error>0.006) throw new AssertionError("Anchor mismatch animation="+animation+" tick="+ticks+" bone="+anchor+" error="+error+" native="+expected+" generated="+actual);
                    assertions++;
                }
            }
        }
        System.out.println("Verified "+assertions+" native animation anchors; maximum error "+maximumError+" blocks");
    }

    private static void verifyHudMotion() {
        for (int fps : new int[]{20, 60, 144}) {
            HeartOfLunaHudMotion motion = new HeartOfLunaHudMotion();
            motion.impulse(35, true, false);
            double maximum = 0;
            HeartOfLunaHudMotion.Frame frame = null;
            for (int i = 0; i < fps * 4; i++) {
                frame = motion.sample(1.0 / fps, 0.7, 0, 0, 1);
                maximum = Math.max(maximum, Math.abs(frame.x()));
                if (!Float.isFinite(frame.x()) || Math.abs(frame.x()) > 4 || Math.abs(frame.y()) > 1.35F) throw new AssertionError("HUD shake escaped its display bounds");
            }
            if (maximum < 0.5 || Math.abs(frame.x()) > 0.005 || Math.abs(frame.y()) > 0.005) throw new AssertionError("HUD impact must recoil and settle at " + fps + " FPS");
            var charged = motion.sample(0, 0.7, 1, 0, 1);
            var discharge = motion.sample(0, 0.7, 0, 1, 1);
            if (charged.scaleX() >= 1 || discharge.scaleX() <= 1) throw new AssertionError("HUD must contract on charge and expand on discharge");
        }
        System.out.println("Verified bounded HUD recoil, frame-rate stability, and charge/discharge motion");
    }

    private static void verifyCamera() {
        for (boolean death : new boolean[]{false, true}) {
            int duration = death ? State.DEATH.duration : State.INTRO.duration;
            Vec3 origin = new Vec3(125, 70, -60);
            Vec3 lastCamera = null;
            Vec3 lastAim = null;
            int titleHold = 0;
            for (int frame = 0; frame <= duration * 3; frame++) {
                float ticks = frame / 3F;
                var shot = HeartOfLunaCameraPath.sample(origin, 179, death, ticks, duration);
                if (lastCamera != null && shot.camera().distanceTo(lastCamera) > 0.08) throw new AssertionError("Cinematic camera jumped between frames");
                if (lastAim != null && shot.aim().distanceTo(lastAim) > 0.06) throw new AssertionError("Cinematic aim jumped between frames");
                if (frame == 0 && (shot.entrance() != 0 || shot.exit() != 0)) throw new AssertionError("Cinematic must ease in from gameplay");
                if (frame == duration * 3 && shot.exit() != 1) throw new AssertionError("Cinematic must return fully to gameplay before ending");
                if (HeartOfLunaCameraPath.titleOpacity(death, ticks, duration) >= 0.99F) titleHold++;
                lastCamera = shot.camera();
                lastAim = shot.aim();
            }
            if (titleHold < 45 * 3) throw new AssertionError("Boss title did not hold long enough");
            if (HeartOfLunaCameraPath.titleOpacity(death, duration, duration) != 0) throw new AssertionError("Title must fade out before the cutscene ends");
        }
        System.out.println("Verified smooth intro/death camera paths, complete return transitions, and sustained title holds");
    }

    private static void verifyPlayback() {
        HeartOfLunaAnimationClock first = new HeartOfLunaAnimationClock();
        HeartOfLunaAnimationClock second = new HeartOfLunaAnimationClock();
        first.sample(0, 1, 20, 100);
        second.sample(0, 1, 40, 100);
        LunaBoss model = new LunaBoss(LunaBoss.createBodyLayer().bakeRoot());
        float startRotation = 0;
        boolean moved = false;
        for (int frame = 0; frame <= 120; frame++) {
            float elapsed = frame / 6F;
            var sample = first.sample(1, 0, elapsed, 100 + elapsed);
            HeartOfLunaRenderState state = new HeartOfLunaRenderState();
            state.animation = sample.animation();
            state.animationTicks = sample.ticks();
            state.previousAnimation = sample.previousAnimation();
            state.previousTicks = sample.previousTicks();
            state.blend = sample.blend();
            if (Math.abs(state.blend - Math.min(1, elapsed / 4)) > 0.00001) throw new AssertionError("Walk crossfade restarted with a fresh render snapshot");
            model.setupAnim(state);
            float rotation = model.root().getChild("Upper Full Body").zRot;
            if (frame == 0) startRotation = rotation;
            else moved |= Math.abs(rotation - startRotation) > 0.02;
            var other = second.sample(0, 1, 40 + elapsed, 100 + elapsed);
            if (other.animation() != 1 || other.blend() != 1) throw new AssertionError("Animation state leaked between bosses");
        }
        if (!moved) throw new AssertionError("Walking model did not animate");
        var attack = first.sample(4, 5, 0, 121);
        if (attack.animation() != 5 || attack.ticks() != 0 || attack.blend() != 1) throw new AssertionError("Attack did not start immediately");
        System.out.println("Verified fresh-frame walk playback, crossfades, attack timing, and independent boss animation clocks");
    }
}
