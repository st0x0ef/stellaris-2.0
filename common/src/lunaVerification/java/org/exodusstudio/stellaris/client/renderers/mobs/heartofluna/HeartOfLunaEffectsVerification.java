package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity.State;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaPose;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaCombatRules;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class HeartOfLunaEffectsVerification {
    public static void verify() {
        int[] counts = new int[3];
        VertexConsumer vertices = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(), new Class<?>[]{VertexConsumer.class}, (proxy, method, args) -> {
            if (method.isDefault()) return InvocationHandler.invokeDefault(proxy, method, args);
            for (Object value : args) if (value instanceof Float f && !Float.isFinite(f)) throw new AssertionError("Non-finite effect vertex");
            switch (method.getName()) {
                case "addVertex" -> {
                    if (counts[0] > 0 && counts[1] != 31) throw new AssertionError("Incomplete beacon vertex attributes: " + counts[1]);
                    counts[0]++;
                    counts[1] = 0;
                }
                case "setColor" -> counts[1] |= 1;
                case "setUv" -> counts[1] |= 2;
                case "setUv1" -> counts[1] |= 4;
                case "setUv2" -> counts[1] |= 8;
                case "setNormal" -> counts[1] |= 16;
                default -> throw new AssertionError("Unexpected vertex operation: " + method.getName());
            }
            return proxy;
        });
        SubmitNodeCollector collector = (SubmitNodeCollector) Proxy.newProxyInstance(SubmitNodeCollector.class.getClassLoader(), new Class<?>[]{SubmitNodeCollector.class}, (proxy, method, args) -> {
            if (!method.getName().equals("submitCustomGeometry")) throw new AssertionError("Unexpected effect submission");
            ((SubmitNodeCollector.CustomGeometryRenderer) args[2]).render(((PoseStack) args[0]).last(), vertices);
            return null;
        });
        int frames = 0;
        for (int distance : new int[]{16, 2500}) for (State action : State.values()) {
            for (int tick = 0; tick <= Math.max(32, action.duration); tick++) {
                HeartOfLunaRenderState state = new HeartOfLunaRenderState();
                state.action = action;
                state.actionTicks = action == State.PULSE_RAY ? HeartOfLunaCombatRules.rayAnimationTicks(tick + 0.5F) : tick + 0.5F;
                state.ageInTicks = 200 + tick + 0.5F;
                int animation = action == State.INTRO && tick >= 100 && tick < 140 ? 3 : action.animation;
                float animationTicks = action == State.INTRO && tick >= 140 ? state.actionTicks - 140 : action == State.INTRO && tick >= 100 ? state.actionTicks - 100 : state.actionTicks;
                state.eye = HeartOfLunaPose.local(animation, animationTicks, HeartOfLunaPose.EYE);
                state.heart = HeartOfLunaPose.local(animation, animationTicks, HeartOfLunaPose.HEART);
                state.leftEye = HeartOfLunaPose.local(animation, animationTicks, HeartOfLunaPose.LEFT_EYE);
                state.rightEye = HeartOfLunaPose.local(animation, animationTicks, HeartOfLunaPose.RIGHT_EYE);
                state.fist = HeartOfLunaPose.local(animation, animationTicks, HeartOfLunaPose.LEFT_HAND);
                state.endpoint = state.eye.add(tick % 3 == 0 ? new Vec3(0, 40, 0) : tick % 3 == 1 ? new Vec3(0, -40, 0) : new Vec3(0, 0, 40));
                state.cloudTicks = 100 - tick % 100;
                state.cloudCenter = new Vec3(-4, 0, 3);
                state.x = 1000;
                state.y = 70;
                state.z = -1000;
                state.cloudRings = new Vec3[3][97];
                for (int band = 0; band < 3; band++) for (int i = 0; i <= 96; i++) {
                    if (i > 20 && i < 25) continue;
                    double angle = i * Math.PI * 2 / 96;
                    double radius = new double[]{6.35, 6.8, 7}[band];
                    state.cloudRings[band][i] = state.cloudCenter.add(state.x + Math.cos(angle) * radius, state.y + (i < 48 ? 0 : 0.5), state.z + Math.sin(angle) * radius);
                }
                state.blockFlash = 8 - tick % 9;
                state.viewerDistance = distance;
                CameraRenderState camera = new CameraRenderState();
                camera.pos = new Vec3(12, 8, 14);
                int previous = counts[0];
                HeartOfLunaWorldEffects.submit(state, new PoseStack(), collector, camera);
                int count = counts[0] - previous;
                if (count % 4 != 0 || count > 15000) throw new AssertionError("Invalid effect mesh budget: " + count);
                counts[2] = Math.max(counts[2], count);
                frames++;
            }
        }
        if (counts[1] != 31) throw new AssertionError("Incomplete final effect vertex");
        System.out.println("Verified " + frames + " effect frames with complete beacon vertex attributes; peak " + counts[2] + " vertices");
    }

    private HeartOfLunaEffectsVerification() {}
}
