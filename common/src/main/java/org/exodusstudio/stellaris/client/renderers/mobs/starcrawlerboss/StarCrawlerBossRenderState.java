package org.exodusstudio.stellaris.client.renderers.mobs.starcrawlerboss;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.CombatState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.IntroState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.DeathCinematicState;

public class StarCrawlerBossRenderState extends LivingEntityRenderState {
    public final AnimationState walkingAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState chargeAnimationState = new AnimationState();
    public final AnimationState jumpSlamAnimationState = new AnimationState();
    public final AnimationState groundSmashAnimationState = new AnimationState();
    public final AnimationState healingAnimationState = new AnimationState();

    public CombatState combatState = CombatState.IDLE;
    public IntroState introState = IntroState.NOT_STARTED;
    public float introTicks;
    public DeathCinematicState deathCinematicState = DeathCinematicState.ALIVE;
    public float deathCinematicTicks;
    public float crystalEnergy = 1.0F;
}
