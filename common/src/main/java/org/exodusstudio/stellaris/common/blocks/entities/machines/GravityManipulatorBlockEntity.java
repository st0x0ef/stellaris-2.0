package org.exodusstudio.stellaris.common.blocks.entities.machines;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.menus.GravityManipulatorMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncGravityManipulatorDataPacketC2S;
import org.exodusstudio.stellaris.common.network.packets.SyncGravityManipulatorDataPacketS2C;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

import java.util.List;

public class GravityManipulatorBlockEntity extends BaseEnergyContainerBlockEntity {
    private double gravity = 9.81;

    public GravityManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), pos, state, 3000);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("item.stellaris.gravity_manipulator");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new GravityManipulatorMenu(containerId, this);
    }

    @Override
    public void tick() {
        if  (this.energyContainer.getEnergy() > 0 && this.level != null) {
            this.energyContainer.extract(1, false); // TODO : adjust energy consumption based on new gravity
            syncDataAccess();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putDouble("gravity", getGravity());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        setGravity(input.getDoubleOr("gravity", getGravity()), false); // can't sync since there's no player yet
    }

    public double getDifferenceGravity(double targetGravity) {
        return this.gravity - targetGravity;
    }

    public double getGravity() {
        return Mth.clamp(this.gravity, 0.0, 20.0);
    }

    public double getNormalizedGravity() {
        return Mth.clamp(this.gravity / 20.0, 0.0, 1.0);
    }

    public void setGravity(double gravity, boolean shouldSync) {
        if (Math.abs(getGravity() - gravity) < 0.01) {
            return;
        }

        this.gravity = Mth.floor(Mth.clamp(gravity, 0.0, 20.0) * 100.0) / 100.0;

        if  (shouldSync) {
            syncDataAccess();
        }
    }

    public void syncDataAccess() {
        if (this.level != null) {
            if (this.level.isClientSide()) {
                NetworkManager.sendToServer(new SyncGravityManipulatorDataPacketC2S(getBlockPos(), getGravity()));
            } else {
                NetworkManager.sendToPlayers(getPlayersIn3x3Chunks(), new SyncGravityManipulatorDataPacketS2C(getBlockPos(), getGravity()));
            }
        }
    }

    // 3 x 3 chunks because player could be in adjacent chunk and throw an object into this chunk
    private List<ServerPlayer> getPlayersIn3x3Chunks() {
        List<ServerPlayer> playersInChunks = List.of();
        if (level != null) {
            ChunkPos chunkPos = new ChunkPos(worldPosition);
            AABB chunkAABB = new AABB(chunkPos.getMinBlockX(), level.getMinY(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), level.getMaxY(), chunkPos.getMaxBlockZ());
            AABB expandedAABB = chunkAABB.inflate(16.0 * 1.5); // 1.5 chunks in each direction to cover 3x3 chunks
            playersInChunks = level.getEntitiesOfClass(ServerPlayer.class, expandedAABB);
        }
        return playersInChunks;
    }
}
