package org.exodusstudio.stellaris.common.entities.vehicles;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.vehicles.base.AbstractRoverBase;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.VehicleFuelStorage;
import org.exodusstudio.stellaris.common.menus.RoverMenu;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rover.RoverModule;
import org.exodusstudio.stellaris.common.modules.rover.RoverModules;
import org.exodusstudio.stellaris.common.networking.packets.SyncRoverDataPacket;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.EntityDataSerializersRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.ModulesRegistry;
import org.exodusstudio.stellaris.common.utils.InventorySaver;
import org.exodusstudio.stellaris.common.vehicle_upgrade.FuelType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Optional;

public class RoverEntity extends AbstractRoverBase implements HasCustomInventoryScreen, ContainerListener, FluidProvider.ENTITY {

    public static final EntityDataAccessor<Modules<RoverModule>> ROVER_MODULES = SynchedEntityData.defineId(RoverEntity.class, EntityDataSerializersRegistry.ROVER_MODULES);

    /** Maximum inventory rows (1 base + cargo module rows). Governs the backing container size. */
    public static final int MAX_INVENTORY_ROWS = 3;

    private static final int BASE_TANK_CAPACITY = 3000;
    private static final int REFUEL_AMOUNT = 1000;

    /**
     * Fuel burned each time the {@code distanceBetweenFuelConsumption} interval (20 blocks) is crossed while driving.
     * At 2 units / 20 blocks a full {@link #BASE_TANK_CAPACITY} tank lasts ~30,000 blocks (~10,000 per bucket).
     */
    private static final int FUEL_CONSUMPTION_PER_INTERVAL = 2;

    public final SimpleContainer inventory;

    /**
     * Exposes the rover's integer fuel as a fluid tank so it can reuse the machine slot logic. The
     * accepted fuel is gated by the motor's fuel type, and the tank's {@link #FUEL_TYPE} is set from
     * the inserted fluid when filling from empty (mirroring {@link #tryFillUpRover}).
     */
    private final VehicleFuelStorage fuelTank = new VehicleFuelStorage() {
        @Override
        public long getFuelAmount() {
            return RoverEntity.this.FUEL;
        }

        @Override
        public void setFuelAmount(long amount) {
            RoverEntity.this.FUEL = (int) amount;
        }

        @Override
        public long getCapacity() {
            return RoverEntity.this.getTankCapacity();
        }

        @Override
        public Fluid getFuelFluid() {
            return FuelType.Type.getFluidBasedOnType(FUEL_TYPE);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            FuelType.Type type = FuelType.Type.getTypeBasedOnFluid(stack.getFluid());
            if (type == null || getMotorFuelType() != type.getMotorType()) {
                return false;
            }
            return FUEL <= 0 || type == FUEL_TYPE;
        }

        @Override
        protected void onFill(FluidStack inserted) {
            FuelType.Type type = FuelType.Type.getTypeBasedOnFluid(inserted.getFluid());
            if (type != null) {
                FUEL_TYPE = type;
            }
        }
    };

    public RoverEntity(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.inventory = new SimpleContainer(2 + 9 * MAX_INVENTORY_ROWS);
        this.FUEL = 0;
        this.FUEL_TYPE = FuelType.Type.DIESEL;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROVER_MODULES, RoverModules.empty());
    }

    public void setRoverModules(Modules<RoverModule> modules) {
        this.entityData.set(ROVER_MODULES, modules);
    }

    public Modules<RoverModule> getRoverModules() {
        return this.entityData.get(ROVER_MODULES);
    }

    /**
     * The fuel type the motor accepts, driven by an installed MOTOR module (defaults to diesel).
     */
    public FuelType.Type getMotorFuelType() {
        for (RoverModule module : this.getRoverModules()) {
            if (module.getRoverFeature() == RoverModule.RoverFeature.MOTOR && module.getFuelType() != null) {
                return module.getFuelType();
            }
        }
        return FuelType.Type.DIESEL;
    }

    /**
     * The tank capacity, driven by an installed TANK module (defaults to {@link #BASE_TANK_CAPACITY}).
     */
    public int getTankCapacity() {
        for (RoverModule module : this.getRoverModules()) {
            if (module.getRoverFeature() == RoverModule.RoverFeature.TANK && module.getTankCapacity() > 0) {
                return module.getTankCapacity();
            }
        }
        return BASE_TANK_CAPACITY;
    }

    /**
     * The speed multiplier, driven by an installed SPEED module (defaults to 1).
     */
    public float getSpeedModifier() {
        for (RoverModule module : this.getRoverModules()) {
            if (module.getRoverFeature() == RoverModule.RoverFeature.SPEED) {
                return module.getSpeedModifier();
            }
        }
        return 1f;
    }

    public int getInventoryRows() {
        int rows = 1;
        for (RoverModule module : this.getRoverModules()) {
            rows += module.getExtraInventoryRows();
        }
        return Math.min(rows, MAX_INVENTORY_ROWS);
    }

    @Override
    public float getMaxSpeed() {
        return 0.8F * getSpeedModifier();
    }

    @Override
    public float getMaxReverseSpeed() {
        return 0.6F * getSpeedModifier();
    }

    @Override
    public float getAcceleration() {
        return (1.8F * getSpeedModifier() * 0.5f) / 2;
    }

    @Override
    public float getMaxRotationSpeed() {
        return 6.8F;
    }

    @Override
    public float getMinRotationSpeed() {
        return 4.8F;
    }

    @Override
    public float getRollResistance() {
        return 1.5F;
    }

    @Override
    public float getRotationModifier() {
        return 2.9F;
    }

    @Override
    public float getPitch() {
        return 0.75F;
    }

    @Override
    public double getPlayerYOffset() {
        return 1f;
    }

    @Override
    public int getPassengerSize() {
        return 2;
    }

    @Override
    public Vector3d[] getPlayerOffsets() {
        return new Vector3d[] {
                new Vector3d(0.45D, 0.5D, -0.35D),
                new Vector3d(0.45D, 0.5D, 0.35D)
        };
    }

    @Override
    public boolean doesEnterThirdPerson() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 vec3) {
        InteractionResult result = InteractionResult.SUCCESS;

        if (!this.level().isClientSide()) {
            if (player.isCrouching()) {
                Item heldItem = player.getItemInHand(hand).getItem();
                if (tryFillUpRover(heldItem, false)) {
                    player.getItemInHand(hand).shrink(1);
                    player.getInventory().add(new ItemStack(Items.BUCKET));
                    return InteractionResult.CONSUME;
                } else {
                    this.openCustomInventoryScreen(player);
                    return InteractionResult.CONSUME;
                }
            }
            if (player.getVehicle() != this) {
                player.startRiding(this);
            }
            return InteractionResult.CONSUME;
        }

        return result;
    }

    @Override
    public void tick() {
        super.tick();
        this.checkContainer();

        if (getDriver() instanceof ServerPlayer serverPlayer) {
            this.syncRoverData(serverPlayer);
        }
    }

    @Override
    protected boolean consumeFuel() {
        if (this.getFuel() <= 0) {
            return false;
        }

        FUEL = Math.max(0, FUEL - FUEL_CONSUMPTION_PER_INTERVAL);

        return true;
    }

    private void checkContainer() {
        if (this.level().isClientSide()) {
            return;
        }

        ItemStack input = this.getInventory().getItem(0);
        if (tryFillUpRover(input.getItem(), true)) {
            return;
        }

        // Fluid cells (and other fluid containers) drain into the tank via the shared machine logic.
        FluidUtil.moveFluidFromItem(0, 0, 1, getInventory(), fuelTank, Long.MAX_VALUE);
    }

    private SimpleContainer getInventory() {
        return inventory;
    }

    public boolean tryFillUpRover(Item item, boolean isFromInventory) {
        if (this.level().isClientSide()) {
            return false;
        }
        if (FUEL >= getTankCapacity() || item == null) {
            return false;
        }

        FuelType.Type itemType = FuelType.Type.getTypeBasedOnItem(item);
        if (itemType == null) {
            return false;
        }

        FuelType.Type motorType = getMotorFuelType();

        if (motorType == itemType.getMotorType()) {
            if (FUEL == 0) {
                FUEL_TYPE = itemType;
            }

            if (itemType == FUEL_TYPE) {
                if (isFromInventory) {
                    boolean leavesEmptyBucket = item == ItemsRegistry.FUEL_BUCKET.get()
                            || item == ItemsRegistry.HYDROGEN_BUCKET.get()
                            || item == ItemsRegistry.DIESEL_BUCKET.get();

                    // If the fuel item leaves an empty bucket, only refuel when it can stack into the remaining slot.
                    if (leavesEmptyBucket && !FluidUtil.addToSlot(getInventory(), 1, new ItemStack(Items.BUCKET))) {
                        return false;
                    }

                    inventory.removeItem(0, 1);
                }

                FUEL += REFUEL_AMOUNT;
                if (FUEL > getTankCapacity()) {
                    FUEL = getTankCapacity();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void kill(ServerLevel level) {
        this.dropEquipment(level);
        this.spawnRoverItem(level);

        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        Entity sourceEntity = source.getEntity();

        if (sourceEntity != null && sourceEntity.isCrouching() && !this.isVehicle()) {
            this.dropEquipment(level);
            this.spawnRoverItem(level);

            this.remove(RemovalReason.DISCARDED);

            return true;
        }

        return false;
    }

    public void syncRoverData(ServerPlayer player) {
        if (!level().isClientSide()) {
            NetworkManager.sendToPlayer(player, new SyncRoverDataPacket(this.getId(), FUEL, FUEL_TYPE.getSerializedName()));
        }
    }

    private void spawnRoverItem(ServerLevel level) {
        ItemEntity entityToSpawn = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), this.toItemStack());
        entityToSpawn.setPickUpDelay(10);

        level.addFreshEntity(entityToSpawn);
    }

    public ItemStack toItemStack() {
        ItemStack roverStack = new ItemStack(ItemsRegistry.ROVER.get(), 1);
        roverStack.set(DataComponentsRegistry.ROVER_MODULES.get(), this.getRoverModules());
        return roverStack;
    }

    public static RoverEntity fromItemStack(Level level, ItemStack stack) {
        RoverEntity rover = new RoverEntity(org.exodusstudio.stellaris.common.registries.EntityTypesRegistry.ROVER.get(), level);
        Modules<RoverModule> modules = stack.getOrDefault(DataComponentsRegistry.ROVER_MODULES.get(), RoverModules.empty());
        rover.setRoverModules(modules);
        return rover;
    }

    protected void dropEquipment(ServerLevel level) {
        for (int i = 0; i < this.inventory.getItems().size(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                this.spawnAtLocation(level, itemstack);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        InventorySaver saver = InventorySaver.fromContainer(this.inventory);
        saver.saveInventory(output);

        output.store("rover_modules", RoverModules.CODEC, this.getRoverModules());

        output.putInt("fuel", FUEL);

        if (FUEL != 0) {
            output.putString("currentFuelItemType", FUEL_TYPE.getSerializedName());
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        InventorySaver.readInventory(input, this.inventory);

        Optional<Modules<RoverModule>> modules = input.read("rover_modules", RoverModules.CODEC);
        modules.ifPresent(this::setRoverModules);

        input.getInt("fuel").ifPresent(fuel -> FUEL = fuel);

        input.getString("currentFuelItemType").ifPresent(type -> {
            if (FUEL != 0) {
                FUEL_TYPE = FuelType.Type.fromString(type);
            }
        });
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            // push the current fuel state so the gauge is correct as soon as the screen opens
            this.syncRoverData(serverPlayer);

            MenuRegistry.openExtendedMenu(serverPlayer, new ExtendedMenuProvider() {

                @Override
                public void saveExtraData(FriendlyByteBuf packetByteBuf) {
                    packetByteBuf.writeVarInt(RoverEntity.this.getId());
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.stellaris.rover");
                }

                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    return new RoverMenu(syncId, inv, inventory, RoverEntity.this.getId(), RoverEntity.this.getInventoryRows());
                }
            });
        }
    }

    public boolean hasCargoModule() {
        return this.getRoverModules().contains(ModulesRegistry.ROVER_CARGO.get());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {

    }

    @Override
    public void dataChanged(AbstractContainerMenu container, int id, int value) {

    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        return fuelTank;
    }
}
