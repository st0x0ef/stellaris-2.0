package org.exodusstudio.stellaris.common.networks.capabilities;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NetworkEnergyStorage implements UniversalEnergyStorage {

    private Runnable markDirtyCallback;

    public static final Codec<NetworkEnergyStorage> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.fieldOf("tank").forGetter(NetworkEnergyStorage::getEnergy),
        Codec.INT.fieldOf("capacity").forGetter(NetworkEnergyStorage::getMaxEnergy)
    ).apply(i, NetworkEnergyStorage::new));

    protected int energy;
    protected int capacity;

    public NetworkEnergyStorage(int energy, int capacity) {
        this.energy = energy;
        this.capacity = capacity;
    }
    public NetworkEnergyStorage() {}

    public void setEnergyStored(int energy) {
        this.energy = Math.clamp(energy, 0, this.getMaxEnergy());
        markDirty();
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        markDirty();
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getMaxEnergy() {
        return this.capacity;
    }

    public int insert(int amount, boolean simulate) {
        if (amount < 0) {
            return this.extract(-amount, simulate);
        } else if (!this.canInsertEnergy()) {
            return 0;
        } else {
            int toReceive = Math.clamp((this.capacity - this.getEnergy()), 0, amount);
            if (!simulate) {
                this.setEnergyStored(this.getEnergy() + toReceive);
                markDirty();
            }

            return toReceive;
        }
    }

    public int extract(int amount, boolean simulate) {
        if (amount < 0) {
            return this.insert(-amount, simulate);
        } else if (!this.canExtractEnergy()) {
            return 0;
        } else {
            int toExtract = Math.min(this.getEnergy(), amount);
            if (!simulate) {
                this.setEnergyStored(this.getEnergy() - toExtract);
                markDirty();
            }

            return toExtract;
        }
    }

    public boolean canInsertEnergy() {
        return true;
    }

    public boolean canExtractEnergy() {
        return true;
    }

    public void setMarkDirtyCallback(Runnable callback) {
        this.markDirtyCallback = callback;
    }

    protected void markDirty() {
        if (this.markDirtyCallback != null) {
            this.markDirtyCallback.run();
        }
    }

}
