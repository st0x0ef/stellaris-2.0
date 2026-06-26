package org.exodusstudio.stellaris.common.entities.vehicles;

import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.components.RoverComponent;
import org.exodusstudio.stellaris.common.entities.vehicles.base.AbstractRoverBase;
import org.exodusstudio.stellaris.common.items.VehicleUpgradeItem;
import org.exodusstudio.stellaris.common.menus.RoverMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncRoverComponentPacket;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.vehicle_upgrade.FuelType;
import org.exodusstudio.stellaris.common.vehicle_upgrade.MotorUpgrade;
import org.exodusstudio.stellaris.common.vehicle_upgrade.SpeedUpgrade;
import org.exodusstudio.stellaris.common.vehicle_upgrade.TankUpgrade;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.utils.InventorySaver;
import org.joml.Vector3d;

public class RoverEntity extends AbstractRoverBase implements HasCustomInventoryScreen, ContainerListener {

    public MotorUpgrade motorUpgrade;
    public TankUpgrade tankUpgrade;
    public SpeedUpgrade speedUpgrade;
    public final SimpleContainer inventory;

    public RoverComponent roverComponent;

    public RoverEntity(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.inventory = new SimpleContainer(13);

        this.motorUpgrade = MotorUpgrade.getBasic(false);
        this.tankUpgrade = TankUpgrade.getBasic();
        this.speedUpgrade = SpeedUpgrade.getBasic();
        this.FUEL = 0;
        this.FUEL_TYPE = FuelType.Type.DIESEL;
        this.roverComponent = new RoverComponent(FUEL_TYPE.getSerializedName(), FUEL, FUEL_TYPE.getFuelTexture(),
                tankUpgrade.getTankCapacity(), speedUpgrade.getSpeedModifier());
    }

    public void setRoverComponent(RoverComponent roverComponent) {
        this.roverComponent = roverComponent;

        this.motorUpgrade = roverComponent.getMotorUpgrade();
        this.tankUpgrade = roverComponent.getTankUpgrade();
        this.speedUpgrade = roverComponent.getSpeedUpgrade();
        this.FUEL = roverComponent.getFuel();
        this.FUEL_TYPE = roverComponent.getFuelType();
    }

    @Override
    public float getMaxSpeed() {
        return 0.8F * speedUpgrade.getSpeedModifier();
    }

    @Override
    public float getMaxReverseSpeed() {
        return 0.6F * speedUpgrade.getSpeedModifier();
    }

    @Override
    public float getAcceleration() {
        return (1.8F * speedUpgrade.getSpeedModifier() * 0.5f) / 2;
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
            this.syncRocketData(serverPlayer);
        }
    }

    @Override
    protected boolean consumeFuel() {
        if (this.getFuel() <= 0) {
            return false;
        }

        FUEL -= 1;

        return this.getFuel() >= 0;
    }

    private void checkContainer() {
        if (this.level().isClientSide()) {
            return;
        }

        if (this.getInventory().getItem(2).getItem() instanceof VehicleUpgradeItem item) {
            if (item.getUpgrade() instanceof MotorUpgrade upgrade) {
                this.motorUpgrade = upgrade;
            }
        } else if (this.getInventory().getItem(2).isEmpty()) {
            this.motorUpgrade = MotorUpgrade.getBasic(false);
        }

        if (this.getInventory().getItem(3).getItem() instanceof VehicleUpgradeItem item) {
            if (item.getUpgrade() instanceof SpeedUpgrade upgrade) {
                this.speedUpgrade = upgrade;
            }
        } else if (this.getInventory().getItem(3).isEmpty()) {
            this.speedUpgrade = SpeedUpgrade.getBasic();
        }

        if (this.getInventory().getItem(4).getItem() instanceof VehicleUpgradeItem item) {
            if (item.getUpgrade() instanceof TankUpgrade upgrade) {
                this.tankUpgrade = upgrade;
            }
        } else if (this.getInventory().getItem(4).isEmpty()) {
            this.tankUpgrade = TankUpgrade.getBasic();
        }

        tryFillUpRover(this.getInventory().getItem(0).getItem(), true);
    }

    private SimpleContainer getInventory() {
        return inventory;
    }

    public boolean tryFillUpRover(Item item, boolean isFromInventory) {
        if (this.level().isClientSide()) {
            return false;
        }
        if (FUEL >= tankUpgrade.getTankCapacity() || item == null) {
            return false;
        }

        FuelType.Type itemType = FuelType.Type.getTypeBasedOnItem(item);
        if (itemType == null) {
            return false;
        }

        FuelType.Type motorType = motorUpgrade.getFuelType();

        if (motorType == itemType.getMotorType()) {
            if (FUEL == 0) {
                FUEL_TYPE = itemType;
            }

            if (itemType == FUEL_TYPE) {
                FUEL += 1000;
                if (FUEL > tankUpgrade.getTankCapacity()) {
                    FUEL = tankUpgrade.getTankCapacity();
                }

                if (isFromInventory) {
                    ItemStack fuelItem = inventory.removeItem(0, 1);

                    if (fuelItem.is(ItemsRegistry.FUEL_BUCKET.get()) || fuelItem.is(ItemsRegistry.HYDROGEN_BUCKET.get()) || fuelItem.is(ItemsRegistry.DIESEL_BUCKET.get())) {
                        inventory.setItem(1, new ItemStack(Items.BUCKET, inventory.getItem(1).getCount() + 1));
                    }
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

    public void syncRocketData(ServerPlayer player) {
        this.roverComponent = new RoverComponent(FUEL_TYPE.getSerializedName(), FUEL, FUEL_TYPE.getFuelTexture(),
                tankUpgrade.getTankCapacity(), speedUpgrade.getSpeedModifier());
        if (!level().isClientSide()) {
            NetworkManager.sendToPlayer(player, new SyncRoverComponentPacket(roverComponent));
        }
    }

    private void spawnRoverItem(ServerLevel level) {
        ItemEntity entityToSpawn = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), this.getRoverItem());
        entityToSpawn.setPickUpDelay(10);
        entityToSpawn.getItem().set(DataComponentsRegistry.ROVER_COMPONENT.get(), roverComponent);

        level.addFreshEntity(entityToSpawn);
    }

    private ItemStack getRoverItem() {
        return ItemsRegistry.ROVER.get().getDefaultInstance();
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

        output.putInt("fuel", FUEL);

        if (FUEL != 0) {
            output.putString("currentFuelItemType", FUEL_TYPE.getSerializedName());
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        InventorySaver.readInventory(input, this.inventory);

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
                    FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                    packetBuffer.writeInt(RoverEntity.this.FUEL);
                    packetBuffer.writeVarInt(RoverEntity.this.getId());
                    return new RoverMenu(syncId, inv, inventory, RoverEntity.this.getId());
                }
            });
        }
    }

    public RoverComponent getRoverComponent() {
        return roverComponent;
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
}
