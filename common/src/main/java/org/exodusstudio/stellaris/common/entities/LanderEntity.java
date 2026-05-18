package org.exodusstudio.stellaris.common.entities;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.keybinds.KeyVariables;
import org.exodusstudio.stellaris.common.menus.LanderMenu;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.registries.EntityDataSerializersRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LanderEntity extends VehicleEntity {
    public static final EntityDataAccessor<Boolean> AUTOPILOT = SynchedEntityData.defineId(LanderEntity.class, EntityDataSerializers.BOOLEAN);

    public LanderEntity(Level level, boolean autopilot) {
        this(EntityTypesRegistry.LANDER.get(), level);
        this.entityData.set(AUTOPILOT, autopilot);
    }

    public LanderEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {
        return this.position().add(this.getPassengerAttachmentPoint(entity, getDimensions(this.getPose()), 1.0F)).add(0d, -0.5d, 0d);
    }


    @Override
    public void kill(ServerLevel level) {
        this.dropEquipment(level);

        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (fallDistance > 5.0F) {
            if (!this.level().isClientSide()) {
                if (!this.entityData.get(AUTOPILOT) && Stellaris.CONFIG.vehicleConfig.shouldLanderExplode) {
                    this.level().explode(this, this.getX(), this.getY(), this.getZ(), 10, true,
                            Level.ExplosionInteraction.TNT);
                    this.remove(RemovalReason.DISCARDED);
                }

            }
        }

        return super.causeFallDamage(fallDistance, damageMultiplier, damageSource);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (damageSource.getEntity() != null && damageSource.getEntity().isCrouching()
                && !this.isVehicle()) {
            this.dropEquipment(level);

            this.remove(RemovalReason.DISCARDED);
            return true;
        }

        return false;
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand) {
        super.interact(player, hand);
        InteractionResult result = level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;

        if (!this.level().isClientSide()) {
            if (player.isCrouching()) {
                this.openCustomInventoryScreen(player);

                return InteractionResult.CONSUME;
            }

            player.startRiding(this);
            return InteractionResult.CONSUME;
        }

        return result;
    }

    @Override
    public void tick() {
        super.tick();

        if(getBlockStateOn().isSolid()) {
            this.setDeltaMovement(new Vec3(0.0D, 0.0D, 0.0D));
            return;
        }

        if (this.getDeltaMovement().y < this.getMaxLanderSpeed() - 0.1) {
            this.addDeltaMovement(new Vec3(0, -0.1, 0));
        } else {
            this.setDeltaMovement(new Vec3(0, this.getMaxLanderSpeed(), 0));
        }

        if (KeyVariables.isHoldingJump(getFirstPlayerPassenger()) || this.entityData.get(AUTOPILOT)) {
            slowDownLander();
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

    }




    public Player getFirstPlayerPassenger() {
        if (!this.getPassengers().isEmpty() && this.getPassengers().getFirst() instanceof Player player) {
            return player;
        }
        return null;
    }


    public void slowDownLander() {
        Vec3 vec = this.getDeltaMovement();

        if (!this.onGround()) {
            if (vec.y() < -0.05) {
                this.setDeltaMovement(vec.x(), vec.y() * 0.75, vec.z());
            }

            this.fallDistance = (float) (vec.y() * (-1) * 4.5);

            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.SPIT, false,true, this.getX(),
                        this.getY() - 0.3, this.getZ(), 3, 0.1, 0.1, 0.1, 0.001);
            }
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.3D;
    }

    public void fillInventoryFromRocket(RocketEntity rocketEntity) {

        for(int i = 0; i < 14; i++) {
            this.inventory.setItem(i, rocketEntity.getInventory().getItem(i));
        }
        this.inventory.setItem(14, rocketEntity.toItemStack());

    }



    @Override
    public void openCustomInventoryScreen(Player player) {
        MenuRegistry.openExtendedMenu((ServerPlayer) player, new ExtendedMenuProvider() {

            @Override
            public void saveExtraData(FriendlyByteBuf buf) {

            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.entity." + Stellaris.MOD_ID + ".lander");
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
                return new LanderMenu(id, playerInv, inventory);
            }
        });
    }


    public double getMaxLanderSpeed() {
        return 0.7;
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AUTOPILOT, false);
    }
}
