package org.exodusstudio.stellaris.common.blocks.cables;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.capabilities.types.BlockCapabilityHolder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.exodusstudio.stellaris.common.networks.NetworkManager;
import org.exodusstudio.stellaris.common.networks.capabilities.EnergyNetwork;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class EnergyCableBlock extends NetworkBlock<EnergyNetwork, EnergyCableBlockEntity> {

    private final int singleCableCapacity;

    public EnergyCableBlock(int capacity, Properties properties) {
        super(properties);
        this.singleCableCapacity = capacity;
    }

    public int getCapacity() {
        return singleCableCapacity;
    }

    @Override
    public SavedDataType<NetworkManager<EnergyNetwork>> getNetworkDataType() {
        return NetworkManager.ENERGY_DATA_TYPE;
    }

    @Override
    public BlockCapabilityHolder<EnergyNetwork, Direction> getNetworkCapability() {
        return EnergyNetwork.NETWORK_CAPABILITY;
    }

    @Override
    public EnergyNetwork createEmptyNetwork() {
        return new EnergyNetwork();
    }

    @Override
    protected void onCableAdded(ServerLevel level, EnergyNetwork network, BlockState state, BlockPos pos) {
        network.energy().setCapacity(network.energy().getMaxEnergy() + singleCableCapacity);
    }

    @Override
    protected void onNetworksMerged(EnergyNetwork survivor, EnergyNetwork dead) {
        int totalEnergy = survivor.energy().getEnergy() + dead.energy().getEnergy();
        int totalCapacity = survivor.energy().getMaxEnergy() + dead.energy().getMaxEnergy();

        survivor.energy().setCapacity(totalCapacity);
        survivor.energy().setEnergyStored(totalEnergy);
    }

    @Override
    protected void recalculateNetwork(ServerLevel level, EnergyNetwork network) {
        int newCapacity = calculateTotalCapacity(level, network.cables());
        network.energy().setCapacity(newCapacity);
        network.energy().setEnergyStored(Math.min(network.energy().getEnergy(), newCapacity));
    }

    @Override
    protected void splitNetwork(ServerLevel level, EnergyNetwork originalNetwork, List<EnergyNetwork> newSubNetworks, List<Set<BlockPos>> components) {
        int originalTotalEnergy = originalNetwork.energy().getEnergy();
        int totalRemainingCapacity = 0;
        int[] subCapacities = new int[components.size()];

        for (int i = 0; i < components.size(); i++) {
            subCapacities[i] = calculateTotalCapacity(level, components.get(i));
            totalRemainingCapacity += subCapacities[i];
        }

        // Survivor Network (Index 0)
        originalNetwork.energy().setCapacity(subCapacities[0]);
        double survivorRatio = totalRemainingCapacity > 0 ? (double) subCapacities[0] / totalRemainingCapacity : 0;
        originalNetwork.energy().setEnergyStored((int) (originalTotalEnergy * survivorRatio));

        // New Networks (Index 1..n)
        for (int i = 0; i < newSubNetworks.size(); i++) {
            EnergyNetwork newSubNetwork = newSubNetworks.get(i);
            int subCap = subCapacities[i + 1];

            newSubNetwork.energy().setCapacity(subCap);
            double subRatio = totalRemainingCapacity > 0 ? (double) subCap / totalRemainingCapacity : 0;
            newSubNetwork.energy().setEnergyStored((int) (originalTotalEnergy * subRatio));
        }
    }

    private int calculateTotalCapacity(ServerLevel level, Collection<BlockPos> cables) {
        int total = 0;
        for (BlockPos pos : cables)
            if (level.getBlockState(pos).getBlock() instanceof EnergyCableBlock cableBlock)
                total += cableBlock.getCapacity();

        return total;
    }

    @Override
    protected MapCodec<EnergyCableBlock> codec() {
        return RecordCodecBuilder.mapCodec(
                i -> i.group(
                        Codec.INT.fieldOf("capacity").forGetter(EnergyCableBlock::getCapacity),
                        propertiesCodec()
                ).apply(i, EnergyCableBlock::new));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new EnergyCableBlockEntity(worldPosition, blockState);
    }

    @Override
    protected boolean isExternalConnectable(Level level, BlockPos selfPos, Direction direction, BlockPos neighborPos) {
        return Capabilities.Energy.BLOCK.getCapability(level, neighborPos, direction.getOpposite()) != null;
    }
}
