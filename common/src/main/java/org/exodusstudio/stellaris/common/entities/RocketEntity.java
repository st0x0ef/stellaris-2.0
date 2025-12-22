package org.exodusstudio.stellaris.common.entities;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.menus.RocketMenu;
import org.exodusstudio.stellaris.common.module.Modules;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.module.rocket.RocketModules;
import org.exodusstudio.stellaris.common.network.packets.SyncRocketModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.EntityDataSerializersRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class RocketEntity extends VehicleEntity  {

    public static final EntityDataAccessor<Modules<RocketModule>> ROCKET_MODULES = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializersRegistry.ROCKET_MODULES );

    public static RocketEntity fromItemStack (Level level, ItemStack stack) {
        RocketEntity rocketEntity = new RocketEntity(EntityTypesRegistry.ROCKET.get(), level);
        Modules<RocketModule> modulesOptional = stack.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty());
        rocketEntity.setRocketModules(modulesOptional);
        return rocketEntity;
    }

    public RocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public void setRocketModules(Modules<RocketModule> modules) {
        this.entityData.set(ROCKET_MODULES, modules);
    }

    /**
     * Drops all equipment stored in the rocket's inventory when destroyed.
     * @param level The server level where the rocket is located.
     */
    protected void dropEquipment(ServerLevel level) {
        for (int i = 0; i < this.inventory.getItems().size(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                this.spawnAtLocation(level, itemstack);
            }
        }
    }

    /**
     * Spawns the rocket item with its modules saved when the rocket entity is destroyed.
     */
    protected void spawnRocketItem() {
        ItemStack rocketStack = new ItemStack(ItemsRegistry.ROCKET.get(), 1);
        rocketStack.set(DataComponentsRegistry.ROCKET_MODULES.get(), this.entityData.get(ROCKET_MODULES));
        ItemEntity entityToSpawn = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), rocketStack);
        entityToSpawn.setPickUpDelay(10);

        this.level().addFreshEntity(entityToSpawn);
    }


    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROCKET_MODULES, RocketModules.empty());
    }


    @Override
    public void tick() {
        if(this.level().isClientSide ) {
            return;
        }
        NetworkManager.sendToPlayers(level().getServer().getPlayerList().getPlayers(),
                new SyncRocketModule(this.getId(), this.entityData.get(ROCKET_MODULES)));

        super.tick();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        Entity sourceEntity = damageSource.getEntity();

        if (sourceEntity != null && sourceEntity.isCrouching() && !this.isVehicle()) {
            this.spawnRocketItem();
            this.dropEquipment(level);

            if (!this.level().isClientSide) {
                this.remove(RemovalReason.DISCARDED);
            }

            return true;
        }

        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    public @NotNull Vec3 getPassengerRidingPosition(Entity entity) {
        return super.getPassengerRidingPosition(entity).subtract(0, 3f, 0);
    }

    @Override
    public Pose getRiderPose() {
        return Pose.STANDING;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.store("rocket_modules", RocketModules.CODEC, this.entityData.get(ROCKET_MODULES));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        Optional<Modules<RocketModule>>  modules = input.read("rocket_modules", RocketModules.CODEC);
        modules.ifPresent(this::setRocketModules);
    }

    @Override
    public void kill(ServerLevel level) {
        super.kill(level);
        this.spawnRocketItem();
        this.dropEquipment(level);
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, new ExtendedMenuProvider() {
                @Override
                public void saveExtraData(FriendlyByteBuf buf) {
                    buf.writeInt(RocketEntity.this.getId());
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("entity.stellaris.rocket");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                    packetBuffer.writeVarInt(RocketEntity.this.getId());
                    return new RocketMenu(syncId, inv, inventory, RocketEntity.this.getId());
                }
            });
        }

    }

}
