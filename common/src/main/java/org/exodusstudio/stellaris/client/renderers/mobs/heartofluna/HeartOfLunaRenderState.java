package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity;

public final class HeartOfLunaRenderState extends LivingEntityRenderState {
    public HeartOfLunaBossEntity.State action = HeartOfLunaBossEntity.State.IDLE;
    public int animation;
    public float animationTicks;
    public float actionTicks;
    public float health;
    public float glow;
    public float overcharge;
    public int previousAnimation = 1;
    public float previousTicks;
    public float blend = 1;
    public boolean buffed;
    public int blockFlash;
    public Vec3 eye = Vec3.ZERO;
    public Vec3 heart = Vec3.ZERO;
    public Vec3 leftEye = Vec3.ZERO;
    public Vec3 rightEye = Vec3.ZERO;
    public Vec3 fist = Vec3.ZERO;
    public Vec3 endpoint = Vec3.ZERO;
    public Vec3 cloudCenter = Vec3.ZERO;
    public int cloudTicks;
    public Vec3[][] cloudRings = new Vec3[0][];
    public double viewerDistance;
}
