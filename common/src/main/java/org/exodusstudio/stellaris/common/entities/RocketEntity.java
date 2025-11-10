package org.exodusstudio.stellaris.common.entities;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.menus.RocketMenu;
import org.exodusstudio.stellaris.common.registries.EntityDataSerializersRegistry;
import org.exodusstudio.stellaris.common.rocket.RocketModules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RocketEntity extends VehicleEntity  {

    public static final int[] MODULES_SLOT = new int[]{2, 3, 4, 5};
    public static final EntityDataAccessor<RocketModules> ROCKET_MODULES = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializersRegistry.ROCKET_MODULES );

    public RocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.inventory.addListener(this::containerChanged);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FUEL, 0);
        builder.define(ROCKET_MODULES, RocketModules.empty());
    }


    @Override
    public int getFuel() {
        return this.entityData.get(FUEL);
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
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        updateModuleFromContainer();
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


    /**
     * This is used to update the Rocket Modules when the inventory changes
     */
    public void containerChanged(Container container) {
        updateModuleFromContainer();
    }

    public void updateModuleFromContainer() {
        this.entityData.set(ROCKET_MODULES, RocketModules.empty());

        RocketModules.Mutable moduleMutable = RocketModules.Mutable.EMPTY;
        for(int slot : MODULES_SLOT) {
            ItemStack stack = inventory.getItem(slot);
            if(!stack.isEmpty()) {
                moduleMutable.insert(stack);
            }
        }

        this.entityData.set(ROCKET_MODULES, moduleMutable.toImmutable());
    }

    public RocketModules getRocketModules() {
        return this.entityData.get(ROCKET_MODULES);
    }

}
