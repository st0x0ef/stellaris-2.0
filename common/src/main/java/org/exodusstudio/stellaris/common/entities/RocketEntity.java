package org.exodusstudio.stellaris.common.entities;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.module.Modules;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.module.rocket.RocketModules;
import org.exodusstudio.stellaris.common.network.packets.OpenRocketMenuPacket;
import org.exodusstudio.stellaris.common.network.packets.SyncRocketModule;
import org.exodusstudio.stellaris.common.registries.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;


public class RocketEntity extends VehicleEntity  {

    public static final EntityDataAccessor<Modules<RocketModule>> ROCKET_MODULES = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializersRegistry.ROCKET_MODULES );

    public static RocketEntity fromItemStack (Level level, ItemStack stack) {
        RocketEntity rocketEntity = new RocketEntity(EntityTypesRegistry.ROCKET.get(), level);
        Modules<RocketModule> modulesOptional = stack.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty());
        rocketEntity.setRocketModules(modulesOptional);

        //TODO: don't allow module fuel if the rocket has already fuel in it
        //Only allow to change the fuel type when the rocket is empty
        if(stack.has(DataComponentsRegistry.FLUID_LIST.get())) {
            FluidAmountMapDataComponent fluidData = stack.get(DataComponentsRegistry.FLUID_LIST.get());

            rocketEntity.entityData.set(FUEL, (int) fluidData.getAmount(0));
        }
        return rocketEntity;
    }

    public RocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public void setRocketModules(Modules<RocketModule> modules) {
        this.entityData.set(ROCKET_MODULES, modules);
    }

    public Modules<RocketModule> getRocketModules() {
        return this.entityData.get(ROCKET_MODULES);
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


        FluidStack fuel = this.getFuelType();
        rocketStack.set(DataComponentsRegistry.FLUID_LIST.get(),
                new FluidAmountMapDataComponent(List.of(fuel.getFluid()), List.of(fuel.getAmount())));

        ItemEntity entityToSpawn = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), rocketStack);

        entityToSpawn.setPickUpDelay(10);

        this.level().addFreshEntity(entityToSpawn);
    }

    /**
     * Gets the fuel type of the rocket, considering any custom fuel modules.
     * @return The FluidStack representing the rocket's fuel type.
     */
    public FluidStack getFuelType() {
        FluidStack fuel = FluidStack.create(FluidsRegistry.HYDROGEN_STILL.get(), this.getFuel());
        for (RocketModule module : this.getRocketModules()) {
            if (module instanceof RocketModule.CustomFuelModule customFuelModule) {
                fuel = customFuelModule.getFuel();
            }
        }
        return fuel;
    }

    /**
     * Container logic to fill up the rocket's fuel tank using fuel items from its inventory.
     * @return true if the rocket was successfully filled, false otherwise.
     */
    @SuppressWarnings(value = "all")
    public boolean tryFillUpRocket() {
        ItemStack item = this.getInventory().getItem(0);

        int fuelLevel = getFuelLevel();
        int tankCapacity = getTankCapacity();
        FluidStack fuelType = getFuelType();


        if (this.level().isClientSide) {
            return false;
        }

        if (fuelLevel >= tankCapacity || item == null) {
            return false;
        }



        if (item.getItem() instanceof BucketItem bucketItem) {

            Fluid fluid = bucketItem.arch$getFluid();

            // Check if the fluid from the bucket matches the rocket's fuel type or if the rocket has no specific fuel type set
            if(fluid == null || (!fluid.isSame(fuelType.getFluid()) && !fuelType.isEmpty())) {
                return false;
            }

            this.entityData.set(FUEL, fuelLevel + 1000);
            if (getFuelLevel() > tankCapacity) {
                this.entityData.set(FUEL, tankCapacity);
            }

            inventory.removeItem(0, 1);

            inventory.setItem(1, new ItemStack(Items.BUCKET, inventory.getItem(1).getCount() + 1));

            return true;
        }
        return false;
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

        tryFillUpRocket();
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
        NetworkManager.sendToServer(
                new OpenRocketMenuPacket(this.getId()));
    }

    /**
     * Gets the tank capacity of the rocket, considering any tank upgrade modules.
     * @return The tank capacity in units.
     */
    public int getTankCapacity() {
        return 3000;
    }

    public int getFuelLevel() {
        return this.entityData.get(FUEL);
    }

}
