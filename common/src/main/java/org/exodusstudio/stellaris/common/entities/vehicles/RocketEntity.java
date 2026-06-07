package org.exodusstudio.stellaris.common.entities.vehicles;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.entities.vehicles.base.VehicleEntity;
import org.exodusstudio.stellaris.common.menus.PlanetSelectionMenu;
import org.exodusstudio.stellaris.common.menus.RocketMenu;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.network.packets.SyncRocketPacket;
import org.exodusstudio.stellaris.common.registries.*;
import org.exodusstudio.stellaris.common.utils.InventorySaver;
import org.exodusstudio.stellaris.common.utils.TeleportUtil;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class RocketEntity extends VehicleEntity {

    public static final EntityDataAccessor<Modules<RocketModule>> ROCKET_MODULES = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializersRegistry.ROCKET_MODULES);
    public static final EntityDataAccessor<Boolean> ROCKET_START = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ROCKET_START_TIMER = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Planet> AUTOPILOT_DESTINATION = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializersRegistry.PLANET);

    public RocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level, 29);
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

        ItemStack rocketStack = this.toItemStack();

        ItemEntity entityToSpawn = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), rocketStack);

        entityToSpawn.setPickUpDelay(10);

        this.level().addFreshEntity(entityToSpawn);
    }

    /**
     * Gets the fuel type of the rocket, considering any custom fuel modules.
     * @return The FluidStack representing the rocket's fuel type.
     */
    public FluidStack getFuelType() {
        FluidStack fuel = FluidStack.create(FluidsRegistry.FUEL_STILL.get(), this.getFuel());
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


        if (this.level().isClientSide()) {
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


    public void spawnParticle() {
        if (this.level() instanceof ServerLevel level) {
            Vec3 vec = this.getDeltaMovement();

            if (this.isTimerOver()) {
                level.sendParticles((ParticleOptions) ParticleTypes.FLAME, this.getX() - vec.x, this.getY() - vec.y - 2.2, this.getZ() - vec.z, 20, 0.1, 0.1, 0.1, 0.001);
                level.sendParticles((ParticleOptions) ParticleTypes.FLAME, this.getX() - vec.x, this.getY() - vec.y - 3.2, this.getZ() - vec.z, 10, 0.1, 0.1, 0.1, 0.04);
            } else {
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - vec.x, this.getY() - vec.y - 0.1, this.getZ() - vec.z, 6, 0.1, 0.1, 0.1, 0.023);
            }
        }
    }

    public void startTimerAndFlyMovement() {
        if (this.getTimer() < 200 && !this.level().isClientSide()) {
            this.entityData.set(ROCKET_START_TIMER, this.getTimer() + 1);
        }


        //To stop the rocket from going in the outer rims
        if(this.isNoGravity()) return;

        if (this.getTimer() == 200) {
            if (this.getDeltaMovement().y < this.getRocketSpeed() - 0.1) {
                this.addDeltaMovement(new Vec3(0, 0.1, 0));
            } else if (this.getDeltaMovement().y > this.getRocketSpeed() + 0.1) {
                Stellaris.LOG.info("Rocket speed: " + this.getDeltaMovement().y);
                this.level().playSeededSound(null, this, SoundRegistry.BOOST_SOUND, SoundSource.NEUTRAL, 1, 1, 1);
                this.setDeltaMovement(new Vec3(0, this.getRocketSpeed(), 0));
            } else {
                this.setDeltaMovement(new Vec3(0, this.getRocketSpeed(), 0));

            }

            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    public void startRocket() {
        if (!canFly()) {
            if (!this.getPassengers().isEmpty() && this.getPassengers().getFirst() instanceof Player player) {
                player.displayClientMessage(Component.literal("There's something blocking the rocket flying path..."), true);
            }

            return;
        }

        Entity entity = this.getPassengers().getFirst();

        if (entity instanceof Player player) {
            if (this.getFuel() > 0 || player.isCreative()) {
                if (!this.entityData.get(ROCKET_START)) {
                    this.entityData.set(ROCKET_START, true);
                    player.awardStat(StatsRegistry.ROCKET_LAUNCHED.get());
                    this.level().playSeededSound(player,this, SoundRegistry.ROCKET_SOUND, SoundSource.NEUTRAL, 1, 1, 1);
                }
            } else {
                player.displayClientMessage(Component.translatable("text.stellaris.rocket.fuel", getFuelType().getFluid().arch$registryName()), true);
            }
        }
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROCKET_MODULES, RocketModules.empty());
        builder.define(ROCKET_START, false);
        builder.define(ROCKET_START_TIMER, 0);
        builder.define(AUTOPILOT_DESTINATION, Objects.requireNonNull(PlanetsData.getPlanet(Level.OVERWORLD))); // default value, shouldn't be used
    }

    @Override
    public void tick() {
        super.tick();

        boolean shouldOpenPlanetMenu = true; // to make sure we don't open the menu if the player is teleporting to the planet with the autopilot

        if (!this.level().isClientSide()) {
            MinecraftServer server = level().getServer();

            tryFillUpRocket();

            if (server != null) {
                NetworkManager.sendToPlayers(server.getPlayerList().getPlayers(),
                        new SyncRocketPacket(this.getId(), this.entityData.get(ROCKET_MODULES), InventorySaver.fromContainer(getInventory())));

                if (this.getRocketModules().contains(ModulesRegistry.AUTOPILOT.get()) && this.getY() >= Stellaris.CONFIG.vehicleConfig.rocketTpHeight) {
                    Entity passenger = null;
                    if (!this.getPassengers().isEmpty()) {
                        passenger = this.getPassengers().getFirst();
                    }
                    TeleportUtil.teleportRocketToPlanet(passenger, server.getLevel(ResourceKey.create(Registries.DIMENSION, this.entityData.get(AUTOPILOT_DESTINATION).dimension())), this, this.blockPosition(), true); // TODO : allow to tp to space station and antenna
                    shouldOpenPlanetMenu = false;
                }
            }
        }

        //Handle rocket movement when started
        if (this.entityData.get(ROCKET_START)) {
            this.spawnParticle();
            this.startTimerAndFlyMovement();
        }

        if(!this.getPassengers().isEmpty() && shouldOpenPlanetMenu) {
            Entity passenger = this.getPassengers().getFirst();

            if(passenger instanceof Player player && this.getY() >= Stellaris.CONFIG.vehicleConfig.rocketTpHeight) {
                openPlanetSelectionScreen(player);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        Entity sourceEntity = damageSource.getEntity();

        if (sourceEntity != null && sourceEntity.isCrouching() && !this.isVehicle()) {
            if (!this.level().isClientSide()) {
                this.spawnRocketItem();
                this.dropEquipment(level);
                this.remove(RemovalReason.DISCARDED);
            }

            return true;
        }

        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    public @NotNull Vec3 getPassengerRidingPosition(Entity entity) {
        float yOffset = 3.75f;
        for (RocketModule rocketModule : this.getRocketModules()) {
            if (rocketModule instanceof RocketModule.CustomModelModule modelUpgrade) {
                yOffset = modelUpgrade.getPlayerYOffset();
            }
        }

        return super.getPassengerRidingPosition(entity).subtract(0f, yOffset, 0f);
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
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        MenuRegistry.openExtendedMenu((ServerPlayer) player, new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeUUID(getUUID());
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("entity.stellaris.rocket");
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
                return new RocketMenu(syncId, playerInv, inventory, getRocketEntity(), getInventoryRows());
            }
        });
    }

    public void openPlanetSelectionScreen(Player player) {

        if(!player.stellaris$isPlanetMenuOpen()) {
            player.stellaris$setPlanetMenuOpen(true, player, true);
            if(player instanceof ServerPlayer serverPlayer) {

                Utils.executeWithFade(player, () -> {
                    MenuRegistry.openExtendedMenu(serverPlayer, PlanetSelectionMenu.createProvider(serverPlayer.level().getServer()));
                    this.setNoGravity(true);
                }, true);
            }
        }
    }

    private boolean canFly() {
        // check if there's enough space for the rocket to fly to the sky
        BlockPos pos = this.blockPosition();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = pos.getY(); y < level().getMaxY(); y++) {
                    BlockPos checkPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z);
                    if (!level().isEmptyBlock(checkPos)) {
                        return false;
                    }
                }
            }
        }

        return true;
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

    public double getRocketSpeed() {
        return 0.8;
    }

    public boolean isTimerOver() {
        return this.entityData.get(ROCKET_START_TIMER) == 200;
    }

    public int getTimer() {
        return this.entityData.get(ROCKET_START_TIMER);
    }

    public int getInventoryRows() {
        return getRocketModules().contains(ModulesRegistry.CARGO.get()) ? 3 : 1;
    }


    public static RocketEntity fromItemStack(Level level, ItemStack stack) {
        RocketEntity rocketEntity = new RocketEntity(EntityTypesRegistry.ROCKET.get(), level);
        Modules<RocketModule> modulesOptional = stack.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty());
        rocketEntity.setRocketModules(modulesOptional);

        if (stack.has(DataComponentsRegistry.AUTOPILOT.get())) {
            rocketEntity.entityData.set(AUTOPILOT_DESTINATION, Objects.requireNonNull(stack.get(DataComponentsRegistry.AUTOPILOT.get())));
        }

        //Only allow to change the fuel type when the rocket is not empty
        if(stack.has(DataComponentsRegistry.FLUID_LIST.get())) {
            FluidAmountMapDataComponent fluidData = stack.get(DataComponentsRegistry.FLUID_LIST.get());

            rocketEntity.entityData.set(FUEL, (int) fluidData.getAmount(0));
        }
        return rocketEntity;
    }

    public ItemStack toItemStack() {
        ItemStack rocketStack = new ItemStack(ItemsRegistry.ROCKET.get(), 1);
        rocketStack.set(DataComponentsRegistry.ROCKET_MODULES.get(), this.entityData.get(ROCKET_MODULES));
        rocketStack.set(DataComponentsRegistry.AUTOPILOT.get(), this.entityData.get(AUTOPILOT_DESTINATION));

        FluidStack fuel = this.getFuelType();
        rocketStack.set(DataComponentsRegistry.FLUID_LIST.get(),
                new FluidAmountMapDataComponent(List.of(fuel.getFluid()), List.of(fuel.getAmount())));

        return rocketStack;
    }

    public RocketEntity getRocketEntity() {
        return this;
    }
}
