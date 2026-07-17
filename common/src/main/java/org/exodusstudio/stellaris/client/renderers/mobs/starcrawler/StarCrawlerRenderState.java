package org.exodusstudio.stellaris.client.renderers.mobs.starcrawler;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawler.StarCrawlerEntity.AttackState;

public class StarCrawlerRenderState extends LivingEntityRenderState {
    public final AnimationState attackAnimationState = new AnimationState();

    public AttackState attackState = AttackState.NORMAL;
    public float attackStateTicks;
    public float spinAngle;
    public float spinVelocity;
    public float leanX;
    public float leanZ;
    public float movementSpeed;
}
