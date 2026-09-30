package org.exodusstudio.stellaris.common.entities.vehicles.base;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.vehicle_upgrade.FuelType;

public abstract class IVehicleEntity extends Entity {

    public int FUEL;

    public FuelType.Type FUEL_TYPE = FuelType.Type.FUEL;

    public IVehicleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    /** Enable Interact with the Entity */
    @Override
    public boolean isPickable() {
        return true;
    }

    /** Interact with the Entity Gui,Spawn Egg... */
    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();
        this.rotAnim();
    }

    public void rotAnim() {
        while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }

        while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    public int getFuel() {
        return this.FUEL;
    }

    public FuelType.Type getFuelType() {
        return this.FUEL_TYPE;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return NetworkManager.createAddEntityPacket(this, entity);
    }

    public boolean setPassengersRiding() {
        return true;
    }
}
