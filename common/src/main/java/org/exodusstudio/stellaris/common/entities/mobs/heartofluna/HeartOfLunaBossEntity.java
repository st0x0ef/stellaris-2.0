// fishguy was here!
package org.exodusstudio.stellaris.common.entities.mobs.heartofluna;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.InfectionUtils;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class HeartOfLunaBossEntity extends Monster {
    public enum State {
        IDLE(1, 0), WALK(0, 0), BLOCK(2, 17), ROAR(3, 40), PUNCH(5, 30),
        PULSE_RAY(4, 40 + HeartOfLunaCombatRules.RAY_CHARGE_DELAY), INTRO(1, 200), DEATH(6, 140);

        public final int animation;
        public final int duration;

        State(int animation, int duration) {
            this.animation = animation;
            this.duration = duration;
        }
    }

    public static final float MAX_HEALTH = 550.0F;
    public static final int CLOUD_LIFETIME = 100;
    public static final double CLOUD_RADIUS = 7.0;
    public static final int INFECTION_DURATION = 600;
    private static final EntityDataAccessor<String> HUD_ID = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Vector3fc> CLOUD_CENTER = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ELAPSED = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RAYS = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BUFF = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CLOUD = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BLOCK_FLASH = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Vector3fc> RAY_END = SynchedEntityData.defineId(HeartOfLunaBossEntity.class, EntityDataSerializers.VECTOR3);
    private final ServerBossEvent bossEvent = new ServerBossEvent(UUID.randomUUID(), Component.empty(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
    private final Set<UUID> rayHits = new HashSet<>();
    private final HeartOfLunaAnimationClock animationClock = new HeartOfLunaAnimationClock();
    private Vec3 lockedAim = Vec3.ZERO;
    private Vec3 cloudCenter = Vec3.ZERO;
    private UUID rayTarget;
    private UUID killer;
    private boolean introPlayed;
    private boolean finalized;
    private boolean applyingDamage;
    private int attackCooldown = 30;
    private int blockCooldown;
    private int roarCooldown = 100;
    private int activeRay;
    private int rayCooldown;
    private boolean rayExploded;
    private int targetReview;
    private int repathCooldown;
    private int stuckTicks;
    private int stillTicks;
    private int flankTicks;
    private int flankCooldown;
    private Vec3 lastProgressPosition = Vec3.ZERO;

    public HeartOfLunaBossEntity(EntityType<? extends HeartOfLunaBossEntity> type, Level level) {
        super(type, level);
        xpReward = 150;
        setPersistenceRequired();
        entityData.set(HUD_ID, bossEvent.getId().toString());
        setHealth(MAX_HEALTH);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, 0.23).add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.ATTACK_DAMAGE, 14)
                .add(Attributes.STEP_HEIGHT, 1.5).add(Attributes.ARMOR, 0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HUD_ID, "");
        builder.define(CLOUD_CENTER, new Vector3f());
        builder.define(STATE, State.IDLE.ordinal());
        builder.define(ELAPSED, 0);
        builder.define(RAYS, 0);
        builder.define(BUFF, 0);
        builder.define(CLOUD, 0);
        builder.define(BLOCK_FLASH, 0);
        builder.define(RAY_END, new Vector3f());
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public State state() { return State.values()[entityData.get(STATE)]; }
    public int elapsed() { return entityData.get(ELAPSED); }
    public int raysCompleted() { return entityData.get(RAYS); }
    public boolean buffed() { return entityData.get(BUFF) > 0; }
    public int cloudTicks() { return entityData.get(CLOUD); }
    public int blockFlash() { return entityData.get(BLOCK_FLASH); }
    public UUID bossEventId() { return uuid(entityData.get(HUD_ID)); }
    public Vec3 rayEnd() { return position().add(new Vec3(entityData.get(RAY_END))); }
    public boolean cinematic() { return state() == State.INTRO || state() == State.DEATH; }
    public int animation() {
        int ticks = elapsed() - (level().isClientSide() ? 1 : 0);
        return state() == State.INTRO && ticks >= 100 && ticks < 140 ? 3 : state().animation;
    }
    public float animationTicks(float partial) {
        float ticks = effectTicks(partial);
        return state() == State.INTRO && ticks >= 140 ? ticks - 140 : state() == State.INTRO && ticks >= 100 ? ticks - 100
                : state() == State.DEATH ? Math.min(35, ticks) : ticks;
    }

    public float effectTicks(float partial) {
        float ticks = Math.max(0, elapsed() - (level().isClientSide() ? 1 : 0) + partial);
        return state() == State.PULSE_RAY ? HeartOfLunaCombatRules.rayAnimationTicks(ticks) : ticks;
    }

    public HeartOfLunaAnimationClock.Frame animationFrame(float partial) {
        return animationClock.sample(state().ordinal(), animation(), animationTicks(partial), tickCount + partial);
    }

    public Vec3 anchor(int bone, float partial) {
        float yaw = level().isClientSide() ? Mth.rotLerp(partial, yRotO, getYRot()) : getYRot();
        return position().add(HeartOfLunaPose.local(animation(), animationTicks(partial), bone).yRot(-yaw * Mth.DEG_TO_RAD));
    }

    private void begin(State next) {
        navigation.stop();
        setDeltaMovement(0, getDeltaMovement().y, 0);
        entityData.set(STATE, next.ordinal());
        entityData.set(ELAPSED, 0);
        HeartOfLunaCinematicManager.tickBoss(this);
        if (next == State.PULSE_RAY) {
            activeRay = HeartOfLunaCombatRules.phaseAfterRay(getHealth(), raysCompleted());
            rayExploded = false;
            rayHits.clear();
            rayTarget = valid(getTarget()) ? getTarget().getUUID() : null;
            lockedAim = valid(getTarget()) ? getTarget().getEyePosition() : anchor(HeartOfLunaPose.EYE, 0).add(getLookAngle().scale(40));
        }
    }

    @Override
    public void tick() {
        if (!level().isClientSide() && finalized) {
            discard();
            return;
        }
        if (state().duration > 0) {
            navigation.stop();
            setDeltaMovement(0, getDeltaMovement().y, 0);
        }
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;
        HeartOfLunaCinematicManager.tickBoss(this);
        if (state() == State.DEATH) {
            setDeltaMovement(Vec3.ZERO);
            resetFallDistance();
        }
        if (attackCooldown > 0) attackCooldown--;
        if (blockCooldown > 0) blockCooldown--;
        if (roarCooldown > 0) roarCooldown--;
        if (rayCooldown > 0 && state() != State.PULSE_RAY) rayCooldown--;
        if (entityData.get(BUFF) > 0) entityData.set(BUFF, entityData.get(BUFF) - 1);
        if (blockFlash() > 0) entityData.set(BLOCK_FLASH, blockFlash() - 1);
        bossEvent.setProgress(state() == State.DEATH ? 0 : getHealth() / MAX_HEALTH);
        bossEvent.setVisible(state() != State.INTRO);
        tickCloud(server);
        if (!introPlayed && state().duration == 0) {
            ServerPlayer witness = null;
            double nearest = 32 * 32;
            for (ServerPlayer player : server.players()) {
                double distance = distanceToSqr(player);
                if (player.isAlive() && !player.isSpectator() && distance < nearest && hasLineOfSight(player)) {
                    witness = player;
                    nearest = distance;
                }
            }
            if (witness != null) {
                introPlayed = true;
                face(witness.position(), 360);
                begin(State.INTRO);
                bossEvent.setVisible(false);
            }
        }
        if (state().duration == 0) {
            reviewTarget(server);
            if (thresholdPending()) {
                begin(State.PULSE_RAY);
            } else if (valid(getTarget())) {
                decide(server, getTarget());
            } else {
                navigation.stop();
                if (state() != State.IDLE) begin(State.IDLE);
            }
        }
        int tick = elapsed();
        int heartbeatPeriod = state() == State.INTRO ? Math.max(8, 34 - tick / 5)
                : state() == State.DEATH ? 14 + tick / 2 : buffed() ? 16 : getHealth() <= 20 ? 9 : getHealth() < 200 ? 20 : 34;
        if (tickCount % heartbeatPeriod == 0 && (state() != State.DEATH || tick < 46)) {
            HeartOfLunaVfx.sound(this, SoundEvents.WARDEN_HEARTBEAT, 0.65F, state() == State.DEATH ? 0.55F : 0.7F);
        }
        switch (state()) {
            case PUNCH -> {
                if (tick < 16 && valid(getTarget())) face(getTarget().position().add(HeartOfLunaCombatRules.lead(getTarget().getDeltaMovement(), Math.min(5, 21 - tick))), 6);
                if (tick == 21) punch(server);
            }
            case BLOCK -> {
                if (tick == 1) HeartOfLunaVfx.sound(this, SoundEvents.AMETHYST_BLOCK_RESONATE, 0.8F, 0.6F);
            }
            case ROAR -> {
                if (tick == 18) {
                    entityData.set(BUFF, 300 + random.nextInt(101));
                    roarCooldown = 480;
                    startCloud();
                    HeartOfLunaVfx.impact(this, anchor(HeartOfLunaPose.MOUTH, 0), 2);
                }
            }
            case PULSE_RAY -> tickRay(server, tick);
            case INTRO -> {
                if (tick == 118) {
                    startCloud();
                    HeartOfLunaVfx.impact(this, anchor(HeartOfLunaPose.MOUTH, 0), 2);
                }
            }
            case DEATH -> {
                if (tick == 0 || tick == 12 || tick == 48) HeartOfLunaVfx.impact(this, anchor(HeartOfLunaPose.HEART, 0), tick == 48 ? 4 : 2);
                if (tick >= State.DEATH.duration) {
                    HeartOfLunaCinematicManager.releaseBoss(this, true);
                    finalized = true;
                    if (killer != null) setLastHurtByPlayer(killer, 100);
                    setHealth(0);
                    super.die(killer != null && server.getEntity(killer) instanceof Player player ? damageSources().playerAttack(player) : damageSources().generic());
                    bossEvent.removeAllPlayers();
                    return;
                }
            }
            default -> {}
        }
        if (state().duration > 0 && state() != State.DEATH && tick >= state().duration - 1) {
            if (state() == State.PULSE_RAY) {
                entityData.set(RAYS, Math.max(raysCompleted(), activeRay));
                rayCooldown = HeartOfLunaCombatRules.repeatRayCooldown(raysCompleted()) + random.nextInt(21);
            }
            attackCooldown = state() == State.BLOCK ? 12 : 24 + random.nextInt(15);
            begin(State.IDLE);
        } else {
            entityData.set(ELAPSED, tick + 1);
        }
    }

    private void startCloud() {
        cloudCenter = position();
        entityData.set(CLOUD_CENTER, cloudCenter.toVector3f());
        entityData.set(CLOUD, CLOUD_LIFETIME);
    }

    private void tickCloud(ServerLevel server) {
        if (cloudTicks() <= 0 || state() == State.DEATH) return;
        entityData.set(CLOUD, cloudTicks() - 1);
        if (cloudTicks() % 20 != 0) return;
        for (ServerPlayer player : server.players()) {
            if (valid(player) && player.position().distanceToSqr(cloudCenter) <= CLOUD_RADIUS * CLOUD_RADIUS) {
                InfectionUtils.infect(player, INFECTION_DURATION);
            }
        }
    }

    public Vec3 cloudCenter() { return level().isClientSide() ? new Vec3(entityData.get(CLOUD_CENTER)) : cloudCenter; }

    private void decide(ServerLevel server, LivingEntity target) {
        double distance = distanceTo(target);
        boolean visible = hasLineOfSight(target);
        Vec3 predicted = target.position().add(HeartOfLunaCombatRules.lead(target.getDeltaMovement(), distance > 6 ? 8 : 4));
        face(predicted, distance < 6 ? 7 : 5);
        if (flankCooldown > 0) flankCooldown--;
        if (flankTicks > 0) flankTicks--;
        boolean meleeThreat = visible && distance < 4 && target.swinging && inFront(target.position())
                && HeartOfLunaCombatRules.faces(target.getLookAngle(), position().subtract(target.position()));
        if (attackCooldown <= 8 && blockCooldown <= 0 && (meleeThreat || incomingProjectile(server))) {
            blockCooldown = 60 + random.nextInt(20);
            begin(State.BLOCK);
            return;
        }
        int nearby = 0;
        if (roarCooldown <= 0) for (ServerPlayer player : server.players()) {
            if (valid(player) && distanceToSqr(player) < 49) nearby++;
        }
        boolean canStrike = visible && punchReaches(target);
        if (attackCooldown <= 0 && rayCooldown <= 0 && raysCompleted() > 0 && visible && distance <= 40) {
            begin(State.PULSE_RAY);
            return;
        }
        if (attackCooldown <= 0 && roarCooldown <= 0 && !buffed() && visible && distance < 12
                && (nearby > 1 || target.isBlocking() || distance > 5 || getHealth() < 200)) {
            begin(State.ROAR);
            return;
        }
        if (target.isBlocking() && visible && distance < 6 && flankCooldown == 0) {
            flankTicks = 16;
            flankCooldown = 90;
            repathCooldown = 0;
        }
        if (attackCooldown <= 0 && canStrike && flankTicks == 0) {
            begin(State.PUNCH);
            HeartOfLunaVfx.sound(this, SoundEvents.WARDEN_ATTACK_IMPACT, 0.6F, 0.45F);
            return;
        }
        if (!canStrike || distance > 3.2 || flankTicks > 0) {
            if (tickCount % 10 == 0) {
                stuckTicks = position().distanceToSqr(lastProgressPosition) < 0.04 ? stuckTicks + 10 : 0;
                lastProgressPosition = position();
            }
            if (--repathCooldown <= 0) {
                repathCooldown = visible ? 8 : 16;
                Vec3 approach = predicted;
                if (flankTicks > 0 || stuckTicks >= 30) {
                    double direction = (getId() & 1) == 0 ? 1 : -1;
                    Vec3 side = target.position().subtract(position()).horizontal().normalize().yRot(Mth.HALF_PI).scale(direction * (stuckTicks >= 30 ? 3 : 2.2));
                    approach = (stuckTicks >= 30 ? position() : predicted).add(side);
                }
                if (!navigation.moveTo(approach.x, target.getY(), approach.z, buffed() ? 1.15 : 1)) stuckTicks = Math.max(stuckTicks, 30);
                if (stuckTicks >= 60) stuckTicks = 0;
            }
            if (getDeltaMovement().horizontalDistanceSqr() > 0.0003) {
                stillTicks = 0;
                if (state() != State.WALK) beginLocomotion(State.WALK);
            } else if (++stillTicks > 5 && state() != State.IDLE) beginLocomotion(State.IDLE);
        } else {
            navigation.stop();
            stuckTicks = 0;
            repathCooldown = 0;
            if (state() != State.IDLE) begin(State.IDLE);
        }
    }

    private boolean punchReaches(LivingEntity target) {
        if (!inFront(target.position()) || distanceToSqr(target) > 30) return false;
        Vec3 from = position().add(HeartOfLunaPose.local(State.PUNCH.animation, 21, HeartOfLunaPose.LEFT_HAND).yRot(-getYRot() * Mth.DEG_TO_RAD));
        Vec3 to = position().add(HeartOfLunaPose.local(State.PUNCH.animation, 21.5F, HeartOfLunaPose.LEFT_HAND).yRot(-getYRot() * Mth.DEG_TO_RAD));
        AABB targetBox = target.getBoundingBox().move(HeartOfLunaCombatRules.lead(target.getDeltaMovement(), 4)).inflate(1.3);
        return targetBox.contains(from) || targetBox.clip(from, to).isPresent();
    }

    private void reviewTarget(ServerLevel server) {
        LivingEntity current = getTarget();
        if (valid(current) && distanceToSqr(current) < 4096 && --targetReview > 0) return;
        targetReview = 30;
        LivingEntity best = valid(current) && distanceToSqr(current) < 4096 ? current : null;
        double score = best == null ? Double.MAX_VALUE : distanceTo(best) * 0.7 + (hasLineOfSight(best) ? 0 : 20);
        for (ServerPlayer player : server.players()) {
            if (!valid(player) || distanceToSqr(player) >= 4096) continue;
            double candidate = distanceTo(player) + (hasLineOfSight(player) ? 0 : 20);
            if (candidate < score) { best = player; score = candidate; }
        }
        if (best != current) { setTarget(best); navigation.stop(); repathCooldown = 0; }
    }

    private void beginLocomotion(State next) {
        entityData.set(STATE, next.ordinal());
        entityData.set(ELAPSED, 0);
    }

    private boolean incomingProjectile(ServerLevel server) {
        if (tickCount % 3 != 0) return false;
        for (Projectile projectile : server.getEntitiesOfClass(Projectile.class, getBoundingBox().inflate(10))) {
            if (projectile.getOwner() != this && inFront(projectile.position()) && hasLineOfSight(projectile)
                    && HeartOfLunaCombatRules.projectileThreat(getBoundingBox(), projectile.position(), projectile.getDeltaMovement())) return true;
        }
        return false;
    }

    private void face(Vec3 point, float speed) {
        Vec3 delta = point.subtract(position());
        float yaw = (float) (Mth.atan2(-delta.x, delta.z) * Mth.RAD_TO_DEG);
        setYRot(Mth.rotLerp(Math.min(1, speed / Math.max(speed, Math.abs(Mth.wrapDegrees(yaw - getYRot())))), getYRot(), yaw));
        yBodyRot = getYRot();
        setYHeadRot(getYRot());
    }

    private boolean inFront(Vec3 point) {
        return HeartOfLunaCombatRules.faces(Vec3.directionFromRotation(0, getYRot()), point.subtract(position()));
    }

    private static boolean valid(LivingEntity target) {
        return target != null && target.isAlive() && !target.isRemoved()
                && (!(target instanceof Player player) || !player.isCreative() && !player.isSpectator());
    }

    private void punch(ServerLevel server) {
        Vec3 fist = anchor(HeartOfLunaPose.LEFT_HAND, 0);
        Vec3 next = position().add(HeartOfLunaPose.local(State.PUNCH.animation, 21.5F, HeartOfLunaPose.LEFT_HAND).yRot(-getYRot() * Mth.DEG_TO_RAD));
        for (LivingEntity target : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5), HeartOfLunaBossEntity::valid)) {
            if (target == this || !inFront(target.position()) || distanceTo(target) > 7 || !hasLineOfSight(target)) continue;
            if (target.getBoundingBox().inflate(1.3).contains(fist) || target.getBoundingBox().inflate(1.3).clip(fist, next).isPresent()) {
                target.hurtServer(server, damageSources().mobAttack(this), HeartOfLunaCombatRules.punchDamage(buffed()));
                target.knockback(0.7, getX() - target.getX(), getZ() - target.getZ());
                HeartOfLunaVfx.shake(target, 6, 0.4F);
            }
        }
        HeartOfLunaVfx.impact(this, fist, 1);
    }

    private void tickRay(ServerLevel server, int tick) {
        LivingEntity target = rayTarget != null && server.getEntity(rayTarget) instanceof LivingEntity living && valid(living) ? living : null;
        if (tick < 18 && target == null && valid(getTarget())) {
            target = getTarget();
            rayTarget = target.getUUID();
        }
        if (tick == 18 && activeRay > raysCompleted()) {
            for (ServerPlayer player : server.players()) {
                if (valid(player) && player.distanceToSqr(this) <= 25) HeartOfLunaMovementLock.repel(this, player);
            }
        }
        if (tick <= 22 && target != null) {
            lockedAim = tick >= 18 && activeRay > raysCompleted() && target instanceof ServerPlayer player ? HeartOfLunaMovementLock.aimPosition(player) : target.getEyePosition();
            face(lockedAim, 8);
        }
        Vec3 origin = anchor(HeartOfLunaPose.EYE, 0);
        Vec3 direction = lockedAim.subtract(origin).normalize();
        Vec3 end = rayExploded ? rayEnd() : server.clip(new ClipContext(origin, origin.add(direction.scale(48)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation();
        entityData.set(RAY_END, end.subtract(position()).toVector3f());
        if (tick == 0) HeartOfLunaVfx.sound(this, SoundEvents.WARDEN_SONIC_CHARGE, 2, 0.6F);
        if (tick == HeartOfLunaCombatRules.RAY_FIRE_TICK) HeartOfLunaVfx.impact(this, origin, 3);
        if (tick < HeartOfLunaCombatRules.RAY_FIRE_TICK || tick > HeartOfLunaCombatRules.RAY_END_TICK) return;
        DamageSource source = new DamageSource(server.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(IdentifierUtils.resourceKey(Registries.DAMAGE_TYPE, "lunar_pulse")), this);
        AABB bounds = new AABB(origin, end).inflate(0.65);
        for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, bounds, HeartOfLunaBossEntity::valid)) {
            if (victim == this || rayHits.contains(victim.getUUID()) || victim.getBoundingBox().inflate(0.65).clip(origin, end).isEmpty()) continue;
            Vec3 center = victim.getBoundingBox().getCenter();
            Vec3 obstruction = server.clip(new ClipContext(origin, center, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation();
            if (obstruction.distanceToSqr(origin) + 0.01 < center.distanceToSqr(origin)) continue;
            rayHits.add(victim.getUUID());
            boolean shield = victim.isBlocking() && HeartOfLunaCombatRules.faces(victim.getLookAngle(), origin.subtract(victim.position()));
            victim.hurtServer(server, source, HeartOfLunaCombatRules.rayDamage(buffed(), shield));
            HeartOfLunaVfx.shake(victim, 10, 0.8F);
            HeartOfLunaVfx.sparks(server, victim.getEyePosition());
        }
        if (!rayExploded) {
            rayExploded = true;
            explodeRayImpact(server, end);
        }
    }

    private void explodeRayImpact(ServerLevel server, Vec3 impact) {
        boolean griefing = server.getGameRules().get(GameRules.MOB_GRIEFING);
        ExplosionDamageCalculator damage = new ExplosionDamageCalculator() {
            @Override
            public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
                return entity != HeartOfLunaBossEntity.this && !rayHits.contains(entity.getUUID())
                        && (!(entity instanceof Player player) || !player.isCreative() && !player.isSpectator());
            }

            @Override
            public float getEntityDamageAmount(Explosion explosion, Entity entity, float exposure) {
                return Math.min(buffed() ? 12 : 8, super.getEntityDamageAmount(explosion, entity, exposure));
            }

            @Override
            public float getKnockbackMultiplier(Entity entity) {
                return entity == HeartOfLunaBossEntity.this || rayHits.contains(entity.getUUID()) ? 0 : 0.5F;
            }
        };
        server.explode(this, damageSources().explosion(this, this), damage, impact, buffed() ? 2.8F : 2.3F, griefing, Level.ExplosionInteraction.MOB);
        if (!griefing) return;
        BlockPos center = BlockPos.containing(impact);
        int placed = 0;
        for (int i = 0; i < 12 && placed < 5; i++) {
            double angle = i * Math.PI / 6;
            BlockPos column = center.offset((int)Math.round(Math.cos(angle) * 2), 2, (int)Math.round(Math.sin(angle) * 2));
            for (int down = 0; down < 6; down++) {
                BlockPos pos = column.below(down);
                if (!server.hasChunkAt(pos) || !server.getWorldBorder().isWithinBounds(pos)) continue;
                var fire = BaseFireBlock.getState(server, pos);
                if (server.isEmptyBlock(pos) && fire.canSurvive(server, pos) && server.getBlockState(pos.below()).isSolidRender()) {
                    server.setBlockAndUpdate(pos, fire);
                    placed++;
                    break;
                }
            }
        }
    }

    private boolean thresholdPending() {
        return HeartOfLunaCombatRules.thresholdPending(getHealth(), raysCompleted());
    }

    @Override
    public boolean hurtServer(ServerLevel server, DamageSource source, float amount) {
        if (state() == State.DEATH || state() == State.PULSE_RAY || state() == State.INTRO) return false;
        Vec3 from = source.getSourcePosition();
        if (state() == State.BLOCK && elapsed() >= 2 && elapsed() <= 14 && from != null && inFront(from)
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (source.getDirectEntity() instanceof Projectile projectile) {
                HeartOfLunaVfx.sparks(server, projectile.position());
                projectile.discard();
            } else if (source.getEntity() instanceof LivingEntity attacker) {
                Vec3 shove = attacker.position().subtract(position()).horizontal().normalize().scale(1.15);
                attacker.setDeltaMovement(shove.x, 0.25, shove.z);
                attacker.hurtMarked = true;
                HeartOfLunaVfx.shake(attacker, 6, 0.45F);
            }
            entityData.set(BLOCK_FLASH, 8);
            HeartOfLunaVfx.sound(this, SoundEvents.SHIELD_BLOCK.value(), 1.5F, 0.55F);
            return false;
        }
        boolean result = super.hurtServer(server, source, amount);
        if (result && source.getEntity() instanceof Player player) {
            killer = player.getUUID();
            if (valid(player) && distanceToSqr(player) < 4096 && state().duration == 0) {
                setTarget(player);
                targetReview = 50;
                repathCooldown = 0;
            }
        }
        if (state() != State.DEATH && thresholdPending()) begin(State.PULSE_RAY);
        return result;
    }

    @Override
    protected void actuallyHurt(ServerLevel server, DamageSource source, float amount) {
        applyingDamage = true;
        try {
            super.actuallyHurt(server, source, amount);
        } finally {
            applyingDamage = false;
        }
    }

    @Override
    public void setHealth(float health) {
        if (applyingDamage) {
            health = HeartOfLunaCombatRules.gatedHealth(health, raysCompleted());
        }
        super.setHealth(health);
    }

    @Override
    public void die(DamageSource source) {
        if (level().isClientSide() || finalized) { super.die(source); return; }
        if (state() == State.DEATH) return;
        if (source.getEntity() instanceof Player player) killer = player.getUUID();
        if (raysCompleted() < 2) {
            setHealth(raysCompleted() == 0 ? 199.99F : 20);
            begin(State.PULSE_RAY);
            return;
        }
        setHealth(1);
        begin(State.DEATH);
        entityData.set(CLOUD, 0);
    }

    @Override
    public boolean isPushable() { return state().duration == 0 && super.isPushable(); }
    @Override
    public boolean isPickable() { return state() != State.DEATH && super.isPickable(); }
    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) { return false; }
    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) { return SoundEvents.DEEPSLATE_BREAK; }
    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() { return null; }
    @Override
    public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossEvent.addPlayer(player); }
    @Override
    public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossEvent.removePlayer(player); }
    @Override
    public void remove(RemovalReason reason) { HeartOfLunaCinematicManager.releaseBoss(this, true); bossEvent.removeAllPlayers(); HeartOfLunaMovementLock.releaseBoss(getUUID()); super.remove(reason); }

    @Override
    protected void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("LunaState", state().ordinal());
        out.putInt("LunaElapsed", elapsed());
        out.putInt("LunaRays", raysCompleted());
        out.putInt("LunaActiveRay", activeRay);
        out.putInt("LunaRayCooldown", rayCooldown);
        out.putBoolean("LunaRayExploded", rayExploded);
        out.putDouble("LunaRayEndX", rayEnd().x); out.putDouble("LunaRayEndY", rayEnd().y); out.putDouble("LunaRayEndZ", rayEnd().z);
        out.putInt("LunaBuff", entityData.get(BUFF));
        out.putInt("LunaCloud", cloudTicks());
        out.putInt("LunaAttackCooldown", attackCooldown);
        out.putInt("LunaBlockCooldown", blockCooldown);
        out.putInt("LunaRoarCooldown", roarCooldown);
        out.putBoolean("LunaIntro", introPlayed);
        out.putBoolean("LunaFinalized", finalized);
        out.putDouble("LunaAimX", lockedAim.x); out.putDouble("LunaAimY", lockedAim.y); out.putDouble("LunaAimZ", lockedAim.z);
        out.putDouble("LunaCloudX", cloudCenter.x); out.putDouble("LunaCloudY", cloudCenter.y); out.putDouble("LunaCloudZ", cloudCenter.z);
        if (killer != null) out.putString("LunaKiller", killer.toString());
        if (rayTarget != null) out.putString("LunaTarget", rayTarget.toString());
        out.putString("LunaHits", String.join(",", rayHits.stream().map(UUID::toString).toList()));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        entityData.set(STATE, Mth.clamp(in.getIntOr("LunaState", 0), 0, State.values().length - 1));
        entityData.set(ELAPSED, Math.max(0, in.getIntOr("LunaElapsed", 0)));
        entityData.set(RAYS, Mth.clamp(in.getIntOr("LunaRays", 0), 0, 2));
        entityData.set(BUFF, Mth.clamp(in.getIntOr("LunaBuff", 0), 0, 400));
        entityData.set(CLOUD, Mth.clamp(in.getIntOr("LunaCloud", 0), 0, CLOUD_LIFETIME));
        activeRay = Mth.clamp(in.getIntOr("LunaActiveRay", raysCompleted() + 1), 1, 2);
        rayCooldown = Mth.clamp(in.getIntOr("LunaRayCooldown", HeartOfLunaCombatRules.repeatRayCooldown(raysCompleted())), 0, 160);
        rayExploded = in.getBooleanOr("LunaRayExploded", state() == State.PULSE_RAY && elapsed() > HeartOfLunaCombatRules.RAY_FIRE_TICK);
        entityData.set(RAY_END, new Vec3(in.getDoubleOr("LunaRayEndX", getX()), in.getDoubleOr("LunaRayEndY", getEyeY()), in.getDoubleOr("LunaRayEndZ", getZ() + 20)).subtract(position()).toVector3f());
        attackCooldown = in.getIntOr("LunaAttackCooldown", 30);
        blockCooldown = in.getIntOr("LunaBlockCooldown", 40);
        roarCooldown = in.getIntOr("LunaRoarCooldown", 100);
        introPlayed = in.getBooleanOr("LunaIntro", false);
        finalized = in.getBooleanOr("LunaFinalized", false);
        lockedAim = new Vec3(in.getDoubleOr("LunaAimX", getX()), in.getDoubleOr("LunaAimY", getEyeY()), in.getDoubleOr("LunaAimZ", getZ() + 20));
        cloudCenter = new Vec3(in.getDoubleOr("LunaCloudX", getX()), in.getDoubleOr("LunaCloudY", getY()), in.getDoubleOr("LunaCloudZ", getZ()));
        entityData.set(CLOUD_CENTER, cloudCenter.toVector3f());
        killer = uuid(in.getStringOr("LunaKiller", ""));
        rayTarget = uuid(in.getStringOr("LunaTarget", ""));
        rayHits.clear();
        for (String value : in.getStringOr("LunaHits", "").split(",")) {
            UUID id = uuid(value);
            if (id != null) rayHits.add(id);
        }
        if (state() == State.DEATH && !finalized) setHealth(1);
    }

    private static UUID uuid(String value) {
        try { return UUID.fromString(value); } catch (IllegalArgumentException ignored) { return null; }
    }
}
