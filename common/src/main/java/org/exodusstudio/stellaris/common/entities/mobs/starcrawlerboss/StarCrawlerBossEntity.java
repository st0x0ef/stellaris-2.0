package org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss;

import dev.architectury.networking.NetworkManager;
import dev.architectury.hooks.level.entity.PlayerHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawler.StarCrawlerEntity;
import org.exodusstudio.stellaris.common.network.packets.ParasiteCameraShakePacket;
import org.exodusstudio.stellaris.common.network.packets.StarCrawlerBossDeathStartPacket;
import org.exodusstudio.stellaris.common.network.packets.StarCrawlerBossIntroStartPacket;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StarCrawlerBossEntity extends Monster {

    private static final EntityDataAccessor<Integer> COMBAT_STATE =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.INT
            );

    private static final EntityDataAccessor<Long> COMBAT_STATE_START_TIME =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.LONG
            );

    private static final EntityDataAccessor<Long> ACTION_START_TIME =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.LONG
            );

    private static final EntityDataAccessor<Integer> COMBAT_PHASE =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.INT
            );

    private static final EntityDataAccessor<Float> CRYSTAL_ENERGY =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.FLOAT
            );

    private static final EntityDataAccessor<Integer> INTRO_STATE =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.INT
            );

    private static final EntityDataAccessor<Long> INTRO_START_TIME =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.LONG
            );

    private static final EntityDataAccessor<Integer> DEATH_CINEMATIC_STATE =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.INT
            );

    private static final EntityDataAccessor<Long> DEATH_CINEMATIC_START_TIME =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.LONG
            );

    private static final EntityDataAccessor<Integer> DEATH_CINEMATIC_TICKS =
            SynchedEntityData.defineId(
                    StarCrawlerBossEntity.class,
                    EntityDataSerializers.INT
            );

    public static final int INTRO_DURATION_TICKS = 150;

    private static final ResourceKey<Structure> MOON_SPHERE_STRUCTURE =
            IdentifierUtils.resourceKey(
                    Registries.STRUCTURE,
                    "moon_sphere"
            );

    private static final double PATROL_MIN_RADIUS = 12.0D;
    private static final double PATROL_SPEED = 0.7D;
    private static final double PATROL_STEP_RADIANS = 0.30D;
    private static final int PATROL_MAX_WAYPOINT_TICKS = 200;
    private static final int PATROL_ANCHOR_RETRY_INTERVAL = 10;
    private static final int PATROL_ANCHOR_MAX_ATTEMPTS = 40;

    private static final double INTRO_TRIGGER_RADIUS = 48.0D;
    private static final double INTRO_TRIGGER_RADIUS_SQR =
            INTRO_TRIGGER_RADIUS * INTRO_TRIGGER_RADIUS;

    private static final double INTRO_PARTICIPANT_MAX_DISTANCE = 72.0D;
    private static final double INTRO_PARTICIPANT_MAX_DISTANCE_SQR =
            INTRO_PARTICIPANT_MAX_DISTANCE
                    * INTRO_PARTICIPANT_MAX_DISTANCE;

    private static final int INTRO_TURN_START_TICK = 72;
    private static final int INTRO_RECOGNITION_SOUND_TICK = 78;
    private static final int INTRO_ROAR_TICK = 90;
    private static final int INTRO_TITLE_IMPACT_TICK = 106;
    private static final int INTRO_POST_GRACE_TICKS = 25;

    public static final int DEATH_CINEMATIC_DURATION_TICKS = 140;

    private static final double DEATH_CINEMATIC_TRIGGER_RADIUS = 64.0D;
    private static final double DEATH_CINEMATIC_TRIGGER_RADIUS_SQR =
            DEATH_CINEMATIC_TRIGGER_RADIUS
                    * DEATH_CINEMATIC_TRIGGER_RADIUS;

    private static final double DEATH_CINEMATIC_PARTICIPANT_MAX_DISTANCE =
            82.0D;
    private static final double DEATH_CINEMATIC_PARTICIPANT_MAX_DISTANCE_SQR =
            DEATH_CINEMATIC_PARTICIPANT_MAX_DISTANCE
                    * DEATH_CINEMATIC_PARTICIPANT_MAX_DISTANCE;

    private static final int DEATH_KILL_CREDIT_MEMORY_TICKS =
            DEATH_CINEMATIC_DURATION_TICKS + 80;

    public static final float MAX_HEALTH = 400.0F;

    public static final float FIRST_HEAL_TRIGGER = 200.0F;
    public static final float FIRST_HEAL_TARGET = 300.0F;

    public static final float SECOND_HEAL_TRIGGER = 100.0F;
    public static final float SECOND_HEAL_TARGET = 240.0F;

    public static final float CHARGE_DAMAGE = 12.0F;
    public static final float JUMP_SLAM_DAMAGE = 25.0F;
    public static final float GROUND_SMASH_DAMAGE = 8.0F;

    public static final double GROUND_SMASH_RADIUS = 5.0D;
    public static final double JUMP_SLAM_RADIUS = 4.5D;

    public static final int HEAL_DURATION_TICKS = 80;

    public static final float FIRST_HEAL_DAMAGE_MULTIPLIER = 0.25F;
    public static final float SECOND_HEAL_DAMAGE_MULTIPLIER = 0.10F;

    private static final int CHARGE_LAUNCH_TICK = 12;
    private static final int CHARGE_END_TICK = 17;
    private static final int CHARGE_CLIP_END_TICK = 24;

    private static final int JUMP_TAKEOFF_TICK = 7;

    private static final int JUMP_CLIP_END_TICK = 30;
    private static final int JUMP_FORCE_DESCENT_TICK = 27;
    private static final int JUMP_ABORT_TICK = 40;

    private static final int GROUND_SMASH_IMPACT_TICK = 13;
    private static final int GROUND_SMASH_CLIP_END_TICK = 30;

    private static final int FORCED_SMASH_GROUNDING_TIMEOUT_TICKS = 30;

    private static final int FINAL_MINION_COUNT = 4;

    private static final double CLIENT_WALK_START_SPEED_SQR = 0.0009D;
    private static final double CLIENT_WALK_KEEP_SPEED_SQR = 0.00018D;
    private static final int CLIENT_WALK_HOLD_TICKS = 6;

    private static final double PATH_SPEED = 1.05D;

    private static final double CHARGE_SPEED = 1.15D;
    private static final double CHARGE_HIT_INFLATION = 0.45D;

    private static final double JUMP_VERTICAL_VELOCITY = 0.52D;
    private static final double JUMP_MIN_HORIZONTAL_VELOCITY = 0.25D;
    private static final double JUMP_MAX_HORIZONTAL_VELOCITY = 0.92D;
    private static final double JUMP_EXPECTED_FLIGHT_TICKS = 14.0D;

    private static final int PHASE_ONE_COOLDOWN_MIN = 56;
    private static final int PHASE_ONE_COOLDOWN_MAX = 76;

    private static final int PHASE_TWO_COOLDOWN_MIN = 44;
    private static final int PHASE_TWO_COOLDOWN_MAX = 64;

    private static final int PHASE_THREE_COOLDOWN_MIN = 34;
    private static final int PHASE_THREE_COOLDOWN_MAX = 52;

    private static final AttackKind[] CLOSE_PHASE_ONE_ATTACKS = {
            AttackKind.JUMP_SLAM,
            AttackKind.JUMP_SLAM,
            AttackKind.CHARGE
    };

    private static final AttackKind[] CLOSE_LATER_PHASE_ATTACKS = {
            AttackKind.JUMP_SLAM,
            AttackKind.JUMP_SLAM,
            AttackKind.CHARGE,
            AttackKind.GROUND_SMASH,
            AttackKind.GROUND_SMASH,
            AttackKind.GROUND_SMASH
    };

    private static final AttackKind[] MEDIUM_ATTACKS = {
            AttackKind.JUMP_SLAM,
            AttackKind.JUMP_SLAM,
            AttackKind.JUMP_SLAM,
            AttackKind.CHARGE,
            AttackKind.CHARGE
    };

    private static final AttackKind[] FAR_ATTACKS = {
            AttackKind.CHARGE,
            AttackKind.CHARGE,
            AttackKind.CHARGE,
            AttackKind.JUMP_SLAM,
            AttackKind.JUMP_SLAM
    };

    public final AnimationState walkingAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();

    public final AnimationState chargeAnimationState = new AnimationState();
    public final AnimationState jumpSlamAnimationState = new AnimationState();
    public final AnimationState groundSmashAnimationState = new AnimationState();
    public final AnimationState healingAnimationState = new AnimationState();

    private final ServerBossEvent bossEvent =
            new ServerBossEvent(
                    Mth.createInsecureUUID(this.random),
                    Component.empty(),
                    BossEvent.BossBarColor.PURPLE,
                    BossEvent.BossBarOverlay.PROGRESS
            );

    private final Set<UUID> chargeHitTargets = new HashSet<>();

    private Vec3 committedDestination;

    private Vec3 chargeDirection = Vec3.ZERO;
    private Vec3 jumpHorizontalVelocity = Vec3.ZERO;

    private Vec3 pendingForcedSmashAnchor;

    private float healingStartHealth;
    private float healingTargetHealth;

    private float healingStartEnergy;
    private float healingTargetEnergy;

    private float healingDamageSustained;

    private int attackCooldown;

    private AttackKind lastAttack;

    private boolean firstHealUsed;
    private boolean secondHealUsed;

    private boolean finalMinionsSpawned;

    private boolean forcedFinalSmash;

    private boolean pendingForcedGroundSmash;
    private int pendingForcedGroundSmashTicks;

    private boolean jumpWasAirborne;
    private boolean scriptedJumpFallProtection;

    private final Set<UUID> introParticipants =
            new HashSet<>();

    private Vec3 introEncounterAnchor;

    private final Set<UUID> deathCinematicParticipants =
            new HashSet<>();

    private Vec3 deathCinematicAnchor;
    private Vec3 lastSafeDeathCinematicAnchor;

    private DamageSource pendingDeathSource;
    private UUID pendingDeathKillerUuid;
    private String pendingDeathDamageType;

    private boolean finalizingDeath;
    private boolean finalizeDeathAfterLoad;
    private boolean discardFinalizedDeathAfterLoad;

    private BlockPos patrolCenter;
    private double patrolRadius;
    private boolean patrolAnchorResolved;
    private int patrolAnchorAttempts;
    private int patrolDirection = 1;

    private int clientWalkHoldTicks;

    public StarCrawlerBossEntity(
            EntityType<? extends StarCrawlerBossEntity> entityType,
            Level level
    ) {
        super(entityType, level);

        this.xpReward = 75;

        this.setHealth(this.getMaxHealth());

        this.attackCooldown = this.randomAttackCooldown();

        this.bossEvent.setDarkenScreen(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(
                        Attributes.MAX_HEALTH,
                        MAX_HEALTH
                )
                .add(
                        Attributes.MOVEMENT_SPEED,
                        0.25D
                )
                .add(
                        Attributes.ATTACK_DAMAGE,
                        CHARGE_DAMAGE
                )
                .add(
                        Attributes.ARMOR,
                        8.0D
                )
                .add(
                        Attributes.ARMOR_TOUGHNESS,
                        4.0D
                )
                .add(
                        Attributes.FOLLOW_RANGE,
                        48.0D
                )
                .add(
                        Attributes.KNOCKBACK_RESISTANCE,
                        0.85D
                );
    }

    @Override
    protected void defineSynchedData(
            SynchedEntityData.Builder builder
    ) {
        super.defineSynchedData(builder);

        builder.define(
                COMBAT_STATE,
                CombatState.IDLE.id
        );

        builder.define(
                COMBAT_STATE_START_TIME,
                -1L
        );

        builder.define(
                ACTION_START_TIME,
                -1L
        );

        builder.define(
                COMBAT_PHASE,
                CombatPhase.PHASE_1.id
        );

        builder.define(
                CRYSTAL_ENERGY,
                1.0F
        );

        builder.define(
                INTRO_STATE,
                IntroState.NOT_STARTED.id
        );

        builder.define(
                INTRO_START_TIME,
                -1L
        );

        builder.define(
                DEATH_CINEMATIC_STATE,
                DeathCinematicState.ALIVE.id
        );

        builder.define(
                DEATH_CINEMATIC_START_TIME,
                -1L
        );

        builder.define(
                DEATH_CINEMATIC_TICKS,
                0
        );
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(
                0,
                new IntroFreezeGoal()
        );

        this.goalSelector.addGoal(
                1,
                new FloatGoal(this)
        );

        this.goalSelector.addGoal(
                2,
                new BossCombatGoal()
        );

        this.goalSelector.addGoal(
                5,
                new RingPatrolGoal()
        );

        this.goalSelector.addGoal(
                6,
                new WaterAvoidingRandomStrollGoal(
                        this,
                        0.7D
                )
        );

        this.goalSelector.addGoal(
                7,
                new LookAtPlayerGoal(
                        this,
                        Player.class,
                        14.0F
                )
        );

        this.goalSelector.addGoal(
                8,
                new RandomLookAroundGoal(this)
        );

        this.targetSelector.addGoal(
                1,
                new HurtByTargetGoal(this)
        );

        this.targetSelector.addGoal(
                2,
                new NearestAttackableTargetGoal<>(
                        this,
                        Player.class,
                        true
                )
        );

        this.targetSelector.addGoal(
                3,
                new NearestAttackableTargetGoal<>(
                        this,
                        AbstractVillager.class,
                        false
                )
        );
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide()
                && this.level()
                instanceof ServerLevel serverLevel) {

            if (this.isAlive()
                    && !this.isDeathCinematicPlaying()) {
                this.updateLastSafeDeathCinematicAnchor(serverLevel);
            }

            if (this.discardFinalizedDeathAfterLoad) {
                this.discardFinalizedDeathAfterLoad = false;
                this.cleanupDeathCinematic(false);
                this.discard();
                return;
            }

            if (this.finalizeDeathAfterLoad) {
                this.finalizeDeathAfterLoad = false;
                this.finalizeDeath(
                        serverLevel,
                        this.restorePendingDeathSource(serverLevel)
                );
                return;
            }

            if (this.isDeathCinematicPlaying()) {
                this.holdDeathCinematicPosition();
            } else if (this.isAlive()) {
                this.tickIntroBeforeSuper(
                        serverLevel
                );
            }
        }

        super.tick();

        if (this.level().isClientSide()) {
            this.tickClientAnimations();
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel)
                || !this.isAlive()) {
            return;
        }

        this.resolvePatrolAnchor(serverLevel);

        if (this.isDeathCinematicPlaying()) {
            this.tickDeathCinematic(serverLevel);

            if (!this.isDeathCinematicPlaying()) {
                return;
            }

            this.bossEvent.setProgress(0.0F);
            this.updateBossPresentation();

            return;
        }

        if (this.isIntroPlaying()) {
            this.tickIntro(
                    serverLevel
            );

            this.bossEvent.setProgress(
                    Mth.clamp(
                            this.getHealth()
                                    / this.getMaxHealth(),
                            0.0F,
                            1.0F
                    )
            );

            this.updateBossPresentation();

            return;
        }

        if (!this.isHealing()) {
            this.checkHealthTransitions(serverLevel);
        }

        if (this.pendingForcedGroundSmash
                && this.isNeutralState()) {

            if (this.pendingForcedSmashAnchor == null) {
                this.pendingForcedSmashAnchor =
                        this.position();
            }

            this.tickPendingForcedGroundSmash();
        }

        if (this.scriptedJumpFallProtection
                && this.onGround()) {

            this.scriptedJumpFallProtection = false;
        }

        if (this.attackCooldown > 0
                && this.isNeutralState()) {

            this.attackCooldown--;
        }

        switch (this.getCombatState()) {
            case IDLE, CHASING -> {
            }

            case CHARGE_WINDUP ->
                    this.tickChargeWindup(serverLevel);

            case CHARGING ->
                    this.tickCharging(serverLevel);

            case CHARGE_RECOVERY ->
                    this.tickChargeRecovery();

            case JUMP_SLAM_WINDUP ->
                    this.tickJumpWindup(serverLevel);

            case JUMP_SLAM_AIRBORNE ->
                    this.tickJumpAirborne(serverLevel);

            case JUMP_SLAM_IMPACT ->
                    this.tickJumpImpact();

            case JUMP_SLAM_RECOVERY ->
                    this.tickJumpRecovery(serverLevel);

            case GROUND_SMASH_WINDUP ->
                    this.tickGroundSmashWindup(serverLevel);

            case GROUND_SMASH_IMPACT ->
                    this.tickGroundSmashImpact();

            case GROUND_SMASH_RECOVERY ->
                    this.tickGroundSmashRecovery(serverLevel);

            case HEALING_PHASE_2, HEALING_PHASE_3 ->
                    this.tickHealing(serverLevel);
        }

        this.updateCrystalEnergy();

        this.bossEvent.setProgress(
                Mth.clamp(
                        this.getHealth()
                                / this.getMaxHealth(),
                        0.0F,
                        1.0F
                )
        );

        this.updateBossPresentation();
    }

    public CombatState getCombatState() {
        return CombatState.byId(
                this.entityData.get(COMBAT_STATE)
        );
    }

    public int getCombatStateTicks() {
        long start =
                this.entityData.get(
                        COMBAT_STATE_START_TIME
                );

        return start < 0L
                ? 0
                : elapsedSince(start);
    }

    public int getActionTicks() {
        long start =
                this.entityData.get(
                        ACTION_START_TIME
                );

        return start < 0L
                ? 0
                : elapsedSince(start);
    }

    public int getCombatPhase() {
        return this.entityData.get(
                COMBAT_PHASE
        );
    }

    public CombatPhase getPhase() {
        return CombatPhase.byId(
                this.getCombatPhase()
        );
    }

    public float getCrystalEnergy() {
        return this.entityData.get(
                CRYSTAL_ENERGY
        );
    }

    public IntroState getIntroState() {
        return IntroState.byId(
                this.entityData.get(
                        INTRO_STATE
                )
        );
    }

    public boolean isIntroPlaying() {
        return this.getIntroState()
                == IntroState.PLAYING;
    }

    public boolean isIntroComplete() {
        return this.getIntroState()
                == IntroState.COMPLETE;
    }

    public long getIntroStartGameTime() {
        return this.entityData.get(
                INTRO_START_TIME
        );
    }

    public int getIntroTicks() {
        long start =
                this.getIntroStartGameTime();

        return start < 0L
                ? 0
                : elapsedSince(start);
    }

    public DeathCinematicState getDeathCinematicState() {
        return DeathCinematicState.byId(
                this.entityData.get(
                        DEATH_CINEMATIC_STATE
                )
        );
    }

    public boolean isDeathCinematicPlaying() {
        return this.getDeathCinematicState()
                == DeathCinematicState.DYING;
    }

    public long getDeathCinematicStartGameTime() {
        return this.entityData.get(
                DEATH_CINEMATIC_START_TIME
        );
    }

    public int getDeathCinematicTicks() {
        return this.entityData.get(
                DEATH_CINEMATIC_TICKS
        );
    }

    private void tickIntroBeforeSuper(
            ServerLevel serverLevel
    ) {
        if (this.isIntroPlaying()) {
            this.holdPosition();
            return;
        }

        if (this.getIntroState()
                != IntroState.NOT_STARTED
                || !this.isNeutralState()) {

            return;
        }

        List<ServerPlayer> candidates =
                new ArrayList<>();

        for (ServerPlayer player :
                serverLevel.players()) {

            if (this.isEligibleIntroPlayer(
                    player
            )) {
                candidates.add(player);
            }
        }

        if (candidates.isEmpty()) {
            return;
        }

        candidates.sort(
                Comparator.comparingDouble(
                                (ServerPlayer player) ->
                                        this.distanceToSqr(player)
                        )
                        .thenComparing(
                                player -> player.getUUID()
                                        .toString()
                        )
        );

        this.beginIntro(
                serverLevel,
                candidates
        );
    }

    private boolean isEligibleIntroPlayer(
            ServerPlayer player
    ) {
        GameType gameType =
                player.gameMode();

        return player.connection != null
                && player.isAlive()
                && !player.isRemoved()
                && !player.isSpectator()
                && !PlayerHooks.isFake(player)
                && (
                gameType == GameType.SURVIVAL
                        || gameType == GameType.ADVENTURE
                        || gameType == GameType.CREATIVE
        )
                && !StarCrawlerBossDeathManager.isClaimed(
                player.getUUID()
        )
                && player.level() == this.level()
                && player.distanceToSqr(this)
                <= INTRO_TRIGGER_RADIUS_SQR;
    }

    private boolean isValidIntroParticipant(
            ServerPlayer player
    ) {
        GameType gameType =
                player.gameMode();

        return player.connection != null
                && player.isAlive()
                && !player.isRemoved()
                && !player.isSpectator()
                && !PlayerHooks.isFake(player)
                && (
                gameType == GameType.SURVIVAL
                        || gameType == GameType.ADVENTURE
                        || gameType == GameType.CREATIVE
        )
                && !StarCrawlerBossDeathManager.isClaimed(
                player.getUUID()
        )
                && player.level() == this.level()
                && player.distanceToSqr(this)
                <= INTRO_PARTICIPANT_MAX_DISTANCE_SQR;
    }

    private void beginIntro(
            ServerLevel serverLevel,
            List<ServerPlayer> candidates
    ) {
        this.introParticipants.clear();

        for (ServerPlayer player :
                candidates) {

            if (StarCrawlerBossIntroManager.tryClaim(
                    this,
                    player
            )) {
                this.introParticipants.add(
                        player.getUUID()
                );
            }
        }

        if (this.introParticipants.isEmpty()) {
            return;
        }

        long startGameTime =
                serverLevel.getGameTime();

        this.entityData.set(
                INTRO_START_TIME,
                startGameTime
        );

        this.setIntroState(
                IntroState.PLAYING
        );

        this.setCombatState(
                CombatState.IDLE
        );

        this.entityData.set(
                ACTION_START_TIME,
                -1L
        );

        this.updateIntroEncounterAnchor(
                serverLevel
        );

        this.holdPosition();

        for (UUID playerUuid :
                this.introParticipants) {

            ServerPlayer player =
                    serverLevel.getServer()
                            .getPlayerList()
                            .getPlayer(playerUuid);

            if (player != null) {
                this.sendIntroStart(
                        player,
                        startGameTime
                );
            }
        }

        this.playIntroSound(
                this.getIntroParticipantPlayers(serverLevel),
                SoundEvents.RESPAWN_ANCHOR_AMBIENT,
                0.75F,
                0.52F,
                true
        );
    }

    private void tickIntro(
            ServerLevel serverLevel
    ) {
        this.holdPosition();

        List<ServerPlayer> activeParticipants =
                new ArrayList<>();

        Iterator<UUID> iterator =
                this.introParticipants.iterator();

        while (iterator.hasNext()) {
            UUID playerUuid =
                    iterator.next();

            ServerPlayer player =
                    serverLevel.getServer()
                            .getPlayerList()
                            .getPlayer(playerUuid);

            if (player == null) {
                StarCrawlerBossIntroManager.releasePlayer(
                        playerUuid,
                        serverLevel.getServer(),
                        false
                );

                iterator.remove();
                continue;
            }

            if (!StarCrawlerBossIntroManager.isClaimedBy(
                    playerUuid,
                    this.getUUID()
            )) {
                iterator.remove();
                continue;
            }

            if (!this.isValidIntroParticipant(
                    player
            )) {
                StarCrawlerBossIntroManager.releasePlayer(
                        player,
                        true
                );

                iterator.remove();

                continue;
            }

            activeParticipants.add(player);
        }

        if (this.introParticipants.isEmpty()) {
            this.finishIntro(
                    serverLevel,
                    false
            );

            return;
        }

        this.updateIntroEncounterAnchor(
                serverLevel
        );

        int introTicks =
                this.getIntroTicks();

        List<ServerPlayer> presentationViewers =
                this.getIntroPresentationViewers(
                        serverLevel,
                        activeParticipants,
                        introTicks >= 84
                                ? 72.0D
                                : 52.0D
                );

        if (introTicks
                >= INTRO_TURN_START_TICK) {

            this.turnTowardIntroParticipants(
                    introTicks
            );
        }

        StarCrawlerBossVfx.introTick(
                serverLevel,
                this,
                introTicks,
                activeParticipants
        );

        if (introTicks == 16) {
            this.playIntroSound(
                    activeParticipants,
                    SoundEvents.WARDEN_NEARBY_CLOSE,
                    0.58F,
                    0.38F,
                    true
            );
        }

        if (introTicks == 28) {
            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.WARDEN_TENDRIL_CLICKS,
                    0.42F,
                    0.50F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.AMETHYST_BLOCK_RESONATE,
                    0.38F,
                    0.48F,
                    false
            );
        }

        if (introTicks == 34) {
            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    0.88F,
                    0.66F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.RESPAWN_ANCHOR_CHARGE,
                    0.34F,
                    0.36F,
                    false
            );
        }

        if (introTicks == 48) {
            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.SCULK_BLOCK_CHARGE,
                    0.48F,
                    0.56F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.AMETHYST_BLOCK_RESONATE,
                    0.74F,
                    0.78F,
                    false
            );
        }

        if (introTicks == 62) {
            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.WARDEN_STEP,
                    1.08F,
                    0.42F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.DEEPSLATE_HIT,
                    0.86F,
                    0.52F,
                    false
            );
        }

        if (introTicks == 70) {
            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.WARDEN_LISTENING,
                    0.54F,
                    0.58F,
                    false
            );
        }

        if (introTicks
                == INTRO_RECOGNITION_SOUND_TICK) {

            this.playIntroSound(
                    activeParticipants,
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.45F,
                    0.58F,
                    false
            );
        }

        if (introTicks == 86) {
            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.WARDEN_SONIC_CHARGE,
                    0.62F,
                    0.60F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.BEACON_ACTIVATE,
                    0.32F,
                    0.46F,
                    false
            );
        }

        if (introTicks
                == INTRO_ROAR_TICK) {

            StarCrawlerBossVfx.introRoar(
                    serverLevel,
                    this,
                    activeParticipants
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.WARDEN_ROAR,
                    2.35F,
                    0.68F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.MACE_SMASH_GROUND_HEAVY,
                    1.68F,
                    0.42F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.WARDEN_SONIC_BOOM,
                    0.98F,
                    0.72F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.END_PORTAL_FRAME_FILL,
                    1.08F,
                    0.46F,
                    false
            );
        }

        if (introTicks == 94) {
            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.AMETHYST_BLOCK_RESONATE,
                    1.08F,
                    0.88F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.SCULK_CATALYST_BLOOM,
                    0.54F,
                    0.56F,
                    false
            );
        }

        if (introTicks
                == INTRO_TITLE_IMPACT_TICK) {

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.RESPAWN_ANCHOR_CHARGE,
                    1.35F,
                    0.55F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.WARDEN_ATTACK_IMPACT,
                    1.02F,
                    0.34F,
                    false
            );
        }

        if (introTicks == 114) {
            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    0.64F,
                    1.22F,
                    false
            );
        }

        if (introTicks
                >= INTRO_DURATION_TICKS) {

            this.finishIntro(
                    serverLevel,
                    true
            );
        }
    }

    private void updateIntroEncounterAnchor(
            ServerLevel serverLevel
    ) {
        Vec3 sum =
                Vec3.ZERO;

        int count =
                0;

        for (UUID playerUuid :
                this.introParticipants) {

            ServerPlayer player =
                    serverLevel.getServer()
                            .getPlayerList()
                            .getPlayer(playerUuid);

            if (player == null
                    || !StarCrawlerBossIntroManager.isClaimedBy(
                    playerUuid,
                    this.getUUID()
            )) {

                continue;
            }

            sum =
                    sum.add(
                            player.position()
                    );

            count++;
        }

        if (count > 0) {
            this.introEncounterAnchor =
                    sum.scale(
                            1.0D / count
                    );
        }
    }

    private List<ServerPlayer> getIntroParticipantPlayers(
            ServerLevel serverLevel
    ) {
        List<ServerPlayer> participants =
                new ArrayList<>();

        for (UUID playerUuid :
                this.introParticipants) {

            ServerPlayer player =
                    serverLevel.getServer()
                            .getPlayerList()
                            .getPlayer(playerUuid);

            if (player != null
                    && player.level() == serverLevel
                    && StarCrawlerBossIntroManager.isClaimedBy(
                    playerUuid,
                    this.getUUID()
            )) {

                participants.add(player);
            }
        }

        return participants;
    }

    private List<ServerPlayer> getIntroPresentationViewers(
            ServerLevel serverLevel,
            Iterable<ServerPlayer> participants,
            double observerRadius
    ) {
        List<ServerPlayer> viewers =
                new ArrayList<>();

        Set<UUID> seen =
                new HashSet<>();

        for (ServerPlayer participant : participants) {
            if (participant.connection != null
                    && !participant.isRemoved()
                    && seen.add(participant.getUUID())) {

                viewers.add(participant);
            }
        }

        double observerRadiusSqr =
                observerRadius * observerRadius;

        for (ServerPlayer player : serverLevel.players()) {
            if (player.connection == null
                    || player.isRemoved()
                    || PlayerHooks.isFake(player)
                    || player.distanceToSqr(this)
                    > observerRadiusSqr
                    || !seen.add(player.getUUID())) {

                continue;
            }

            viewers.add(player);
        }

        return viewers;
    }

    private void turnTowardIntroParticipants(
            int introTicks
    ) {
        if (this.introEncounterAnchor == null) {
            return;
        }

        Vec3 horizontal =
                this.introEncounterAnchor
                        .subtract(
                                this.position()
                        )
                        .multiply(
                                1.0D,
                                0.0D,
                                1.0D
                        );

        if (horizontal.horizontalDistanceSqr()
                < 1.0E-5D) {

            return;
        }

        float desiredYaw =
                (float) (
                        Mth.atan2(
                                horizontal.z,
                                horizontal.x
                        )
                                * Mth.RAD_TO_DEG
                )
                        - 90.0F;

        float turnSpeed =
                introTicks < INTRO_ROAR_TICK
                        ? 2.4F
                        : 4.2F;

        float bodyYaw =
                Mth.approachDegrees(
                        this.getYRot(),
                        desiredYaw,
                        turnSpeed
                );

        float headYaw =
                Mth.approachDegrees(
                        this.getYHeadRot(),
                        desiredYaw,
                        turnSpeed * 1.35F
                );

        this.setYRot(bodyYaw);
        this.setYBodyRot(bodyYaw);
        this.setYHeadRot(headYaw);

        this.getLookControl()
                .setLookAt(
                        this.introEncounterAnchor.x,
                        this.introEncounterAnchor.y
                                + 1.0D,
                        this.introEncounterAnchor.z,
                        turnSpeed,
                        turnSpeed
                );
    }

    private void finishIntro(
            ServerLevel serverLevel,
            boolean playFinishPresentation
    ) {
        if (!this.isIntroPlaying()) {
            return;
        }

        if (playFinishPresentation) {
            List<ServerPlayer> activeParticipants =
                    this.getIntroParticipantPlayers(
                            serverLevel
                    );

            List<ServerPlayer> presentationViewers =
                    this.getIntroPresentationViewers(
                            serverLevel,
                            activeParticipants,
                            72.0D
                    );

            StarCrawlerBossVfx.introComplete(
                    serverLevel,
                    this,
                    activeParticipants
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.AMETHYST_BLOCK_RESONATE,
                    1.30F,
                    0.82F,
                    false
            );

            this.playIntroSound(
                    presentationViewers,
                    SoundEvents.WARDEN_ANGRY,
                    0.58F,
                    0.48F,
                    false
            );
        }

        this.setIntroState(
                IntroState.COMPLETE
        );

        this.entityData.set(
                INTRO_START_TIME,
                -1L
        );

        this.setCombatState(
                CombatState.IDLE
        );

        this.entityData.set(
                ACTION_START_TIME,
                -1L
        );

        this.attackCooldown =
                INTRO_POST_GRACE_TICKS;

        StarCrawlerBossIntroManager.releaseBoss(
                this,
                true
        );

        this.introParticipants.clear();
        this.introEncounterAnchor =
                null;
    }

    private void cleanupIntro(
            boolean notifyClients
    ) {
        if (this.isIntroPlaying()) {
            this.setIntroState(
                    IntroState.COMPLETE
            );

            this.entityData.set(
                    INTRO_START_TIME,
                    -1L
            );
        }

        StarCrawlerBossIntroManager.releaseBoss(
                this,
                notifyClients
        );

        this.introParticipants.clear();
        this.introEncounterAnchor =
                null;
    }

    private void sendIntroStart(
            ServerPlayer player,
            long serverGameTimeAtSend
    ) {
        NetworkManager.sendToPlayer(
                player,
                new StarCrawlerBossIntroStartPacket(
                        this.getId(),
                        this.getUUID(),
                        this.getIntroStartGameTime(),
                        serverGameTimeAtSend,
                        INTRO_DURATION_TICKS,
                        this.getX(),
                        this.getY(),
                        this.getZ()
                )
        );
    }

    private void setIntroState(
            IntroState state
    ) {
        this.entityData.set(
                INTRO_STATE,
                state.id
        );
    }

    private void beginDeathCinematic(
            ServerLevel serverLevel,
            DamageSource source
    ) {
        if (this.getDeathCinematicState()
                != DeathCinematicState.ALIVE
                || this.finalizingDeath) {

            return;
        }

        this.setHealth(1.0F);

        this.pendingDeathSource = source;
        this.pendingDeathDamageType =
                source.typeHolder()
                        .unwrapKey()
                        .map(key -> key.identifier().toString())
                        .orElse(null);

        Player creditedPlayer =
                this.getLastHurtByPlayer();

        if (creditedPlayer != null) {
            this.pendingDeathKillerUuid =
                    creditedPlayer.getUUID();
        } else if (source.getEntity()
                instanceof Player player) {
            this.pendingDeathKillerUuid =
                    player.getUUID();
        } else {
            this.pendingDeathKillerUuid = null;
        }

        this.refreshDeathKillCredit();

        this.cleanupIntro(true);
        this.setIntroState(
                IntroState.COMPLETE
        );
        this.entityData.set(
                INTRO_START_TIME,
                -1L
        );

        long startGameTime =
                serverLevel.getGameTime();

        this.entityData.set(
                DEATH_CINEMATIC_START_TIME,
                startGameTime
        );

        this.entityData.set(
                DEATH_CINEMATIC_TICKS,
                0
        );

        this.setDeathCinematicState(
                DeathCinematicState.DYING
        );

        this.setCombatState(
                CombatState.IDLE
        );

        this.entityData.set(
                ACTION_START_TIME,
                -1L
        );

        this.pendingForcedGroundSmash = false;
        this.pendingForcedGroundSmashTicks = 0;
        this.forcedFinalSmash = false;
        this.scriptedJumpFallProtection = false;

        this.chargeHitTargets.clear();
        this.committedDestination = null;
        this.chargeDirection = Vec3.ZERO;
        this.jumpHorizontalVelocity = Vec3.ZERO;
        this.pendingForcedSmashAnchor = null;

        this.setTarget(null);
        this.setAggressive(false);
        this.deathCinematicAnchor =
                this.resolveDeathCinematicAnchor(serverLevel);
        this.holdDeathCinematicPosition();

        this.deathCinematicParticipants.clear();

        List<ServerPlayer> candidates =
                new ArrayList<>();

        for (ServerPlayer player :
                serverLevel.players()) {

            if (this.isEligibleDeathCinematicPlayer(player)) {
                candidates.add(player);
            }
        }

        candidates.sort(
                Comparator.comparingDouble(
                                (ServerPlayer player) ->
                                        this.distanceToSqr(player)
                        )
                        .thenComparing(
                                player -> player.getUUID()
                                        .toString()
                        )
        );

        for (ServerPlayer player :
                candidates) {

            if (StarCrawlerBossDeathManager.tryClaim(
                    this,
                    player
            )) {
                this.deathCinematicParticipants.add(
                        player.getUUID()
                );

                this.sendDeathCinematicStart(
                        player,
                        startGameTime
                );
            }
        }

        List<ServerPlayer> participants =
                this.getDeathCinematicParticipantPlayers(
                        serverLevel
                );

        StarCrawlerBossDeathVfx.lethalImpact(
                serverLevel,
                this,
                participants
        );

        this.bossEvent.setProgress(0.0F);
        this.updateBossPresentation();
    }

    private void tickDeathCinematic(
            ServerLevel serverLevel
    ) {
        this.holdDeathCinematicPosition();
        this.setTarget(null);
        this.setAggressive(false);
        this.refreshDeathKillCredit();

        List<ServerPlayer> activeParticipants =
                new ArrayList<>();

        Iterator<UUID> iterator =
                this.deathCinematicParticipants.iterator();

        while (iterator.hasNext()) {
            UUID playerUuid =
                    iterator.next();

            ServerPlayer player =
                    serverLevel.getServer()
                            .getPlayerList()
                            .getPlayer(playerUuid);

            if (player == null) {
                StarCrawlerBossDeathManager.releasePlayer(
                        playerUuid,
                        serverLevel.getServer(),
                        false
                );

                iterator.remove();
                continue;
            }

            if (!StarCrawlerBossDeathManager.isClaimedBy(
                    playerUuid,
                    this.getUUID()
            )) {
                iterator.remove();
                continue;
            }

            if (!this.isValidDeathCinematicParticipant(player)) {
                StarCrawlerBossDeathManager.releasePlayer(
                        player,
                        true
                );

                iterator.remove();
                continue;
            }

            activeParticipants.add(player);
        }

        int deathTicks =
                this.elapsedSince(
                        this.getDeathCinematicStartGameTime()
                );

        this.entityData.set(
                DEATH_CINEMATIC_TICKS,
                Mth.clamp(
                        deathTicks,
                        0,
                        DEATH_CINEMATIC_DURATION_TICKS
                )
        );

        StarCrawlerBossDeathVfx.tick(
                serverLevel,
                this,
                deathTicks,
                activeParticipants
        );

        if (deathTicks
                >= DEATH_CINEMATIC_DURATION_TICKS) {

            this.finalizeDeath(
                    serverLevel,
                    this.pendingDeathSource
            );
        }
    }

    private boolean isEligibleDeathCinematicPlayer(
            ServerPlayer player
    ) {
        return this.isValidEncounterPlayer(player)
                && player.distanceToSqr(this)
                <= DEATH_CINEMATIC_TRIGGER_RADIUS_SQR;
    }

    private boolean isValidDeathCinematicParticipant(
            ServerPlayer player
    ) {
        return this.isValidEncounterPlayer(player)
                && player.distanceToSqr(this)
                <= DEATH_CINEMATIC_PARTICIPANT_MAX_DISTANCE_SQR;
    }

    private boolean isValidEncounterPlayer(
            ServerPlayer player
    ) {
        GameType gameType =
                player.gameMode();

        return player.connection != null
                && player.isAlive()
                && !player.isRemoved()
                && !player.isSpectator()
                && !PlayerHooks.isFake(player)
                && (
                gameType == GameType.SURVIVAL
                        || gameType == GameType.ADVENTURE
                        || gameType == GameType.CREATIVE
        )
                && player.level() == this.level();
    }

    private List<ServerPlayer> getDeathCinematicParticipantPlayers(
            ServerLevel serverLevel
    ) {
        List<ServerPlayer> participants =
                new ArrayList<>();

        for (UUID playerUuid :
                this.deathCinematicParticipants) {

            ServerPlayer player =
                    serverLevel.getServer()
                            .getPlayerList()
                            .getPlayer(playerUuid);

            if (player != null
                    && player.level() == serverLevel
                    && StarCrawlerBossDeathManager.isClaimedBy(
                    playerUuid,
                    this.getUUID()
            )) {
                participants.add(player);
            }
        }

        return participants;
    }

    private void sendDeathCinematicStart(
            ServerPlayer player,
            long serverGameTimeAtSend
    ) {
        NetworkManager.sendToPlayer(
                player,
                new StarCrawlerBossDeathStartPacket(
                        this.getId(),
                        this.getUUID(),
                        this.getDeathCinematicStartGameTime(),
                        serverGameTimeAtSend,
                        DEATH_CINEMATIC_DURATION_TICKS,
                        this.getX(),
                        this.getY(),
                        this.getZ()
                )
        );
    }

    private void refreshDeathKillCredit() {
        if (this.pendingDeathKillerUuid != null) {
            this.setLastHurtByPlayer(
                    this.pendingDeathKillerUuid,
                    DEATH_KILL_CREDIT_MEMORY_TICKS
            );
        }

        if (this.pendingDeathSource != null
                && this.pendingDeathSource.getEntity()
                instanceof LivingEntity livingEntity
                && !(livingEntity instanceof Player)) {

            this.setLastHurtByMob(livingEntity);
        }
    }

    private void finalizeDeath(
            ServerLevel serverLevel,
            DamageSource source
    ) {
        if (this.finalizingDeath
                || this.getDeathCinematicState()
                == DeathCinematicState.FINALIZED) {

            return;
        }

        this.finalizingDeath = true;

        DamageSource finalSource =
                source == null
                        ? this.damageSources().genericKill()
                        : source;

        this.refreshDeathKillCredit();

        this.setDeathCinematicState(
                DeathCinematicState.FINALIZED
        );

        this.entityData.set(
                DEATH_CINEMATIC_START_TIME,
                -1L
        );

        this.entityData.set(
                DEATH_CINEMATIC_TICKS,
                DEATH_CINEMATIC_DURATION_TICKS
        );

        this.entityData.set(
                CRYSTAL_ENERGY,
                0.0F
        );

        this.cleanupDeathCinematic(true);
        this.cleanupIntro(true);

        this.setHealth(0.0F);

        super.die(finalSource);

        if (!this.dead) {
            this.discard();
        }

        this.pendingDeathSource = null;
        this.pendingDeathKillerUuid = null;
        this.pendingDeathDamageType = null;
        this.finalizingDeath = false;
    }

    private DamageSource restorePendingDeathSource(
            ServerLevel serverLevel
    ) {
        Entity killer = null;

        if (this.pendingDeathKillerUuid != null) {
            killer =
                    serverLevel.getEntityInAnyDimension(
                            this.pendingDeathKillerUuid
                    );

            this.setLastHurtByPlayer(
                    this.pendingDeathKillerUuid,
                    DEATH_KILL_CREDIT_MEMORY_TICKS
            );
        }

        if (this.pendingDeathDamageType != null) {
            Identifier identifier =
                    Identifier.tryParse(
                            this.pendingDeathDamageType
                    );

            if (identifier != null) {
                ResourceKey<DamageType> damageTypeKey =
                        ResourceKey.create(
                                Registries.DAMAGE_TYPE,
                                identifier
                        );

                var damageType =
                        serverLevel.registryAccess()
                                .lookupOrThrow(
                                        Registries.DAMAGE_TYPE
                                )
                                .get(damageTypeKey);

                if (damageType.isPresent()) {
                    return killer == null
                            ? new DamageSource(
                            damageType.get()
                    )
                            : new DamageSource(
                            damageType.get(),
                            killer
                    );
                }
            }
        }

        var genericKillType =
                serverLevel.registryAccess()
                        .lookupOrThrow(
                                Registries.DAMAGE_TYPE
                        )
                        .getOrThrow(
                                net.minecraft.world.damagesource.DamageTypes.GENERIC_KILL
                        );

        return killer == null
                ? new DamageSource(genericKillType)
                : new DamageSource(
                        genericKillType,
                        killer
                );
    }

    private void cleanupDeathCinematic(
            boolean notifyClients
    ) {
        StarCrawlerBossDeathManager.releaseBoss(
                this,
                notifyClients
        );

        this.deathCinematicParticipants.clear();
        this.deathCinematicAnchor = null;
    }

    private void setDeathCinematicState(
            DeathCinematicState state
    ) {
        this.entityData.set(
                DEATH_CINEMATIC_STATE,
                state.id
        );
    }

    private void updateLastSafeDeathCinematicAnchor(
            ServerLevel serverLevel
    ) {
        if (this.onGround()
                && !this.isInLava()
                && this.getY() >= serverLevel.getMinY() + 1.0D) {
            this.lastSafeDeathCinematicAnchor = this.position();
        }
    }

    private Vec3 resolveDeathCinematicAnchor(
            ServerLevel serverLevel
    ) {
        if (this.getY() >= serverLevel.getMinY() + 2.0D) {
            return this.position();
        }

        if (this.lastSafeDeathCinematicAnchor != null) {
            return this.lastSafeDeathCinematicAnchor;
        }

        int surfaceY = serverLevel.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mth.floor(this.getX()),
                Mth.floor(this.getZ())
        );

        double fallbackY = surfaceY > serverLevel.getMinY()
                ? surfaceY
                : serverLevel.getMinY() + 8.0D;

        return new Vec3(
                this.getX(),
                Mth.clamp(
                        fallbackY,
                        serverLevel.getMinY() + 2.0D,
                        serverLevel.getMaxY() - this.getBbHeight() - 1.0D
                ),
                this.getZ()
        );
    }

    private int elapsedSince(long start) {
        return (int) Mth.clamp(
                this.level().getGameTime() - start,
                0L,
                Integer.MAX_VALUE
        );
    }

    private void tickClientAnimations() {
        CombatState state =
                this.getCombatState();

        int actionStartTick =
                this.tickCount
                        - this.getActionTicks();

        if (state.isHealing()) {
            this.clientWalkHoldTicks = 0;

            this.stopMovementAnimations();

            this.stopAttackAnimationsExcept(
                    this.healingAnimationState
            );

            this.healingAnimationState.startIfStopped(
                    actionStartTick
            );

            return;
        }

        if (state.isCharge()) {
            this.clientWalkHoldTicks = 0;

            this.stopMovementAnimations();

            this.stopAttackAnimationsExcept(
                    this.chargeAnimationState
            );

            this.chargeAnimationState.startIfStopped(
                    actionStartTick
            );

            return;
        }

        if (state.isJumpSlam()) {
            this.clientWalkHoldTicks = 0;

            this.stopMovementAnimations();

            this.stopAttackAnimationsExcept(
                    this.jumpSlamAnimationState
            );

            this.jumpSlamAnimationState.startIfStopped(
                    actionStartTick
            );

            return;
        }

        if (state.isGroundSmash()) {
            this.clientWalkHoldTicks = 0;

            this.stopMovementAnimations();

            this.stopAttackAnimationsExcept(
                    this.groundSmashAnimationState
            );

            this.groundSmashAnimationState.startIfStopped(
                    actionStartTick
            );

            return;
        }

        this.stopAllActionAnimations();

        double horizontalSpeedSqr =
                this.getDeltaMovement()
                        .horizontalDistanceSqr();

        if (horizontalSpeedSqr
                >= CLIENT_WALK_START_SPEED_SQR) {

            this.clientWalkHoldTicks =
                    CLIENT_WALK_HOLD_TICKS;
        }

        else if (horizontalSpeedSqr
                >= CLIENT_WALK_KEEP_SPEED_SQR
                && this.clientWalkHoldTicks > 0) {

            this.clientWalkHoldTicks =
                    CLIENT_WALK_HOLD_TICKS;
        }

        else if (this.clientWalkHoldTicks > 0) {
            this.clientWalkHoldTicks--;
        }

        boolean walking =
                (state == CombatState.CHASING
                        || state == CombatState.IDLE)
                        && (
                        horizontalSpeedSqr
                                >= CLIENT_WALK_START_SPEED_SQR
                                || this.clientWalkHoldTicks > 0
                );

        if (walking) {
            this.idleAnimationState.stop();

            this.walkingAnimationState.startIfStopped(
                    this.tickCount
            );
        } else {
            this.walkingAnimationState.stop();

            this.idleAnimationState.startIfStopped(
                    this.tickCount
            );
        }
    }

    private void stopMovementAnimations() {
        this.walkingAnimationState.stop();
        this.idleAnimationState.stop();
    }

    private void stopAttackAnimationsExcept(
            AnimationState preserved
    ) {
        if (preserved
                != this.chargeAnimationState) {

            this.chargeAnimationState.stop();
        }

        if (preserved
                != this.jumpSlamAnimationState) {

            this.jumpSlamAnimationState.stop();
        }

        if (preserved
                != this.groundSmashAnimationState) {

            this.groundSmashAnimationState.stop();
        }

        if (preserved
                != this.healingAnimationState) {

            this.healingAnimationState.stop();
        }
    }

    private void stopAllActionAnimations() {
        this.chargeAnimationState.stop();
        this.jumpSlamAnimationState.stop();
        this.groundSmashAnimationState.stop();
        this.healingAnimationState.stop();
    }

    private void updateBossPresentation() {

        this.bossEvent.setOverlay(
                BossEvent.BossBarOverlay.PROGRESS
        );


        this.bossEvent.setName(
                Component.empty()
        );

        if (this.isHealing()) {
            this.bossEvent.setColor(
                    BossEvent.BossBarColor.YELLOW
            );

            return;
        }

        switch (this.getPhase()) {
            case PHASE_1 ->
                    this.bossEvent.setColor(
                            BossEvent.BossBarColor.PURPLE
                    );

            case PHASE_2 ->
                    this.bossEvent.setColor(
                            BossEvent.BossBarColor.BLUE
                    );

            case PHASE_3 ->
                    this.bossEvent.setColor(
                            BossEvent.BossBarColor.RED
                    );
        }
    }

    private void beginCharge(
            LivingEntity target
    ) {
        Vec3 predicted =
                target.position()
                        .add(
                                target.getDeltaMovement()
                                        .scale(6.0D)
                        );

        this.committedDestination =
                predicted;

        this.chargeDirection =
                Vec3.ZERO;

        this.chargeHitTargets.clear();

        this.lastAttack =
                AttackKind.CHARGE;

        this.beginAction(
                CombatState.CHARGE_WINDUP
        );

        this.playHostileSound(
                SoundEvents.BREEZE_CHARGE,
                1.2F,
                0.55F
        );
    }

    private void tickChargeWindup(
            ServerLevel serverLevel
    ) {
        this.holdPosition();

        this.lookAtCommittedDestination();

        StarCrawlerBossVfx.chargeWindup(
                serverLevel,
                this,
                this.getActionTicks()
        );

        if (this.getActionTicks()
                < CHARGE_LAUNCH_TICK) {

            return;
        }

        Vec3 horizontal =
                this.committedDestination == null
                        ? Vec3.directionFromRotation(
                                        0.0F,
                                        this.getYRot()
                                )
                                .horizontal()
                        : this.committedDestination
                                .subtract(
                                        this.position()
                                )
                                .multiply(
                                        1.0D,
                                        0.0D,
                                        1.0D
                                );

        if (horizontal.horizontalDistanceSqr()
                < 1.0E-4D) {

            horizontal =
                    Vec3.directionFromRotation(
                                    0.0F,
                                    this.getYRot()
                            )
                            .horizontal();
        }

        this.chargeDirection =
                horizontal.normalize();

        this.transitionWithinAction(
                CombatState.CHARGING
        );

        this.setDeltaMovement(
                this.chargeDirection.x
                        * CHARGE_SPEED,
                this.getDeltaMovement().y,
                this.chargeDirection.z
                        * CHARGE_SPEED
        );

        this.hurtMarked = true;

        StarCrawlerBossVfx.chargeLaunch(
                serverLevel,
                this
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_ROAR,
                SoundSource.HOSTILE,
                1.0F,
                1.25F
        );
    }

    private void tickCharging(
            ServerLevel serverLevel
    ) {
        this.getNavigation().stop();

        this.setDeltaMovement(
                this.chargeDirection.x
                        * CHARGE_SPEED,
                this.getDeltaMovement().y,
                this.chargeDirection.z
                        * CHARGE_SPEED
        );

        this.hurtMarked = true;

        this.damageChargeContacts(
                serverLevel
        );

        StarCrawlerBossVfx.chargeTrail(
                serverLevel,
                this,
                this.getActionTicks()
        );

        if (this.horizontalCollision
                || this.getActionTicks()
                >= CHARGE_END_TICK) {

            if (this.horizontalCollision) {
                StarCrawlerBossVfx.chargeCrash(
                        serverLevel,
                        this
                );
            }

            this.stopHorizontalMovement();

            this.transitionWithinAction(
                    CombatState.CHARGE_RECOVERY
            );
        }
    }

    private void damageChargeContacts(
            ServerLevel serverLevel
    ) {
        AABB hitBox =
                this.getBoundingBox()
                        .inflate(
                                CHARGE_HIT_INFLATION,
                                0.25D,
                                CHARGE_HIT_INFLATION
                        );

        for (LivingEntity target :
                serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        hitBox,
                        this::isValidDamageTarget
                )) {

            if (!this.chargeHitTargets.add(
                    target.getUUID()
            )) {
                continue;
            }

            target.hurtServer(
                    serverLevel,
                    this.damageSources()
                            .mobAttack(this),
                    CHARGE_DAMAGE
            );

            StarCrawlerBossVfx.chargeHit(
                    serverLevel,
                    target
            );
        }
    }

    private void tickChargeRecovery() {
        this.holdPosition();

        if (this.getActionTicks()
                >= CHARGE_CLIP_END_TICK) {

            this.finishAction();
        }
    }

    private void beginJumpSlam(
            LivingEntity target
    ) {
        Vec3 prediction =
                target.position()
                        .add(
                                target.getDeltaMovement()
                                        .scale(4.0D)
                        );

        Vec3 offset =
                prediction
                        .subtract(this.position())
                        .multiply(
                                1.0D,
                                0.0D,
                                1.0D
                        );

        if (offset.horizontalDistance()
                > 13.0D) {

            offset =
                    offset.normalize()
                            .scale(13.0D);
        }

        this.committedDestination =
                this.position()
                        .add(offset);

        this.jumpWasAirborne =
                false;

        this.lastAttack =
                AttackKind.JUMP_SLAM;

        this.beginAction(
                CombatState.JUMP_SLAM_WINDUP
        );

        this.playHostileSound(
                SoundEvents.BREEZE_INHALE,
                1.1F,
                0.62F
        );
    }

    private void tickJumpWindup(
            ServerLevel serverLevel
    ) {
        this.holdPosition();

        this.lookAtCommittedDestination();

        StarCrawlerBossVfx.jumpWindup(
                serverLevel,
                this,
                this.getActionTicks()
        );

        if (this.getActionTicks()
                < JUMP_TAKEOFF_TICK) {

            return;
        }

        Vec3 offset =
                this.committedDestination == null
                        ? Vec3.directionFromRotation(
                                        0.0F,
                                        this.getYRot()
                                )
                                .horizontal()
                                .scale(4.0D)
                        : this.committedDestination
                                .subtract(
                                        this.position()
                                )
                                .multiply(
                                        1.0D,
                                        0.0D,
                                        1.0D
                                );

        double distance =
                offset.horizontalDistance();

        Vec3 direction =
                distance < 1.0E-4D
                        ? Vec3.directionFromRotation(
                                        0.0F,
                                        this.getYRot()
                                )
                                .horizontal()
                                .normalize()
                        : offset.normalize();

        double speed =
                Mth.clamp(
                        distance
                                / JUMP_EXPECTED_FLIGHT_TICKS,
                        JUMP_MIN_HORIZONTAL_VELOCITY,
                        JUMP_MAX_HORIZONTAL_VELOCITY
                );

        this.transitionWithinAction(
                CombatState.JUMP_SLAM_AIRBORNE
        );

        this.scriptedJumpFallProtection =
                true;

        this.setOnGround(false);

        this.fallDistance =
                0.0F;

        this.jumpHorizontalVelocity =
                direction.scale(speed);

        this.setDeltaMovement(
                this.jumpHorizontalVelocity.x,
                JUMP_VERTICAL_VELOCITY,
                this.jumpHorizontalVelocity.z
        );

        this.hurtMarked = true;

        StarCrawlerBossVfx.jumpTakeoff(
                serverLevel,
                this
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.BREEZE_JUMP,
                SoundSource.HOSTILE,
                1.15F,
                0.55F
        );
    }

    private void tickJumpAirborne(
            ServerLevel serverLevel
    ) {
        this.getNavigation().stop();

        Vec3 currentVelocity =
                this.getDeltaMovement();

        this.setDeltaMovement(
                this.jumpHorizontalVelocity.x,
                currentVelocity.y,
                this.jumpHorizontalVelocity.z
        );

        this.hurtMarked = true;

        this.resetFallDistance();

        StarCrawlerBossVfx.jumpFlightTrail(
                serverLevel,
                this,
                this.getActionTicks()
        );

        if (!this.onGround()) {
            this.jumpWasAirborne = true;
        } else {
            this.scriptedJumpFallProtection =
                    false;
        }

        boolean landed =
                this.jumpWasAirborne
                        && this.onGround();

        if (landed) {
            this.stopHorizontalMovement();

            this.transitionWithinAction(
                    CombatState.JUMP_SLAM_IMPACT
            );

            this.performJumpImpact(
                    serverLevel
            );

            return;
        }

        if (this.getActionTicks()
                >= JUMP_ABORT_TICK) {

            this.stopHorizontalMovement();

            this.resetFallDistance();

            this.finishAction();

            return;
        }

        if (this.getActionTicks()
                >= JUMP_FORCE_DESCENT_TICK) {

            Vec3 velocity =
                    this.getDeltaMovement();

            this.setDeltaMovement(
                    velocity.x * 0.75D,
                    Math.min(
                            velocity.y,
                            -0.75D
                    ),
                    velocity.z * 0.75D
            );

            this.hurtMarked =
                    true;
        }
    }

    private void performJumpImpact(
            ServerLevel serverLevel
    ) {
        this.damageRadius(
                serverLevel,
                JUMP_SLAM_RADIUS,
                JUMP_SLAM_DAMAGE
        );

        this.emitImpact(
                serverLevel,
                JUMP_SLAM_RADIUS,
                false
        );

        StarCrawlerBossVfx.jumpImpact(
                serverLevel,
                this
        );

        this.sendCameraShake(
                serverLevel,
                24.0D,
                10,
                0.85F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.BREEZE_LAND,
                SoundSource.HOSTILE,
                1.4F,
                0.52F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_ATTACK_IMPACT,
                SoundSource.HOSTILE,
                0.75F,
                0.82F
        );
    }

    private void tickJumpImpact() {
        this.holdPosition();

        if (this.getCombatStateTicks()
                >= 1) {

            this.transitionWithinAction(
                    CombatState.JUMP_SLAM_RECOVERY
            );
        }
    }

    private void tickJumpRecovery(
            ServerLevel serverLevel
    ) {
        this.holdPosition();

        StarCrawlerBossVfx.jumpShockwave(
                serverLevel,
                this,
                this.getCombatStateTicks()
        );

        if (this.getActionTicks()
                >= JUMP_CLIP_END_TICK) {

            this.finishAction();
        }
    }

    private void beginGroundSmash(
            boolean forced
    ) {
        this.forcedFinalSmash =
                forced;

        this.lastAttack =
                AttackKind.GROUND_SMASH;

        this.beginAction(
                CombatState.GROUND_SMASH_WINDUP
        );

        this.playHostileSound(
                SoundEvents.WARDEN_SONIC_CHARGE,
                1.25F,
                0.68F
        );
    }

    private void tickGroundSmashWindup(
            ServerLevel serverLevel
    ) {
        this.holdPosition();

        StarCrawlerBossVfx.groundSmashWindup(
                serverLevel,
                this,
                this.getActionTicks()
        );

        LivingEntity target =
                this.getTarget();

        if (this.isValidAttackTarget(
                target
        )) {
            this.getLookControl()
                    .setLookAt(
                            target,
                            25.0F,
                            25.0F
                    );
        }

        if (this.getActionTicks()
                >= GROUND_SMASH_IMPACT_TICK) {

            this.transitionWithinAction(
                    CombatState.GROUND_SMASH_IMPACT
            );

            this.performGroundSmashImpact(
                    serverLevel
            );
        }
    }

    private void performGroundSmashImpact(
            ServerLevel serverLevel
    ) {
        this.damageRadius(
                serverLevel,
                GROUND_SMASH_RADIUS,
                GROUND_SMASH_DAMAGE
        );

        this.emitImpact(
                serverLevel,
                GROUND_SMASH_RADIUS,
                true
        );

        StarCrawlerBossVfx.groundSmashImpact(
                serverLevel,
                this,
                this.forcedFinalSmash
        );

        this.sendCameraShake(
                serverLevel,
                30.0D,
                14,
                1.25F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_ATTACK_IMPACT,
                SoundSource.HOSTILE,
                1.65F,
                0.48F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.HOSTILE,
                0.55F,
                0.72F
        );

        if (this.forcedFinalSmash
                && !this.finalMinionsSpawned) {

            this.finalMinionsSpawned =
                    true;

            this.spawnFinalMinions(
                    serverLevel
            );
        }
    }

    private void tickGroundSmashImpact() {
        this.holdPosition();

        if (this.getCombatStateTicks()
                >= 1) {

            this.transitionWithinAction(
                    CombatState.GROUND_SMASH_RECOVERY
            );
        }
    }

    private void tickGroundSmashRecovery(
            ServerLevel serverLevel
    ) {
        this.holdPosition();

        StarCrawlerBossVfx.groundSmashShockwave(
                serverLevel,
                this,
                this.getCombatStateTicks(),
                this.forcedFinalSmash
        );

        if (this.getActionTicks()
                >= GROUND_SMASH_CLIP_END_TICK) {

            this.finishAction();
        }
    }

    private void beginHealing(
            ServerLevel serverLevel,
            boolean secondHeal
    ) {
        this.getNavigation().stop();

        this.stopHorizontalMovement();

        Vec3 velocity =
                this.getDeltaMovement();

        if (!this.onGround()) {
            this.setDeltaMovement(
                    0.0D,
                    Math.min(
                            velocity.y,
                            -0.55D
                    ),
                    0.0D
            );

            this.hurtMarked =
                    true;
        }

        this.resetFallDistance();

        this.chargeHitTargets.clear();

        this.committedDestination =
                null;

        if (secondHeal) {
            this.secondHealUsed =
                    true;

            this.healingTargetHealth =
                    SECOND_HEAL_TARGET;

            this.beginAction(
                    CombatState.HEALING_PHASE_3
            );
        } else {
            this.firstHealUsed =
                    true;

            this.healingTargetHealth =
                    FIRST_HEAL_TARGET;

            this.beginAction(
                    CombatState.HEALING_PHASE_2
            );
        }

        this.healingStartHealth =
                Math.min(
                        this.getHealth(),
                        this.healingTargetHealth
                );

        this.healingDamageSustained =
                0.0F;

        float healthEnergy =
                0.06F
                        + Mth.clamp(
                        this.getHealth()
                                / this.getMaxHealth(),
                        0.0F,
                        1.0F
                ) * 0.94F;

        this.healingStartEnergy =
                Math.min(
                        this.getCrystalEnergy(),
                        healthEnergy
                );

        this.healingTargetEnergy =
                secondHeal
                        ? 1.0F
                        : 0.86F;

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.HOSTILE,
                1.4F,
                secondHeal
                        ? 0.48F
                        : 0.62F
        );

        /*
         * Ritual opening rings.
         */
        StarCrawlerBossVfx.healingStart(
                serverLevel,
                this,
                secondHeal
        );
    }

    private void tickHealing(
            ServerLevel serverLevel
    ) {
        this.holdPosition();

        int elapsed =
                Math.min(
                        this.getActionTicks(),
                        HEAL_DURATION_TICKS
                );

        float progress =
                elapsed
                        / (float) HEAL_DURATION_TICKS;

        float scriptedHealth =
                Mth.lerp(
                        progress,
                        this.healingStartHealth,
                        this.healingTargetHealth
                )
                        - this.healingDamageSustained
                        * (1.0F - progress);

        this.setHealth(
                Math.min(
                        this.healingTargetHealth,
                        Math.max(
                                this.getHealth(),
                                scriptedHealth
                        )
                )
        );

        boolean secondHeal =
                this.getCombatState()
                        == CombatState.HEALING_PHASE_3;

        StarCrawlerBossVfx.healingTick(
                serverLevel,
                this,
                elapsed,
                secondHeal
        );

        if (elapsed == 28
                || elapsed == 44
                || elapsed == 60) {

            serverLevel.playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.AMETHYST_BLOCK_RESONATE,
                    SoundSource.HOSTILE,
                    1.1F,
                    0.72F
                            + elapsed
                            * 0.005F
            );
        }

        if (this.getActionTicks()
                < HEAL_DURATION_TICKS) {

            return;
        }

        this.setHealth(
                this.healingTargetHealth
        );

        boolean enteringPhaseThree =
                this.getCombatState()
                        == CombatState.HEALING_PHASE_3;

        this.entityData.set(
                COMBAT_PHASE,
                enteringPhaseThree
                        ? CombatPhase.PHASE_3.id
                        : CombatPhase.PHASE_2.id
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.BEACON_POWER_SELECT,
                SoundSource.HOSTILE,
                1.45F,
                enteringPhaseThree
                        ? 0.72F
                        : 0.88F
        );

        StarCrawlerBossVfx.healingFinish(
                serverLevel,
                this,
                enteringPhaseThree
        );

        if (enteringPhaseThree) {
            this.pendingForcedGroundSmash =
                    false;

            this.pendingForcedGroundSmashTicks =
                    0;

            this.beginGroundSmash(
                    true
            );
        } else {
            this.finishAction();
        }
    }

    private void checkHealthTransitions(
            ServerLevel serverLevel
    ) {
        if (!this.firstHealUsed
                && this.getHealth()
                < FIRST_HEAL_TRIGGER) {

            this.beginHealing(
                    serverLevel,
                    false
            );

            return;
        }

        if (this.firstHealUsed
                && !this.secondHealUsed
                && this.getPhase()
                .isAtLeast(
                        CombatPhase.PHASE_2
                )
                && this.getHealth()
                < SECOND_HEAL_TRIGGER) {

            this.beginHealing(
                    serverLevel,
                    true
            );
        }
    }

    @Override
    public boolean hurtServer(
            ServerLevel serverLevel,
            DamageSource source,
            float amount
    ) {
        if (this.finalizingDeath) {
            return super.hurtServer(
                    serverLevel,
                    source,
                    amount
            );
        }

        if (this.getDeathCinematicState()
                != DeathCinematicState.ALIVE) {

            return false;
        }

        if (this.getIntroState()
                != IntroState.COMPLETE
                && !source.is(
                DamageTypeTags.BYPASSES_INVULNERABILITY
        )) {

            return false;
        }

        if (amount <= 0.0F) {
            return super.hurtServer(
                    serverLevel,
                    source,
                    amount
            );
        }

        if (this.isHealing()
                && !source.is(
                DamageTypeTags.BYPASSES_INVULNERABILITY
        )
                && !source.isCreativePlayer()) {

            amount *=
                    this.getCombatState()
                            == CombatState.HEALING_PHASE_3

                            ? SECOND_HEAL_DAMAGE_MULTIPLIER
                            : FIRST_HEAL_DAMAGE_MULTIPLIER;
        }

        float transitionTrigger =
                this.nextTransitionTrigger();

        float effectiveRawDamage =
                this.effectiveRawDamageDuringHurtCooldown(
                        source,
                        amount
                );

        float predictedDamage =
                this.predictHealthDamage(
                        source,
                        effectiveRawDamage
                );

        boolean protectedTransition =
                transitionTrigger > 0.0F
                        && !source.is(
                        DamageTypeTags.BYPASSES_INVULNERABILITY
                )
                        && !source.isCreativePlayer()
                        && this.getHealth()
                        >= transitionTrigger
                        && this.getHealth()
                        - predictedDamage
                        < transitionTrigger;

        if (protectedTransition) {
            float desiredHealthDamage =
                    this.getHealth()
                            - (transitionTrigger - 0.01F);

            float cappedEffectiveRawDamage =
                    this.rawDamageForHealthDamage(
                            source,
                            desiredHealthDamage,
                            effectiveRawDamage
                    );

            amount =
                    this.rawAmountForEffectiveCooldownDamage(
                            source,
                            cappedEffectiveRawDamage
                    );
        }

        boolean wasHealingBeforeDamage =
                this.isHealing();

        float healthBeforeDamage =
                this.getHealth();

        boolean hurt =
                super.hurtServer(
                        serverLevel,
                        source,
                        amount
                );

        if (this.isDeathCinematicPlaying()) {
            return hurt;
        }

        if (!hurt
                || !this.isAlive()) {

            return hurt;
        }

        if (protectedTransition) {
            if (this.getHealth()
                    >= transitionTrigger) {

                this.setHealth(
                        transitionTrigger
                                - 0.01F
                );
            }

            this.checkHealthTransitions(
                    serverLevel
            );
        }

        else if (!this.isHealing()) {
            this.checkHealthTransitions(
                    serverLevel
            );
        }

        if (wasHealingBeforeDamage
                && this.isHealing()) {

            float damageTaken =
                    Math.max(
                            0.0F,
                            healthBeforeDamage
                                    - this.getHealth()
                    );

            float progressAtHit =
                    Mth.clamp(
                            this.getActionTicks()
                                    / (float) HEAL_DURATION_TICKS,
                            0.0F,
                            0.9875F
                    );

            this.healingDamageSustained +=
                    damageTaken
                            / (1.0F - progressAtHit);
        }

        return true;
    }

    private float effectiveRawDamageDuringHurtCooldown(
            DamageSource source,
            float amount
    ) {
        if (this.invulnerableTime > 10
                && !source.is(
                DamageTypeTags.BYPASSES_COOLDOWN
        )) {

            return Math.max(
                    0.0F,
                    amount - this.lastHurt
            );
        }

        return amount;
    }

    private float rawAmountForEffectiveCooldownDamage(
            DamageSource source,
            float effectiveRawDamage
    ) {
        if (this.invulnerableTime > 10
                && !source.is(
                DamageTypeTags.BYPASSES_COOLDOWN
        )) {

            return this.lastHurt
                    + effectiveRawDamage;
        }

        return effectiveRawDamage;
    }

    private float predictHealthDamage(
            DamageSource source,
            float rawDamage
    ) {
        float damageAfterArmor =
                source.is(
                        DamageTypeTags.BYPASSES_ARMOR
                )

                        ? rawDamage

                        : CombatRules.getDamageAfterAbsorb(
                                this,
                                rawDamage,
                                source,
                                this.getArmorValue(),
                                (float) this.getAttributeValue(
                                        Attributes.ARMOR_TOUGHNESS
                                )
                        );

        return Math.max(
                0.0F,
                damageAfterArmor
                        - this.getAbsorptionAmount()
        );
    }

    private float rawDamageForHealthDamage(
            DamageSource source,
            float desiredHealthDamage,
            float upperBound
    ) {
        float low =
                0.0F;

        float high =
                upperBound;

        for (int iteration = 0;
             iteration < 12;
             iteration++) {

            float middle =
                    (low + high)
                            * 0.5F;

            if (this.predictHealthDamage(
                    source,
                    middle
            ) < desiredHealthDamage) {

                low =
                        middle;
            } else {
                high =
                        middle;
            }
        }

        return high;
    }

    private float nextTransitionTrigger() {
        if (this.isHealing()) {
            return -1.0F;
        }

        if (!this.firstHealUsed) {
            return FIRST_HEAL_TRIGGER;
        }

        if (!this.secondHealUsed
                && this.getPhase()
                .isAtLeast(
                        CombatPhase.PHASE_2
                )) {

            return SECOND_HEAL_TRIGGER;
        }

        return -1.0F;
    }

    private void damageRadius(
            ServerLevel serverLevel,
            double radius,
            float damage
    ) {
        double radiusSqr =
                radius * radius;

        AABB bounds =
                this.getBoundingBox()
                        .inflate(radius);

        for (LivingEntity target :
                serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        bounds,
                        this::isValidDamageTarget
                )) {

            if (this.position()
                    .distanceToSqr(
                            target.position()
                    )
                    > radiusSqr) {

                continue;
            }

            target.hurtServer(
                    serverLevel,
                    this.damageSources()
                            .mobAttack(this),
                    damage
            );
        }
    }

    private boolean isValidDamageTarget(
            LivingEntity target
    ) {
        return target != this

                && !(target
                instanceof StarCrawlerBossEntity)

                && !(target
                instanceof StarCrawlerEntity)

                && target.isAlive()

                && !target.isRemoved()

                && !this.isAlliedTo(target)

                && this.canAttack(target);
    }

    private boolean isValidAttackTarget(
            LivingEntity target
    ) {
        return target != null
                && this.isValidDamageTarget(
                target
        );
    }

    private void emitImpact(
            ServerLevel serverLevel,
            double radius,
            boolean strongest
    ) {
        int samples =
                strongest
                        ? 32
                        : 24;

        for (int i = 0;
             i < samples;
             i++) {

            double angle =
                    Math.PI
                            * 2.0D
                            * i
                            / samples;

            double distance =
                    radius
                            * (
                            0.35D
                                    + 0.65D
                                    * (
                                    (i % 5)
                                            / 4.0D
                            )
                    );

            double x =
                    this.getX()
                            + Math.cos(angle)
                            * distance;

            double z =
                    this.getZ()
                            + Math.sin(angle)
                            * distance;

            BlockPos surface =
                    BlockPos.containing(
                            x,
                            this.getY()
                                    - 0.2D,
                            z
                    );

            BlockState state =
                    serverLevel.getBlockState(
                            surface
                    );

            if (!state.isAir()) {
                serverLevel.sendParticles(
                        new BlockParticleOption(
                                ParticleTypes.BLOCK,
                                state
                        ),
                        x,
                        this.getY() + 0.25D,
                        z,
                        strongest
                                ? 5
                                : 3,
                        0.22D,
                        0.18D,
                        0.22D,
                        0.14D
                );
            }
        }

        serverLevel.sendParticles(
                ParticleTypes.DUST_PLUME,
                this.getX(),
                this.getY()
                        + 0.25D,
                this.getZ(),
                strongest
                        ? 42
                        : 28,
                radius * 0.55D,
                0.25D,
                radius * 0.55D,
                0.08D
        );
    }

    private void sendCameraShake(
            ServerLevel serverLevel,
            double radius,
            int ticks,
            float intensity
    ) {
        double radiusSqr =
                radius * radius;

        for (ServerPlayer player :
                serverLevel.players()) {

            double distanceSqr =
                    player.distanceToSqr(this);

            if (distanceSqr
                    > radiusSqr) {

                continue;
            }

            float attenuation =
                    1.0F
                            - (float) (
                            Math.sqrt(
                                    distanceSqr
                            )
                                    / radius
                    );

            float playerIntensity =
                    intensity
                            * Mth.clamp(
                            attenuation,
                            0.0F,
                            1.0F
                    );

            if (playerIntensity
                    <= 0.02F) {

                continue;
            }

            NetworkManager.sendToPlayer(
                    player,
                    new ParasiteCameraShakePacket(
                            ticks,
                            playerIntensity
                    )
            );
        }
    }

    private void spawnFinalMinions(
            ServerLevel serverLevel
    ) {
        LivingEntity target =
                this.getTarget();

        double phaseOffset =
                this.random.nextDouble()
                        * Math.PI
                        * 2.0D;

        List<Vec3> spawnPositions =
                this.collectFinalMinionSpawnPositions(
                        serverLevel,
                        phaseOffset
                );

        for (Vec3 spawnPosition :
                spawnPositions) {

            StarCrawlerEntity minion =
                    EntityTypesRegistry.STAR_CRAWLER
                            .get()
                            .create(
                                    serverLevel,
                                    EntitySpawnReason.MOB_SUMMONED
                            );

            if (minion == null) {
                continue;
            }

            minion.snapTo(
                    spawnPosition.x,
                    spawnPosition.y,
                    spawnPosition.z,
                    this.random.nextFloat()
                            * 360.0F,
                    0.0F
            );

            if (!serverLevel.noCollision(
                    minion
            )) {
                continue;
            }

            minion.finalizeSpawn(
                    serverLevel,
                    serverLevel.getCurrentDifficultyAt(
                            minion.blockPosition()
                    ),
                    EntitySpawnReason.MOB_SUMMONED,
                    null
            );

            minion.setPersistenceRequired();

            if (this.isValidAttackTarget(
                    target
            )) {
                minion.setTarget(target);
            }

            if (serverLevel.addFreshEntity(
                    minion
            )) {
                minion.spawnAnim();

                serverLevel.sendParticles(
                        ParticleTypes.REVERSE_PORTAL,
                        minion.getX(),
                        minion.getY()
                                + 0.5D,
                        minion.getZ(),
                        14,
                        0.55D,
                        0.45D,
                        0.55D,
                        0.08D
                );
            }
        }

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WITHER_SPAWN,
                SoundSource.HOSTILE,
                0.85F,
                1.45F
        );
    }

    private List<Vec3> collectFinalMinionSpawnPositions(
            ServerLevel serverLevel,
            double phaseOffset
    ) {
        record Candidate(
                Vec3 position,
                double distanceSqr
        ) {
        }

        List<Candidate> candidates =
                new ArrayList<>();

        for (int ring = 0;
             ring < 5;
             ring++) {

            double radius =
                    3.5D
                            + ring
                            * 0.8D;

            int samples =
                    12
                            + ring
                            * 4;

            for (int sample = 0;
                 sample < samples;
                 sample++) {

                double angle =
                        phaseOffset
                                + Math.PI
                                * 2.0D
                                * sample
                                / samples;

                int x =
                        Mth.floor(
                                this.getX()
                                        + Math.cos(angle)
                                        * radius
                        );

                int z =
                        Mth.floor(
                                this.getZ()
                                        + Math.sin(angle)
                                        * radius
                        );

                int y =
                        this.findLocalSpawnY(
                                serverLevel,
                                x,
                                z
                        );

                if (y
                        == Integer.MIN_VALUE) {

                    continue;
                }

                Vec3 position =
                        new Vec3(
                                x + 0.5D,
                                y,
                                z + 0.5D
                        );

                candidates.add(
                        new Candidate(
                                position,
                                this.position()
                                        .distanceToSqr(
                                                position
                                        )
                        )
                );
            }
        }

        candidates.sort(
                Comparator.comparingDouble(
                        Candidate::distanceSqr
                )
        );

        List<Vec3> selected =
                new ArrayList<>(
                        FINAL_MINION_COUNT
                );

        for (Candidate candidate :
                candidates) {

            if (selected.stream()
                    .allMatch(
                            position ->
                                    position.distanceToSqr(
                                            candidate.position()
                                    )
                                            >= 4.0D
                    )) {

                selected.add(
                        candidate.position()
                );

                if (selected.size()
                        >= FINAL_MINION_COUNT) {

                    break;
                }
            }
        }

        return selected;
    }

    private int findLocalSpawnY(
            ServerLevel serverLevel,
            int x,
            int z
    ) {
        int centerY =
                Mth.floor(
                        this.getY()
                );

        int minimumY =
                Math.max(
                        serverLevel.getMinY()
                                + 1,
                        centerY - 5
                );

        int maximumY =
                Math.min(
                        serverLevel.getMaxY()
                                - 2,
                        centerY + 5
                );

        BlockPos.MutableBlockPos pos =
                new BlockPos.MutableBlockPos();

        for (int offset = 0;
             offset <= 5;
             offset++) {

            int below =
                    centerY - offset;

            if (below >= minimumY
                    && this.hasSpawnFooting(
                    serverLevel,
                    pos,
                    x,
                    below,
                    z
            )) {
                return below;
            }

            if (offset > 0) {
                int above =
                        centerY + offset;

                if (above <= maximumY
                        && this.hasSpawnFooting(
                        serverLevel,
                        pos,
                        x,
                        above,
                        z
                )) {
                    return above;
                }
            }
        }

        return Integer.MIN_VALUE;
    }

    private boolean hasSpawnFooting(
            ServerLevel serverLevel,
            BlockPos.MutableBlockPos pos,
            int x,
            int y,
            int z
    ) {
        pos.set(
                x,
                y - 1,
                z
        );

        if (!serverLevel
                .getBlockState(pos)
                .isFaceSturdy(
                        serverLevel,
                        pos,
                        net.minecraft.core.Direction.UP
                )) {

            return false;
        }

        pos.set(
                x,
                y,
                z
        );

        if (!serverLevel
                .getBlockState(pos)
                .getCollisionShape(
                        serverLevel,
                        pos
                )
                .isEmpty()

                || !serverLevel
                .getFluidState(pos)
                .is(Fluids.EMPTY)) {

            return false;
        }

        pos.set(
                x,
                y + 1,
                z
        );

        return serverLevel
                .getBlockState(pos)
                .getCollisionShape(
                        serverLevel,
                        pos
                )
                .isEmpty()

                && serverLevel
                .getFluidState(pos)
                .is(Fluids.EMPTY);
    }

    private void tickPendingForcedGroundSmash() {
        this.pendingForcedGroundSmashTicks++;

        this.getNavigation().stop();

        this.stopHorizontalMovement();

        this.setDeltaMovement(
                0.0D,
                Math.min(
                        this.getDeltaMovement().y,
                        -0.45D
                ),
                0.0D
        );

        this.resetFallDistance();

        if (this.onGround()) {
            this.pendingForcedGroundSmash =
                    false;

            this.pendingForcedGroundSmashTicks =
                    0;

            this.beginGroundSmash(
                    true
            );

            return;
        }

        if (this.pendingForcedGroundSmashTicks
                < FORCED_SMASH_GROUNDING_TIMEOUT_TICKS) {

            return;
        }

        this.pendingForcedGroundSmashTicks =
                FORCED_SMASH_GROUNDING_TIMEOUT_TICKS;

        Vec3 safePosition =
                this.findForcedSmashGroundingPosition();

        if (safePosition != null
                && safePosition.y
                < this.getY()) {

            this.setPos(
                    this.getX(),
                    safePosition.y,
                    this.getZ()
            );

            this.setDeltaMovement(
                    Vec3.ZERO
            );

            this.resetFallDistance();
        }

        else if (this.isInWater()) {
            this.setDeltaMovement(
                    0.0D,
                    -0.2D,
                    0.0D
            );
        }
    }

    private Vec3 findForcedSmashGroundingPosition() {
        Vec3 anchor =
                this.pendingForcedSmashAnchor == null
                        ? this.position()
                        : this.pendingForcedSmashAnchor;

        BlockPos.MutableBlockPos pos =
                new BlockPos.MutableBlockPos();

        int x =
                Mth.floor(
                        this.getX()
                );

        int z =
                Mth.floor(
                        this.getZ()
                );

        int startY =
                Mth.floor(
                        Math.min(
                                anchor.y,
                                this.getY()
                        )
                );

        int minY =
                Math.max(
                        this.level().getMinY()
                                + 1,
                        startY - 12
                );

        for (int y = startY;
             y >= minY;
             y--) {

            if (!this.hasSpawnFooting(
                    (ServerLevel) this.level(),
                    pos,
                    x,
                    y,
                    z
            )) {
                continue;
            }

            Vec3 candidate =
                    new Vec3(
                            this.getX(),
                            y,
                            this.getZ()
                    );

            AABB candidateBounds =
                    this.getDimensions(
                                    this.getPose()
                            )
                            .makeBoundingBox(
                                    candidate
                            );

            if (this.level()
                    .noCollision(
                            this,
                            candidateBounds
                    )) {

                return candidate;
            }
        }

        return null;
    }

    private void updateCrystalEnergy() {
        float ratio =
                Mth.clamp(
                        this.getHealth()
                                / this.getMaxHealth(),
                        0.0F,
                        1.0F
                );

        float energy;

        if (this.isHealing()) {
            float progress =
                    Mth.clamp(
                            this.getActionTicks()
                                    / (float) HEAL_DURATION_TICKS,
                            0.0F,
                            1.0F
                    );

            energy =
                    Mth.lerp(
                            progress,
                            this.healingStartEnergy,
                            this.healingTargetEnergy
                    );
        }

        else if (this.getPhase()
                == CombatPhase.PHASE_3) {

            energy =
                    0.06F
                            + 0.94F
                            * Mth.clamp(
                            this.getHealth()
                                    / SECOND_HEAL_TARGET,
                            0.0F,
                            1.0F
                    );
        }

        else if (this.getPhase()
                == CombatPhase.PHASE_2) {

            float phaseHealth =
                    Mth.clamp(
                            (
                                    this.getHealth()
                                            - SECOND_HEAL_TRIGGER
                            )
                                    / (
                                    FIRST_HEAL_TARGET
                                            - SECOND_HEAL_TRIGGER
                            ),
                            0.0F,
                            1.0F
                    );

            energy =
                    0.16F
                            + 0.70F
                            * phaseHealth;
        }

        else {
            energy =
                    0.06F
                            + ratio
                            * 0.94F;
        }

        energy =
                Mth.clamp(
                        energy,
                        0.0F,
                        1.0F
                );

        if (Math.abs(
                energy
                        - this.entityData.get(
                        CRYSTAL_ENERGY
                )
        ) > 0.002F) {

            this.entityData.set(
                    CRYSTAL_ENERGY,
                    energy
            );
        }
    }

    private void beginAction(
            CombatState state
    ) {
        this.getNavigation().stop();

        this.entityData.set(
                ACTION_START_TIME,
                this.level().getGameTime()
        );

        this.setCombatState(state);
    }

    private void transitionWithinAction(
            CombatState state
    ) {
        this.setCombatState(state);
    }

    private void setCombatState(
            CombatState state
    ) {
        if (this.getCombatState()
                == state) {

            return;
        }

        this.entityData.set(
                COMBAT_STATE,
                state.id
        );

        this.entityData.set(
                COMBAT_STATE_START_TIME,
                this.level().getGameTime()
        );
    }

    private void finishAction() {
        this.committedDestination =
                null;

        this.chargeDirection =
                Vec3.ZERO;

        this.jumpHorizontalVelocity =
                Vec3.ZERO;

        this.chargeHitTargets.clear();

        this.jumpWasAirborne =
                false;

        this.forcedFinalSmash =
                false;

        this.pendingForcedSmashAnchor =
                null;

        this.entityData.set(
                ACTION_START_TIME,
                -1L
        );

        this.setCombatState(
                this.isValidAttackTarget(
                        this.getTarget()
                )

                        ? CombatState.CHASING
                        : CombatState.IDLE
        );

        this.attackCooldown =
                this.randomAttackCooldown();
    }

    private boolean isNeutralState() {
        CombatState state =
                this.getCombatState();

        return state
                == CombatState.IDLE

                || state
                == CombatState.CHASING;
    }

    private boolean isHealing() {
        return this.getCombatState()
                .isHealing();
    }

    private void holdPosition() {
        this.getNavigation().stop();

        this.stopHorizontalMovement();
    }

    private void holdDeathCinematicPosition() {
        this.getNavigation().stop();

        if (this.deathCinematicAnchor == null) {
            this.deathCinematicAnchor = this.position();
        }

        this.setPos(
                this.deathCinematicAnchor.x,
                this.deathCinematicAnchor.y,
                this.deathCinematicAnchor.z
        );
        this.setDeltaMovement(Vec3.ZERO);
        this.resetFallDistance();
    }

    private void stopHorizontalMovement() {
        Vec3 velocity =
                this.getDeltaMovement();

        this.setDeltaMovement(
                0.0D,
                velocity.y,
                0.0D
        );

        this.hurtMarked =
                true;
    }

    private void lookAtCommittedDestination() {
        if (this.committedDestination == null) {
            return;
        }

        this.getLookControl()
                .setLookAt(
                        this.committedDestination.x,
                        this.committedDestination.y,
                        this.committedDestination.z,
                        35.0F,
                        35.0F
                );
    }

    private void playHostileSound(
            SoundEvent sound,
            float volume,
            float pitch
    ) {
        if (!(this.level()
                instanceof ServerLevel serverLevel)) {

            return;
        }

        serverLevel.playSound(
                null,
                this.blockPosition(),
                sound,
                SoundSource.HOSTILE,
                volume,
                pitch
        );
    }

    private void playIntroSound(
            Iterable<ServerPlayer> participants,
            SoundEvent sound,
            float volume,
            float pitch,
            boolean localToParticipant
    ) {
        long seed =
                this.random.nextLong();

        for (ServerPlayer participant :
                participants) {

            if (participant.connection == null) {
                continue;
            }

            double soundX =
                    localToParticipant
                            ? participant.getX()
                            : this.getX();

            double soundY =
                    localToParticipant
                            ? participant.getY()
                            : this.getY();

            double soundZ =
                    localToParticipant
                            ? participant.getZ()
                            : this.getZ();

            participant.connection.send(
                    new ClientboundSoundPacket(
                            BuiltInRegistries.SOUND_EVENT
                                    .wrapAsHolder(sound),
                            SoundSource.HOSTILE,
                            soundX,
                            soundY,
                            soundZ,
                            volume,
                            pitch,
                            seed
                    )
            );
        }
    }

    private int randomAttackCooldown() {
        int min;
        int max;

        if (this.getPhase()
                == CombatPhase.PHASE_3) {

            min =
                    PHASE_THREE_COOLDOWN_MIN;

            max =
                    PHASE_THREE_COOLDOWN_MAX;
        }

        else if (this.getPhase()
                == CombatPhase.PHASE_2) {

            min =
                    PHASE_TWO_COOLDOWN_MIN;

            max =
                    PHASE_TWO_COOLDOWN_MAX;
        }

        else {
            min =
                    PHASE_ONE_COOLDOWN_MIN;

            max =
                    PHASE_ONE_COOLDOWN_MAX;
        }

        return min
                + this.random.nextInt(
                max - min + 1
        );
    }

    private void tryBeginContextualAttack(
            LivingEntity target
    ) {
        if (this.pendingForcedGroundSmash

                || !this.isNeutralState()

                || this.attackCooldown > 0

                || !this.onGround()

                || !this.isValidAttackTarget(
                target
        )) {

            return;
        }

        double distance =
                Math.sqrt(
                        this.distanceToSqr(
                                target
                        )
                );

        AttackKind[] choices;

        if (distance < 5.0D) {
            choices =
                    this.getPhase()
                            .isAtLeast(
                                    CombatPhase.PHASE_2
                            )

                            ? CLOSE_LATER_PHASE_ATTACKS
                            : CLOSE_PHASE_ONE_ATTACKS;
        }

        else if (distance < 11.0D) {
            choices =
                    MEDIUM_ATTACKS;
        }

        else {
            choices =
                    FAR_ATTACKS;
        }

        int eligibleCount =
                0;

        for (AttackKind choice :
                choices) {

            if (choice
                    != this.lastAttack) {

                eligibleCount++;
            }
        }

        AttackKind selected;

        if (eligibleCount == 0) {
            selected =
                    choices[
                            this.random.nextInt(
                                    choices.length
                            )
                            ];
        }

        else {
            int selectedIndex =
                    this.random.nextInt(
                            eligibleCount
                    );

            selected =
                    choices[0];

            for (AttackKind choice :
                    choices) {

                if (choice
                        != this.lastAttack
                        && selectedIndex-- == 0) {

                    selected =
                            choice;

                    break;
                }
            }
        }

        switch (selected) {
            case CHARGE ->
                    this.beginCharge(target);

            case JUMP_SLAM ->
                    this.beginJumpSlam(target);

            case GROUND_SMASH ->
                    this.beginGroundSmash(false);
        }
    }

    /**
     * Anchors the ring patrol the first time the boss ticks inside a moon
     * sphere. The centre comes from the structure's bounding box, so it stays
     * correct whichever rotation the jigsaw picked, and the radius is simply
     * wherever on the ring the boss happens to be standing.
     */
    private void resolvePatrolAnchor(
            ServerLevel serverLevel
    ) {
        if (this.patrolAnchorResolved
                || !this.onGround()
                || this.tickCount % PATROL_ANCHOR_RETRY_INTERVAL != 0) {

            return;
        }

        if (++this.patrolAnchorAttempts
                >= PATROL_ANCHOR_MAX_ATTEMPTS) {

            this.patrolAnchorResolved = true;
        }

        StructureStart start =
                serverLevel.structureManager()
                        .getStructureWithPieceAt(
                                this.blockPosition(),
                                holder -> holder.is(
                                        MOON_SPHERE_STRUCTURE
                                )
                        );

        if (!start.isValid()) {
            return;
        }

        this.patrolAnchorResolved = true;

        BlockPos center =
                start.getBoundingBox()
                        .getCenter();

        double radius =
                Math.sqrt(
                        this.distanceToSqrHorizontal(
                                center.getX() + 0.5D,
                                center.getZ() + 0.5D
                        )
                );

        if (radius < PATROL_MIN_RADIUS) {
            return;
        }

        this.patrolCenter =
                new BlockPos(
                        center.getX(),
                        this.blockPosition().getY(),
                        center.getZ()
                );

        this.patrolRadius = radius;

        this.patrolDirection =
                this.random.nextBoolean()
                        ? 1
                        : -1;
    }

    private double distanceToSqrHorizontal(
            double x,
            double z
    ) {
        double dx = this.getX() - x;
        double dz = this.getZ() - z;

        return dx * dx + dz * dz;
    }

    /**
     * Picks the next point along the ring, one step further round from
     * wherever the boss currently stands. Deriving the angle from the live
     * position means a boss dragged off the path by a fight walks back onto
     * it rather than resuming from a stale waypoint.
     */
    private Vec3 nextPatrolWaypoint() {
        double centerX = this.patrolCenter.getX() + 0.5D;
        double centerZ = this.patrolCenter.getZ() + 0.5D;

        double angle =
                Math.atan2(
                        this.getZ() - centerZ,
                        this.getX() - centerX
                )
                        + PATROL_STEP_RADIANS * this.patrolDirection;

        return new Vec3(
                centerX + Math.cos(angle) * this.patrolRadius,
                this.patrolCenter.getY(),
                centerZ + Math.sin(angle) * this.patrolRadius
        );
    }

    @Override
    public boolean removeWhenFarAway(
            double distanceToClosestPlayer
    ) {
        return false;
    }

    @Override
    public boolean causeFallDamage(
            double fallDistance,
            float damageMultiplier,
            DamageSource damageSource
    ) {
        if (this.getCombatState()
                .isJumpSlam()

                || this.isHealing()

                || this.scriptedJumpFallProtection) {

            boolean handled =
                    super.causeFallDamage(
                            0.0D,
                            damageMultiplier,
                            damageSource
                    );

            if (this.scriptedJumpFallProtection
                    && this.onGround()) {

                this.scriptedJumpFallProtection =
                        false;
            }

            return handled;
        }

        return super.causeFallDamage(
                fallDistance,
                damageMultiplier,
                damageSource
        );
    }

    @Override
    public void die(
            DamageSource damageSource
    ) {
        if (!this.level().isClientSide()
                && this.level()
                instanceof ServerLevel serverLevel
                && !this.finalizingDeath) {

            if (this.getDeathCinematicState()
                    == DeathCinematicState.ALIVE) {

                this.beginDeathCinematic(
                        serverLevel,
                        damageSource
                );

                return;
            }

            if (this.isDeathCinematicPlaying()) {
                return;
            }
        }

        if (!this.level().isClientSide()) {
            this.cleanupIntro(true);
            this.cleanupDeathCinematic(true);
        }

        super.die(damageSource);
    }

    @Override
    public void kill(
            ServerLevel serverLevel
    ) {
        this.cleanupIntro(true);
        this.cleanupDeathCinematic(true);

        this.finalizingDeath = true;

        this.setDeathCinematicState(
                DeathCinematicState.FINALIZED
        );

        this.entityData.set(
                DEATH_CINEMATIC_START_TIME,
                -1L
        );

        this.entityData.set(
                DEATH_CINEMATIC_TICKS,
                0
        );

        try {
            super.kill(serverLevel);
        } finally {
            this.finalizingDeath = false;
        }
    }

    @Override
    public void remove(
            Entity.RemovalReason reason
    ) {
        if (!this.level().isClientSide()) {
            this.cleanupIntro(true);
            this.cleanupDeathCinematic(true);
        }

        super.remove(reason);
    }

    @Override
    public void heal(
            float amount
    ) {
        if (!this.isDeathCinematicPlaying()) {
            super.heal(amount);
        }
    }

    @Override
    public void startSeenByPlayer(
            ServerPlayer player
    ) {
        super.startSeenByPlayer(
                player
        );

        this.bossEvent.addPlayer(
                player
        );

        if (this.isIntroPlaying()
                && StarCrawlerBossIntroManager.isClaimedBy(
                player.getUUID(),
                this.getUUID()
        )) {

            this.sendIntroStart(
                    player,
                    this.level().getGameTime()
            );
        }

        if (this.isDeathCinematicPlaying()
                && StarCrawlerBossDeathManager.isClaimedBy(
                player.getUUID(),
                this.getUUID()
        )) {

            this.sendDeathCinematicStart(
                    player,
                    this.level().getGameTime()
            );
        }
    }

    @Override
    public void stopSeenByPlayer(
            ServerPlayer player
    ) {
        super.stopSeenByPlayer(
                player
        );

        this.bossEvent.removePlayer(
                player
        );
    }

    @Override
    public void setCustomName(
            Component name
    ) {
        super.setCustomName(name);

        this.bossEvent.setName(
                Component.empty()
        );
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isIntroPlaying()
                || this.isDeathCinematicPlaying()
                ? null
                : SoundEvents.WARDEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(
            DamageSource source
    ) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.getHealth() <= 0.0F
                || this.getDeathCinematicState()
                != DeathCinematicState.ALIVE
                ? null
                : SoundEvents.WARDEN_DEATH;
    }

    @Override
    protected void addAdditionalSaveData(
            ValueOutput output
    ) {
        super.addAdditionalSaveData(
                output
        );

        DeathCinematicState deathState =
                this.getDeathCinematicState();

        output.putBoolean(
                "death_cinematic_pending",
                deathState == DeathCinematicState.DYING
        );

        output.putBoolean(
                "death_rewards_finalized",
                deathState == DeathCinematicState.FINALIZED
        );

        if (deathState == DeathCinematicState.DYING) {
            if (this.pendingDeathDamageType != null) {
                output.putString(
                        "death_damage_type",
                        this.pendingDeathDamageType
                );
            }

            if (this.pendingDeathKillerUuid != null) {
                output.putString(
                        "death_killer_uuid",
                        this.pendingDeathKillerUuid.toString()
                );
            }
        }

        output.putBoolean(
                "encounter_intro_complete",
                this.getIntroState()
                        != IntroState.NOT_STARTED
        );

        output.putInt(
                "combat_phase",
                this.getCombatPhase()
        );

        output.putBoolean(
                "first_heal_used",
                this.firstHealUsed
        );

        output.putBoolean(
                "second_heal_used",
                this.secondHealUsed
        );

        output.putBoolean(
                "final_minions_spawned",
                this.finalMinionsSpawned
        );

        output.putBoolean(
                "forced_final_smash",
                this.forcedFinalSmash
        );

        output.putBoolean(
                "pending_forced_ground_smash",
                this.pendingForcedGroundSmash
        );

        output.putBoolean(
                "scripted_jump_fall_protection",
                this.scriptedJumpFallProtection
        );

        output.putInt(
                "pending_forced_ground_smash_ticks",
                this.pendingForcedGroundSmashTicks
        );

        output.putInt(
                "attack_cooldown",
                this.attackCooldown
        );

        output.putInt(
                "last_attack",
                this.lastAttack == null
                        ? -1
                        : this.lastAttack.ordinal()
        );

        output.putInt(
                "combat_state",
                this.getCombatState().id
        );

        output.putInt(
                "state_elapsed",
                this.getCombatStateTicks()
        );

        output.putInt(
                "action_elapsed",
                this.getActionTicks()
        );

        output.putFloat(
                "healing_start_health",
                this.healingStartHealth
        );

        output.putFloat(
                "healing_target_health",
                this.healingTargetHealth
        );

        output.putFloat(
                "healing_start_energy",
                this.healingStartEnergy
        );

        output.putFloat(
                "healing_target_energy",
                this.healingTargetEnergy
        );

        output.putFloat(
                "healing_damage_sustained",
                this.healingDamageSustained
        );

        output.putBoolean(
                "patrol_anchor_resolved",
                this.patrolAnchorResolved
        );

        if (this.patrolCenter != null) {
            output.putInt(
                    "patrol_center_x",
                    this.patrolCenter.getX()
            );

            output.putInt(
                    "patrol_center_y",
                    this.patrolCenter.getY()
            );

            output.putInt(
                    "patrol_center_z",
                    this.patrolCenter.getZ()
            );

            output.putDouble(
                    "patrol_radius",
                    this.patrolRadius
            );

            output.putInt(
                    "patrol_direction",
                    this.patrolDirection
            );
        }
    }

    @Override
    protected void readAdditionalSaveData(
            ValueInput input
    ) {
        super.readAdditionalSaveData(
                input
        );

        boolean deathCinematicPending =
                input.getBooleanOr(
                        "death_cinematic_pending",
                        false
                );

        boolean deathRewardsFinalized =
                input.getBooleanOr(
                        "death_rewards_finalized",
                        false
                );

        this.pendingDeathDamageType =
                input.getString(
                                "death_damage_type"
                        )
                        .orElse(null);

        this.pendingDeathKillerUuid = null;

        input.getString(
                        "death_killer_uuid"
                )
                .ifPresent(serializedUuid -> {
                    try {
                        this.pendingDeathKillerUuid =
                                UUID.fromString(serializedUuid);
                    } catch (IllegalArgumentException ignored) {
                        this.pendingDeathKillerUuid = null;
                    }
                });

        this.pendingDeathSource = null;
        this.finalizeDeathAfterLoad = false;
        this.discardFinalizedDeathAfterLoad = false;

        this.entityData.set(
                DEATH_CINEMATIC_STATE,
                DeathCinematicState.ALIVE.id
        );

        this.entityData.set(
                DEATH_CINEMATIC_START_TIME,
                -1L
        );

        this.entityData.set(
                DEATH_CINEMATIC_TICKS,
                0
        );

        this.firstHealUsed =
                input.getBooleanOr(
                        "first_heal_used",
                        false
                );

        this.secondHealUsed =
                input.getBooleanOr(
                        "second_heal_used",
                        false
                );

        boolean introComplete =
                input.getBooleanOr(
                        "encounter_intro_complete",
                        this.firstHealUsed
                                || this.secondHealUsed
                                || this.getHealth()
                                < this.getMaxHealth()
                );

        this.entityData.set(
                INTRO_STATE,
                introComplete
                        ? IntroState.COMPLETE.id
                        : IntroState.NOT_STARTED.id
        );

        this.entityData.set(
                INTRO_START_TIME,
                -1L
        );

        this.finalMinionsSpawned =
                input.getBooleanOr(
                        "final_minions_spawned",
                        false
                );

        this.forcedFinalSmash =
                input.getBooleanOr(
                        "forced_final_smash",
                        false
                );

        this.pendingForcedGroundSmash =
                input.getBooleanOr(
                        "pending_forced_ground_smash",
                        false
                );

        this.scriptedJumpFallProtection =
                input.getBooleanOr(
                        "scripted_jump_fall_protection",
                        false
                );

        this.pendingForcedGroundSmashTicks =
                Mth.clamp(
                        input.getIntOr(
                                "pending_forced_ground_smash_ticks",
                                0
                        ),
                        0,
                        FORCED_SMASH_GROUNDING_TIMEOUT_TICKS
                );

        int phase =
                Mth.clamp(
                        input.getIntOr(
                                "combat_phase",
                                CombatPhase.PHASE_1.id
                        ),
                        CombatPhase.PHASE_1.id,
                        CombatPhase.PHASE_3.id
                );

        if (this.secondHealUsed) {
            phase =
                    Math.max(
                            phase,
                            CombatPhase.PHASE_2.id
                    );
        }

        if (!this.firstHealUsed) {
            phase =
                    CombatPhase.PHASE_1.id;
        }

        this.entityData.set(
                COMBAT_PHASE,
                phase
        );

        this.attackCooldown =
                Math.max(
                        0,
                        input.getIntOr(
                                "attack_cooldown",
                                this.randomAttackCooldown()
                        )
                );

        int attack =
                input.getIntOr(
                        "last_attack",
                        -1
                );

        this.lastAttack =
                attack >= 0
                        && attack
                        < AttackKind.values().length

                        ? AttackKind.values()[attack]
                        : null;

        this.healingStartHealth =
                input.getFloatOr(
                        "healing_start_health",
                        this.getHealth()
                );

        this.healingTargetHealth =
                input.getFloatOr(
                        "healing_target_health",
                        0.0F
                );

        this.healingStartEnergy =
                input.getFloatOr(
                        "healing_start_energy",
                        this.getCrystalEnergy()
                );

        this.healingTargetEnergy =
                input.getFloatOr(
                        "healing_target_energy",
                        0.0F
                );

        this.healingDamageSustained =
                Math.max(
                        0.0F,
                        input.getFloatOr(
                                "healing_damage_sustained",
                                0.0F
                        )
                );

        CombatState savedState =
                CombatState.byId(
                        input.getIntOr(
                                "combat_state",
                                CombatState.IDLE.id
                        )
                );


        if (savedState.isHealing()) {
            int stateElapsed =
                    Mth.clamp(
                            input.getIntOr(
                                    "state_elapsed",
                                    0
                            ),
                            0,
                            HEAL_DURATION_TICKS
                    );

            int actionElapsed =
                    Mth.clamp(
                            input.getIntOr(
                                    "action_elapsed",
                                    stateElapsed
                            ),
                            0,
                            HEAL_DURATION_TICKS
                    );

            this.entityData.set(
                    COMBAT_STATE,
                    savedState.id
            );

            this.entityData.set(
                    COMBAT_STATE_START_TIME,
                    this.level().getGameTime()
                            - stateElapsed
            );

            this.entityData.set(
                    ACTION_START_TIME,
                    this.level().getGameTime()
                            - actionElapsed
            );

            if (this.healingTargetHealth
                    <= 0.0F) {

                this.healingTargetHealth =
                        savedState
                                == CombatState.HEALING_PHASE_3

                                ? SECOND_HEAL_TARGET
                                : FIRST_HEAL_TARGET;
            }

            if (this.healingTargetEnergy
                    <= 0.0F) {

                this.healingTargetEnergy =
                        savedState
                                == CombatState.HEALING_PHASE_3

                                ? 1.0F
                                : 0.86F;
            }
        }

        else if (savedState.isGroundSmash()
                && this.forcedFinalSmash
                && this.secondHealUsed
                && phase
                >= CombatPhase.PHASE_3.id) {

            int actionElapsed =
                    Mth.clamp(
                            input.getIntOr(
                                    "action_elapsed",
                                    0
                            ),
                            0,
                            GROUND_SMASH_CLIP_END_TICK
                    );

            boolean impactAlreadyOccurred =
                    this.finalMinionsSpawned

                            || savedState
                            == CombatState.GROUND_SMASH_IMPACT

                            || savedState
                            == CombatState.GROUND_SMASH_RECOVERY

                            || actionElapsed
                            > GROUND_SMASH_IMPACT_TICK;

            CombatState restoredState =
                    impactAlreadyOccurred

                            ? CombatState.GROUND_SMASH_RECOVERY
                            : CombatState.GROUND_SMASH_WINDUP;

            int restoredElapsed =
                    impactAlreadyOccurred

                            ? Math.max(
                            GROUND_SMASH_IMPACT_TICK
                                    + 1,
                            actionElapsed
                    )

                            : actionElapsed;

            this.pendingForcedGroundSmash =
                    false;

            this.pendingForcedGroundSmashTicks =
                    0;

            this.entityData.set(
                    COMBAT_STATE,
                    restoredState.id
            );

            this.entityData.set(
                    COMBAT_STATE_START_TIME,
                    this.level().getGameTime()
            );

            this.entityData.set(
                    ACTION_START_TIME,
                    this.level().getGameTime()
                            - restoredElapsed
            );
        }

        else {
            if ((savedState.isJumpSlam()
                    || this.scriptedJumpFallProtection)

                    && !this.onGround()) {

                this.scriptedJumpFallProtection =
                        true;

                this.resetFallDistance();

                Vec3 velocity =
                        this.getDeltaMovement();

                this.setDeltaMovement(
                        0.0D,
                        Math.min(
                                velocity.y,
                                -0.35D
                        ),
                        0.0D
                );
            }

            this.entityData.set(
                    COMBAT_STATE,
                    CombatState.IDLE.id
            );

            this.entityData.set(
                    COMBAT_STATE_START_TIME,
                    this.level().getGameTime()
            );

            this.entityData.set(
                    ACTION_START_TIME,
                    -1L
            );

            this.pendingForcedGroundSmash =
                    this.pendingForcedGroundSmash

                            || (
                            this.secondHealUsed

                                    && phase
                                    >= CombatPhase.PHASE_3.id

                                    && !this.finalMinionsSpawned
                    );
        }

        if (deathRewardsFinalized) {
            this.setDeathCinematicState(
                    DeathCinematicState.FINALIZED
            );

            this.entityData.set(
                    DEATH_CINEMATIC_START_TIME,
                    -1L
            );

            this.entityData.set(
                    DEATH_CINEMATIC_TICKS,
                    DEATH_CINEMATIC_DURATION_TICKS
            );

            this.setHealth(0.0F);
            this.discardFinalizedDeathAfterLoad = true;
        }

        else if (deathCinematicPending) {

            this.setHealth(1.0F);

            this.setDeathCinematicState(
                    DeathCinematicState.DYING
            );

            this.entityData.set(
                    DEATH_CINEMATIC_START_TIME,
                    this.level().getGameTime()
                            - DEATH_CINEMATIC_DURATION_TICKS
            );

            this.entityData.set(
                    DEATH_CINEMATIC_TICKS,
                    DEATH_CINEMATIC_DURATION_TICKS
            );

            this.entityData.set(
                    COMBAT_STATE,
                    CombatState.IDLE.id
            );

            this.entityData.set(
                    COMBAT_STATE_START_TIME,
                    this.level().getGameTime()
            );

            this.entityData.set(
                    ACTION_START_TIME,
                    -1L
            );

            this.pendingForcedGroundSmash = false;
            this.pendingForcedGroundSmashTicks = 0;
            this.scriptedJumpFallProtection = false;
            this.finalizeDeathAfterLoad = true;
        }

        this.patrolAnchorResolved =
                input.getBooleanOr(
                        "patrol_anchor_resolved",
                        false
                );

        this.patrolCenter = null;
        this.patrolRadius = 0.0D;

        double savedRadius =
                input.getDoubleOr(
                        "patrol_radius",
                        0.0D
                );

        if (savedRadius >= PATROL_MIN_RADIUS) {
            this.patrolCenter =
                    new BlockPos(
                            input.getIntOr(
                                    "patrol_center_x",
                                    0
                            ),
                            input.getIntOr(
                                    "patrol_center_y",
                                    0
                            ),
                            input.getIntOr(
                                    "patrol_center_z",
                                    0
                            )
                    );

            this.patrolRadius = savedRadius;

            this.patrolDirection =
                    input.getIntOr(
                            "patrol_direction",
                            1
                    ) < 0
                            ? -1
                            : 1;
        }

        this.bossEvent.setName(
                Component.empty()
        );

        this.updateCrystalEnergy();

        this.updateBossPresentation();
    }

    private final class RingPatrolGoal
            extends Goal {

        private int waypointTicks;

        private RingPatrolGoal() {
            this.setFlags(
                    EnumSet.of(
                            Flag.MOVE
                    )
            );
        }

        @Override
        public boolean canUse() {
            return StarCrawlerBossEntity.this
                    .patrolCenter != null

                    && StarCrawlerBossEntity.this
                    .getTarget() == null

                    && !StarCrawlerBossEntity.this
                    .isIntroPlaying()

                    && !StarCrawlerBossEntity.this
                    .isDeathCinematicPlaying();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            this.moveToNextWaypoint();
        }

        @Override
        public void stop() {
            this.waypointTicks = 0;

            StarCrawlerBossEntity.this
                    .getNavigation()
                    .stop();
        }

        @Override
        public void tick() {
            this.waypointTicks++;

            boolean stuck =
                    this.waypointTicks
                            > PATROL_MAX_WAYPOINT_TICKS;

            if (stuck
                    || StarCrawlerBossEntity.this
                    .getNavigation()
                    .isDone()) {

                if (stuck) {
                    StarCrawlerBossEntity.this
                            .patrolDirection *= -1;
                }

                this.moveToNextWaypoint();
            }
        }

        private void moveToNextWaypoint() {
            this.waypointTicks = 0;

            Vec3 waypoint =
                    StarCrawlerBossEntity.this
                            .nextPatrolWaypoint();

            StarCrawlerBossEntity.this
                    .getNavigation()
                    .moveTo(
                            waypoint.x,
                            waypoint.y,
                            waypoint.z,
                            PATROL_SPEED
                    );
        }
    }

    private final class IntroFreezeGoal
            extends Goal {

        private IntroFreezeGoal() {
            this.setFlags(
                    EnumSet.of(
                            Flag.MOVE,
                            Flag.LOOK,
                            Flag.JUMP
                    )
            );
        }

        @Override
        public boolean canUse() {
            return StarCrawlerBossEntity.this
                    .isIntroPlaying()

                    || StarCrawlerBossEntity.this
                    .isDeathCinematicPlaying();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            StarCrawlerBossEntity.this
                    .holdPosition();
        }

        @Override
        public void tick() {
            StarCrawlerBossEntity.this
                    .holdPosition();
        }
    }

    private final class BossCombatGoal
            extends Goal {

        private BossCombatGoal() {
            this.setFlags(
                    EnumSet.of(
                            Flag.MOVE,
                            Flag.LOOK
                    )
            );
        }

        @Override
        public boolean canUse() {
            return StarCrawlerBossEntity.this
                    .isIntroComplete()

                    && StarCrawlerBossEntity.this
                    .getDeathCinematicState()
                    == DeathCinematicState.ALIVE

                    && StarCrawlerBossEntity.this
                    .isValidAttackTarget(
                            StarCrawlerBossEntity.this
                                    .getTarget()
                    );
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void stop() {
            if (StarCrawlerBossEntity.this
                    .isNeutralState()) {

                StarCrawlerBossEntity.this
                        .getNavigation()
                        .stop();

                StarCrawlerBossEntity.this
                        .setCombatState(
                                CombatState.IDLE
                        );
            }
        }

        @Override
        public void tick() {
            LivingEntity target =
                    StarCrawlerBossEntity.this
                            .getTarget();

            if (!StarCrawlerBossEntity.this
                    .isValidAttackTarget(
                            target
                    )) {

                return;
            }

            if (!StarCrawlerBossEntity.this
                    .isNeutralState()) {

                StarCrawlerBossEntity.this
                        .getNavigation()
                        .stop();

                return;
            }

            StarCrawlerBossEntity.this
                    .setCombatState(
                            CombatState.CHASING
                    );

            StarCrawlerBossEntity.this
                    .getLookControl()
                    .setLookAt(
                            target,
                            30.0F,
                            30.0F
                    );

            if (StarCrawlerBossEntity.this
                    .getCombatState()
                    == CombatState.CHASING) {

                StarCrawlerBossEntity.this
                        .getNavigation()
                        .moveTo(
                                target,
                                PATH_SPEED
                        );
            }

            StarCrawlerBossEntity.this
                    .tryBeginContextualAttack(
                            target
                    );
        }
    }

    public enum IntroState {
        NOT_STARTED(0),
        PLAYING(1),
        COMPLETE(2);

        private static final IntroState[] VALUES =
                values();

        private final int id;

        IntroState(int id) {
            this.id =
                    id;
        }

        public static IntroState byId(
                int id
        ) {
            return VALUES[
                    Mth.clamp(
                            id,
                            0,
                            VALUES.length - 1
                    )
                    ];
        }
    }

    public enum DeathCinematicState {
        ALIVE(0),
        DYING(1),
        FINALIZED(2);

        private static final DeathCinematicState[] VALUES =
                values();

        private final int id;

        DeathCinematicState(int id) {
            this.id = id;
        }

        public static DeathCinematicState byId(
                int id
        ) {
            return VALUES[
                    Mth.clamp(
                            id,
                            0,
                            VALUES.length - 1
                    )
                    ];
        }
    }

    private enum AttackKind {
        CHARGE,
        JUMP_SLAM,
        GROUND_SMASH
    }

    public enum CombatPhase {
        PHASE_1(1),
        PHASE_2(2),
        PHASE_3(3);

        private static final CombatPhase[] VALUES =
                values();

        private final int id;

        CombatPhase(int id) {
            this.id =
                    id;
        }

        public static CombatPhase byId(
                int id
        ) {
            return VALUES[
                    Mth.clamp(
                            id - 1,
                            0,
                            VALUES.length - 1
                    )
                    ];
        }

        public boolean isAtLeast(
                CombatPhase phase
        ) {
            return this.id
                    >= phase.id;
        }
    }

    public enum CombatState {
        IDLE(0),

        CHASING(1),

        CHARGE_WINDUP(2),
        CHARGING(3),
        CHARGE_RECOVERY(4),

        JUMP_SLAM_WINDUP(5),
        JUMP_SLAM_AIRBORNE(6),
        JUMP_SLAM_IMPACT(7),
        JUMP_SLAM_RECOVERY(8),

        GROUND_SMASH_WINDUP(9),
        GROUND_SMASH_IMPACT(10),
        GROUND_SMASH_RECOVERY(11),

        HEALING_PHASE_2(12),
        HEALING_PHASE_3(13);

        private static final CombatState[] VALUES =
                values();

        private final int id;

        CombatState(int id) {
            this.id =
                    id;
        }

        public static CombatState byId(
                int id
        ) {
            return VALUES[
                    Mth.clamp(
                            id,
                            0,
                            VALUES.length - 1
                    )
                    ];
        }

        public boolean isCharge() {
            return this
                    == CHARGE_WINDUP

                    || this
                    == CHARGING

                    || this
                    == CHARGE_RECOVERY;
        }

        public boolean isJumpSlam() {
            return this
                    == JUMP_SLAM_WINDUP

                    || this
                    == JUMP_SLAM_AIRBORNE

                    || this
                    == JUMP_SLAM_IMPACT

                    || this
                    == JUMP_SLAM_RECOVERY;
        }

        public boolean isGroundSmash() {
            return this
                    == GROUND_SMASH_WINDUP

                    || this
                    == GROUND_SMASH_IMPACT

                    || this
                    == GROUND_SMASH_RECOVERY;
        }

        public boolean isHealing() {
            return this
                    == HEALING_PHASE_2

                    || this
                    == HEALING_PHASE_3;
        }
    }
}
