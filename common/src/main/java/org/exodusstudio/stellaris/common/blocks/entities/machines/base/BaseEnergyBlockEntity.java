package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.providers.EnergyProvider;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.network.packets.SyncEnergyPacketWithoutDirection;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseEnergyBlockEntity extends BlockEntity implements EnergyProvider.BLOCK, TickingBlockEntity {

    protected @NotNull EnergyStorage energyContainer;
    private int lastSyncedEnergy = Integer.MIN_VALUE;

    public BaseEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxCapacity, int maxInput, int maxOutput) {
        super(type, pos, state);
        this.energyContainer = new EnergyStorage(maxCapacity, maxInput, maxOutput) {
            @Override
            protected void onChange() {
                setChanged();
                syncEnergy();
            }
        };
    }

    private void syncEnergy() {
        if (!(level instanceof ServerLevel serverLevel) || energyContainer.getEnergy() == lastSyncedEnergy) {
            return;
        }
        lastSyncedEnergy = energyContainer.getEnergy();
        List<ServerPlayer> players = Utils.getPlayersIn3x3Chunks(serverLevel, getBlockPos());
        if (!players.isEmpty()) {
            NetworkManager.sendToPlayers(players,
                    new SyncEnergyPacketWithoutDirection(lastSyncedEnergy, getBlockPos()));
        }
    }

    public BaseEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxCapacity) {
        this(type, pos, state, maxCapacity, maxCapacity, maxCapacity);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyContainer.load(input, "base");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyContainer.save(output, "base");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    @Override
    public @Nullable EnergyStorage getEnergy(@Nullable Direction direction) {
        return energyContainer;
    }
}
