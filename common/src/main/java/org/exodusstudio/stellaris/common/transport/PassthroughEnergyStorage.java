package org.exodusstudio.stellaris.common.transport;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

public final class PassthroughEnergyStorage implements UniversalEnergyStorage {

    private final Level level;
    private final BlockPos pos;
    private final @Nullable Direction side;
    private final int transferRate;

    public PassthroughEnergyStorage(Level level, BlockPos pos, @Nullable Direction side, int transferRate) {
        this.level = level;
        this.pos = pos;
        this.side = side;
        this.transferRate = transferRate;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (amount <= 0 || level.isClientSide()) {
            return 0;
        }
        int capped = Math.min(amount, transferRate);
        if (capped <= 0) {
            return 0;
        }

        EnergyStorage source = new EnergyStorage(capped, 0, capped) {
            @Override
            protected void onChange() {
            }
        };
        source.setEnergyStored(capped);

        TransportGraph.Network network = TransportGraph.get(level, pos, TransportMedium.ENERGY);
        BlockPos[] exclude = side != null ? new BlockPos[]{pos.relative(side)} : new BlockPos[0];
        return (int) Transport.spreadAcrossNetwork(level, pos, source, Transport.energyMover(capped),
                TransportMedium.ENERGY, network, simulate, exclude);
    }

    @Override
    public int extract(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergy() {
        return 0;
    }

    @Override
    public int getMaxEnergy() {
        return transferRate;
    }

    @Override
    public void setEnergyStored(int amount) {
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }
}
