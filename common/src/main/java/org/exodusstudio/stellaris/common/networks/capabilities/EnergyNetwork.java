package org.exodusstudio.stellaris.common.networks.capabilities;

import com.fej1fun.potentials.capabilities.types.BlockCapabilityHolder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.networks.Network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public class EnergyNetwork extends Network {

    public static final BlockCapabilityHolder<EnergyNetwork, Direction> NETWORK_CAPABILITY =
            BlockCapabilityHolder.createSided(EnergyNetwork.class, Identifier.fromNamespaceAndPath(MOD_ID, "energy_network"));

    public static final Codec<EnergyNetwork> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    UUIDUtil.CODEC.fieldOf("id").forGetter(Network::id),
                    BlockPos.CODEC.listOf().xmap(HashSet::new, ArrayList::new)
                            .fieldOf("cables").forGetter(Network::cables),
                    NetworkEnergyStorage.CODEC.fieldOf("tank").forGetter(EnergyNetwork::energy)
            ).apply(i, EnergyNetwork::new));

    protected final NetworkEnergyStorage energy;

    public EnergyNetwork(UUID id, HashSet<BlockPos> cables, NetworkEnergyStorage energy) {
        super(id, cables);
        this.energy = energy;
    }

    public EnergyNetwork(UUID id, HashSet<BlockPos> cables) {
        super(id, cables);
        this.energy = new NetworkEnergyStorage();
    }

    public EnergyNetwork() {
        super();
        this.energy = new NetworkEnergyStorage();
    }

    public NetworkEnergyStorage energy() {
        return energy;
    }

    @Override
    public Codec<? extends Network> getCodec() {
        return CODEC;
    }

    @Override
    public <N extends Network> boolean canMergeWith(N other) {
        return true;
    }

    @Override
    public void setMarkDirtyCallback(Runnable callback) {
        super.setMarkDirtyCallback(callback);
        if (this.energy != null) {
            this.energy.setMarkDirtyCallback(this::markDirty);
        }
    }
}
