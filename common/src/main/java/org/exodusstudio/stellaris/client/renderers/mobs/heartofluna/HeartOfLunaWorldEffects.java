package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity.State;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public final class HeartOfLunaWorldEffects {
    private static final Identifier RIBBON = IdentifierUtils.texture("effect/luna_ribbon");
    private static final Identifier GLOW = IdentifierUtils.texture("effect/luna_glow");
    private static final Vec3 UP = new Vec3(0, 1, 0);

    public static void submit(HeartOfLunaRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.viewerDistance > 9216 || state.isInvisible) return;
        Vec3 viewer = camera.pos.subtract(state.x, state.y, state.z);
        collector.submitCustomGeometry(pose, RenderTypes.beaconBeam(BeaconRenderer.BEAM_LOCATION, false),
                (matrix, vertices) -> cores(state, matrix, vertices));
        collector.submitCustomGeometry(pose, RenderTypes.beaconBeam(RIBBON, true),
                (matrix, vertices) -> ribbons(state, matrix, vertices));
        collector.submitCustomGeometry(pose, RenderTypes.beaconBeam(GLOW, true),
                (matrix, vertices) -> glows(state, matrix, vertices, viewer));
    }

    private static double release(HeartOfLunaRenderState s) {
        double t = s.actionTicks;
        return switch (s.action) {
            case PULSE_RAY -> t >= 23 && t < 40 ? Math.min(1, (40 - t) / 8) : 0;
            case ROAR -> t >= 18 ? Math.pow(Math.max(0, (40 - t) / 22), 2) : 0;
            case INTRO -> t >= 118 ? Math.pow(Math.max(0, (140 - t) / 22), 2) : 0;
            case DEATH -> t >= 48 && t < 80 ? Math.pow((80 - t) / 32, 2) : 0;
            default -> 0;
        };
    }

    private static void cores(HeartOfLunaRenderState s, PoseStack.Pose pose, VertexConsumer out) {
        double power = release(s);
        if (power <= 0) return;
        double spin = s.ageInTicks * 0.035;
        if (s.action == State.PULSE_RAY) {
            beam(pose, out, s.eye, s.endpoint, 0.085 * power, 0.06 * power, spin, -s.ageInTicks * 0.15, 0xFFF3DEFF, 4);
        } else {
            Vec3 floor = new Vec3(s.heart.x, 0.05, s.heart.z);
            double height = s.action == State.DEATH ? 44 : 28;
            beam(pose, out, floor, floor.add(0, height, 0), 0.28 * power, 0.06 * power, spin, -s.ageInTicks * 0.12, 0xFFE8AEFF, 4);
        }
    }

    private static void ribbons(HeartOfLunaRenderState s, PoseStack.Pose pose, VertexConsumer out) {
        int detail = s.viewerDistance > 1600 ? 32 : 64;
        double t = s.actionTicks, age = s.ageInTicks, power = release(s);
        if (s.action == State.PULSE_RAY) {
            Vec3 axis = s.endpoint.subtract(s.eye).normalize();
            if (t < 23) {
                double charge = t / 23;
                beam(pose, out, s.eye, s.endpoint, 0.018, 0.01, age * 0.04, -age * 0.1, 0x608545CF, 6);
                for (int i = 0; i < 3; i++) {
                    helix(pose, out, s.heart, s.eye, age * 0.16 + i * Math.PI * 2 / 3, 0.35 * (1 - charge) + 0.08, 0.055, 0xD9D383FF, 24);
                    double cycle = (t * 0.065 + i / 3.0) % 1;
                    ring(pose, out, s.eye.add(axis.scale(0.1 + cycle * 1.4)), axis,
                            (1 - cycle) * 1.8 + 0.12, 0.18, age * 0.06, fade(0xD5C260FF, Math.sin(cycle * Math.PI)), detail);
                }
            } else if (power > 0) {
                double radius = (0.32 + Math.sin(t * 2.4) * 0.025) * power;
                beam(pose, out, s.eye, s.endpoint, radius, radius * 0.8, age * 0.06, -age * 0.2, fade(0xDFBE48FF, power), 12);
                beam(pose, out, s.eye, s.endpoint, radius * 2.4, radius * 1.8, -age * 0.035, -age * 0.11, fade(0x428D28EC, power), 12);
                for (int i = 0; i < 2; i++) helix(pose, out, s.eye, s.endpoint, age * 0.42 + i * Math.PI,
                        radius * 1.8, 0.08 * power, fade(0xE3E6A7FF, power), s.viewerDistance > 1600 ? 32 : 72);
                for (int i = 0; i < 5; i++) {
                    double cycle = ((t - 23) * 0.16 + i / 5.0) % 1;
                    ring(pose, out, s.eye.lerp(s.endpoint, cycle), axis, radius * 2, 0.19 * power,
                            age * 0.1, fade(0xBBC386FF, power * Math.sin(cycle * Math.PI)), detail);
                }
                ring(pose, out, s.endpoint, axis, 0.6 + (t - 23) * 0.24, 0.5, age * 0.03, fade(0xDAE2B4FF, power), detail);
                ring(pose, out, s.eye, axis, 0.6 + (t - 23) * 0.14, 0.24, -age * 0.04, fade(0xE4F4D8FF, power), detail);
            }
        }
        double wave = s.action == State.ROAR ? t - 18 : s.action == State.INTRO ? t - 118 : s.action == State.DEATH ? t - 48 : -1;
        if (wave >= 0 && power > 0 && s.action != State.PULSE_RAY) {
            Vec3 floor = new Vec3(s.heart.x, 0.08, s.heart.z);
            double height = s.action == State.DEATH ? 44 : 28;
            beam(pose, out, floor, floor.add(0, height, 0), 0.9 * power, 0.1 * power, age * 0.04, -age * 0.14, fade(0xBDB765FF, power), 12);
            beam(pose, out, floor, floor.add(0, height, 0), 2 * power, 0.35 * power, -age * 0.025, -age * 0.08, fade(0x458636CA, power), 12);
            for (int i = 0; i < 3; i++) {
                double radius = wave * (s.action == State.DEATH ? 0.95 : 0.72) - i * 2;
                ring(pose, out, new Vec3(0, 0.12 + i * 0.06, 0), UP, radius, 0.7 + i * 0.15,
                        age * 0.03, fade(i == 0 ? 0xE4D69CFF : 0x957638C1, power), detail);
                helix(pose, out, floor, floor.add(0, height * 0.75, 0), age * 0.14 + i * Math.PI * 2 / 3,
                        0.8 * power, 0.08 * power, fade(0xCBDCB0FF, power), 40);
            }
        }
        if (s.action == State.INTRO && t > 65 && t < 136) {
            Vec3 normal = Vec3.directionFromRotation(0, s.bodyRot);
            double strength = Math.sin((t - 65) / 71 * Math.PI);
            Vec3 center = s.heart.add(0, 1, 0).subtract(normal.scale(1.7));
            ring(pose, out, center, normal, 3.9, 0.35, age * 0.03, fade(0xB29D44F3, strength), detail);
            ring(pose, out, center, normal, 4.35, 0.07, -age * 0.015, fade(0xD5DB9FFF, strength), detail);
        }
        if (s.action == State.PUNCH && t >= 21 && t < 30) {
            ring(pose, out, s.fist, s.fist.subtract(s.heart).normalize(), (t - 21) * 0.35, 0.35,
                    age * 0.05, fade(0xE5DD9BFF, (30 - t) / 9), detail);
        }
        if (s.blockFlash > 0) {
            Vec3 axis = Vec3.directionFromRotation(0, s.bodyRot);
            ring(pose, out, s.heart.add(axis.scale(1.15)), axis, 0.7 + (8 - s.blockFlash) * 0.12,
                    0.24, age * 0.04, fade(0xF3E5C4FF, s.blockFlash / 8.0), detail);
        }
        if (s.cloudTicks > 0 && s.action != State.DEATH) {
            infectionRing(s, pose, out);
        }
    }

    private static void infectionRing(HeartOfLunaRenderState s, PoseStack.Pose pose, VertexConsumer out) {
        if (s.cloudRings.length != 3) return;
        double partial = s.ageInTicks - Math.floor(s.ageInTicks);
        double remaining = Mth.clamp((s.cloudTicks - partial) / 100, 0, 1);
        double strength = Math.min(1, remaining * 8) * Math.min(1, (1 - remaining) * 10);
        Vec3 origin = new Vec3(s.x, s.y, s.z);
        int segments = s.cloudRings[0].length - 1;
        for (int band = 0; band < 2; band++) {
            for (int i = 0; i < segments; i++) {
                Vec3 a = s.cloudRings[band][i], b = s.cloudRings[band + 1][i];
                Vec3 c = s.cloudRings[band + 1][i + 1], d = s.cloudRings[band][i + 1];
                if (a == null || b == null || c == null || d == null) continue;
                if (Math.max(Math.max(a.y, b.y), Math.max(c.y, d.y)) - Math.min(Math.min(a.y, b.y), Math.min(c.y, d.y)) > 1.25) continue;
                double angle = i / (double) segments;
                double pulse = 0.65 + 0.35 * Math.sin(angle * Math.PI * 6 - s.ageInTicks * 0.045);
                double life = angle < remaining ? 1 : 0.23;
                int color = fade(band == 0 ? 0x917A36AE : 0xDBCD95F5, strength * pulse * (band == 0 ? 0.6 : life));
                double scroll = s.ageInTicks * (band == 0 ? 0.009 : -0.014);
                quad(pose, out, a.subtract(origin), b.subtract(origin), c.subtract(origin), d.subtract(origin), color,
                        0, angle * 12 + scroll, 1, (i + 1) * 12.0 / segments + scroll);
                if (band == 1 && i % 8 == 0) {
                    Vec3 base = b.subtract(origin);
                    beam(pose, out, base, base.add(0, 0.22 + pulse * 0.13, 0), 0.035, 0.008,
                            angle * Math.PI * 2, scroll, fade(0xE4E6BCFF, strength * life * 0.75), 4);
                }
            }
        }
    }

    private static void glows(HeartOfLunaRenderState s, PoseStack.Pose pose, VertexConsumer out, Vec3 viewer) {
        double t = s.actionTicks, age = s.ageInTicks, power = release(s);
        double life = s.action == State.DEATH ? Mth.clamp((50 - t) / 15, 0, 1) : s.action == State.INTRO ? Mth.clamp((t - 35) / 50, 0, 1) : 1;
        halo(pose, out, s.heart, viewer, 0.95 + s.glow * 0.35, fade(0x888E2CE0, life));
        for (Vec3 eye : new Vec3[]{s.eye, s.leftEye, s.rightEye}) {
            halo(pose, out, eye, viewer, 0.36 + s.glow * 0.16, fade(0xB3BA59FD, life));
        }
        if (s.action == State.PULSE_RAY) {
            double charge = t < 23 ? t / 23 : power;
            halo(pose, out, s.eye, viewer, 0.4 + charge * 1.5, fade(0xD2B64BFF, charge));
            halo(pose, out, s.eye, viewer, 0.12 + charge * 0.5, fade(0xF8F5DCFF, charge));
            if (power > 0) {
                flare(pose, out, s.eye, viewer, 2.6 * power, fade(0xD2D997FF, power));
                halo(pose, out, s.endpoint, viewer, 2.7 * power, fade(0xDBC66EFF, power));
                halo(pose, out, s.endpoint, viewer, 0.85 * power, fade(0xFFF6E4FF, power));
                flare(pose, out, s.endpoint, viewer, 3.7 * power, fade(0xDDE8B7FF, power));
            }
        } else if (power > 0) {
            halo(pose, out, s.heart, viewer, 5 * power, fade(0xAB9E38E7, power));
            flare(pose, out, s.heart, viewer, 7 * power, fade(0xDBE5AFFF, power));
        }
        if (s.action == State.DEATH && t >= 28 && t < 48) {
            double collapse = (48 - t) / 20;
            halo(pose, out, s.heart, viewer, 2.6 * collapse + 0.12, 0xC19539E2);
            halo(pose, out, s.heart, viewer, 0.8 * collapse + 0.05, 0xFCEED5FF);
            for (int i = 0; i < 6; i++) {
                double a = i * Math.PI / 3 + age * 0.12;
                Vec3 point = s.heart.add(Math.cos(a) * collapse * 3, Math.sin(a * 2) * collapse, Math.sin(a) * collapse * 3);
                halo(pose, out, point, viewer, 0.2 * collapse, 0xD6DD97FF);
            }
        }
        if (s.cloudTicks > 0 && s.action != State.DEATH) {
            double opacity = Math.min(1, s.cloudTicks / 30.0) * Math.min(1, (100 - s.cloudTicks) / 15.0);
            int count = s.viewerDistance > 1600 ? 8 : 16;
            for (int i = 0; i < count; i++) {
                double a = i * 2.39996 + age * 0.008;
                double radius = 2 + (i % 4) * 1.1;
                Vec3 p = s.cloudCenter.add(Math.cos(a) * radius, 0.45 + Math.sin(age * 0.025 + i) * 0.25, Math.sin(a) * radius);
                billboard(pose, out, p, viewer, 2.1, 0.6, fade(0x287C2DAB, opacity));
                halo(pose, out, p.add(0, 0.4 + (i % 3) * 0.4, 0), viewer, 0.08, fade(0xBFA85CD9, opacity));
            }
        }
    }

    private static void flare(PoseStack.Pose pose, VertexConsumer out, Vec3 center, Vec3 viewer, double size, int color) {
        billboard(pose, out, center, viewer, size, size * 0.075, color);
        billboard(pose, out, center, viewer, size * 0.045, size * 0.55, fade(color, 0.65));
    }

    private static void halo(PoseStack.Pose pose, VertexConsumer out, Vec3 center, Vec3 viewer, double size, int color) {
        billboard(pose, out, center, viewer, size, size, color);
    }

    private static void billboard(PoseStack.Pose pose, VertexConsumer out, Vec3 center, Vec3 viewer, double width, double height, int color) {
        if (width <= 0 || height <= 0 || color >>> 24 == 0) return;
        Vec3 axis = viewer.subtract(center).normalize();
        Vec3 u = perpendicular(axis).scale(width), v = axis.cross(u).normalize().scale(height);
        quad(pose, out, center.subtract(u).subtract(v), center.add(u).subtract(v), center.add(u).add(v), center.subtract(u).add(v), color, 0, 0, 1, 1);
    }

    private static void beam(PoseStack.Pose pose, VertexConsumer out, Vec3 a, Vec3 b, double radius, double endRadius, double spin, double scroll, int color, int sides) {
        if (radius <= 0 || a.distanceToSqr(b) < 0.0001) return;
        Vec3 axis = b.subtract(a).normalize(), u = perpendicular(axis), v = axis.cross(u);
        double length = a.distanceTo(b) * 0.4;
        for (int i = 0; i < sides; i++) {
            double angle = i * Math.PI * 2 / sides + spin, next = (i + 1) * Math.PI * 2 / sides + spin;
            Vec3 p = u.scale(Math.cos(angle)).add(v.scale(Math.sin(angle)));
            Vec3 q = u.scale(Math.cos(next)).add(v.scale(Math.sin(next)));
            quad(pose, out, a.add(p.scale(radius)), a.add(q.scale(radius)), b.add(q.scale(endRadius)), b.add(p.scale(endRadius)), color, 0, scroll, 1, scroll + length);
        }
    }

    private static void helix(PoseStack.Pose pose, VertexConsumer out, Vec3 from, Vec3 to, double time, double radius, double width, int color, int segments) {
        Vec3 axis = to.subtract(from).normalize(), u = perpendicular(axis), v = axis.cross(u);
        Vec3 previous = from;
        for (int i = 1; i <= segments; i++) {
            double p = i / (double) segments, angle = p * Math.PI * 6 - time;
            double r = radius * Math.sin(p * Math.PI);
            Vec3 next = from.lerp(to, p).add(u.scale(Math.cos(angle) * r)).add(v.scale(Math.sin(angle) * r));
            Vec3 edge = u.scale(Math.cos(angle) * width).add(v.scale(Math.sin(angle) * width));
            quad(pose, out, previous.subtract(edge), previous.add(edge), next.add(edge), next.subtract(edge), color, 0, p * 4 - time, 1, p * 4 + 4.0 / segments - time);
            previous = next;
        }
    }

    private static void ring(PoseStack.Pose pose, VertexConsumer out, Vec3 center, Vec3 axis, double radius, double width, double scroll, int color, int sides) {
        if (radius <= 0 || color >>> 24 == 0) return;
        Vec3 u = perpendicular(axis), v = axis.cross(u).normalize();
        for (int i = 0; i < sides; i++) {
            double angle = i * Math.PI * 2 / sides, next = (i + 1) * Math.PI * 2 / sides;
            Vec3 p = u.scale(Math.cos(angle)).add(v.scale(Math.sin(angle)));
            Vec3 q = u.scale(Math.cos(next)).add(v.scale(Math.sin(next)));
            quad(pose, out, center.add(p.scale(radius)), center.add(p.scale(radius + width)), center.add(q.scale(radius + width)), center.add(q.scale(radius)),
                    color, 0, i * 8.0 / sides + scroll, 1, (i + 1) * 8.0 / sides + scroll);
        }
    }

    private static Vec3 perpendicular(Vec3 axis) { return axis.cross(Math.abs(axis.y) > 0.9 ? new Vec3(1, 0, 0) : UP).normalize(); }
    private static int fade(int color, double alpha) { return ((int) ((color >>> 24) * Mth.clamp(alpha, 0, 1)) << 24) | (color & 0xFFFFFF); }

    private static void quad(PoseStack.Pose pose, VertexConsumer out, Vec3 a, Vec3 b, Vec3 c, Vec3 d, int color, double u0, double v0, double u1, double v1) {
        vertex(pose, out, a, color, u0, v0); vertex(pose, out, b, color, u1, v0);
        vertex(pose, out, c, color, u1, v1); vertex(pose, out, d, color, u0, v1);
        vertex(pose, out, d, color, u0, v1); vertex(pose, out, c, color, u1, v1);
        vertex(pose, out, b, color, u1, v0); vertex(pose, out, a, color, u0, v0);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer out, Vec3 p, int color, double u, double v) {
        out.addVertex(pose, (float) p.x, (float) p.y, (float) p.z).setColor(color).setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0, 1, 0);
    }

    private HeartOfLunaWorldEffects() {}
}
