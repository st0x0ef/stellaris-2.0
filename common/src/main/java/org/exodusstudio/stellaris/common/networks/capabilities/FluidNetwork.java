package org.exodusstudio.stellaris.common.networks.capabilities;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.capabilities.types.BlockCapabilityHolder;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.networks.Network;
import org.exodusstudio.stellaris.common.networks.NetworkEndpoint;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public class FluidNetwork extends Network {

    public static final BlockCapabilityHolder<FluidNetwork, Direction> NETWORK_CAPABILITY =
            BlockCapabilityHolder.createSided(FluidNetwork.class, Identifier.fromNamespaceAndPath(MOD_ID, "fluid_network"));

    public static final Codec<FluidNetwork> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    UUIDUtil.CODEC.fieldOf("id").forGetter(Network::id),
                    BlockPos.CODEC.listOf().xmap(HashSet::new, ArrayList::new).fieldOf("cables").forGetter(Network::cables),
                    NetworkFluidStorage.CODEC.fieldOf("tank").forGetter(FluidNetwork::tank)
            ).apply(i, FluidNetwork::new));

    protected final NetworkFluidStorage tank;

    public FluidNetwork(UUID id, HashSet<BlockPos> cables, NetworkFluidStorage tank) {
        super(id, cables);
        this.tank = tank;
    }

    public FluidNetwork(UUID id, HashSet<BlockPos> cables) {
        super(id, cables);
        this.tank = new NetworkFluidStorage();
    }

    public FluidNetwork() {
        super();
        this.tank = new NetworkFluidStorage();
    }

    public NetworkFluidStorage tank() {
        return tank;
    }
    public FluidStack getFluidStack() {
        return tank.fluidStack;
    }

    @Override
    public Codec<? extends Network> getCodec() {
        return CODEC;
    }

    @Override
    public <N extends Network> boolean canMergeWith(N other) {
        FluidStack thisStack = getFluidStack();
        if (thisStack.isEmpty()) return true;
        if (!(other instanceof FluidNetwork fn)) return false;
        FluidStack otherStack = fn.getFluidStack();
        return otherStack.isEmpty() || thisStack.isFluidEqual(otherStack);
    }

    @Override
    public void tick(ServerLevel level) {
        for (NetworkEndpoint endpoint : getEndpoints()) {
            BlockPos otherPos = endpoint.cablePos().relative(endpoint.direction());
            if (!level.isLoaded(otherPos)) continue;

            UniversalFluidStorage other = Capabilities.Fluid.BLOCK.getCapability(level, otherPos, endpoint.direction().getOpposite());
            if (other == null) continue;
            if (endpoint.isPull())
                FluidUtil.moveFluid(other, tank, other.getFluidInTank(0).copyWithAmount(100000));
            else if (endpoint.isPush())
                FluidUtil.moveFluid(tank, other, tank.getFluidInTank(0).copyWithAmount(100000));
        }
    }

    @Override
    public void setMarkDirtyCallback(Runnable callback) {
        super.setMarkDirtyCallback(callback);
        if (this.tank != null) {
            this.tank.setMarkDirtyCallback(this::markDirty);
        }
    }

}
