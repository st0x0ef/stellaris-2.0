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
import org.exodusstudio.stellaris.common.networks.capabilities.FluidNetwork;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class FluidPipeBlock extends NetworkBlock<FluidNetwork, FluidPipeBlockEntity> {

    private final int singlePipeCapacity;

    public FluidPipeBlock(int capacity, Properties properties) {
        super(properties);
        this.singlePipeCapacity = capacity;
    }

    public int getCapacity() {
        return singlePipeCapacity;
    }

    @Override
    public SavedDataType<NetworkManager<FluidNetwork>> getNetworkDataType() {
        return NetworkManager.FLUID_DATA_TYPE;
    }

    @Override
    public BlockCapabilityHolder<FluidNetwork, Direction> getNetworkCapability() {
        return FluidNetwork.NETWORK_CAPABILITY;
    }

    @Override
    public FluidNetwork createEmptyNetwork() {
        return new FluidNetwork();
    }

    @Override
    protected void onCableAdded(ServerLevel level, FluidNetwork network, BlockState state, BlockPos pos) {
        network.tank().setCapacity(network.tank().getTankCapacity(0) + singlePipeCapacity);
    }

    @Override
    protected void onNetworksMerged(FluidNetwork survivor, FluidNetwork dead) {
        long totalFluid = survivor.getFluidStack().getAmount() + dead.getFluidStack().getAmount();
        long totalCapacity = survivor.tank().getTankCapacity(0) + dead.tank().getTankCapacity(0);

        survivor.tank().setAmount(totalFluid);
        survivor.tank().setCapacity(totalCapacity);
    }

    @Override
    protected void recalculateNetwork(ServerLevel level, FluidNetwork network) {
        int newCapacity = calculateTotalCapacity(level, network.cables());
        network.tank().setCapacity(newCapacity);
        network.tank().setAmount(Math.min(network.getFluidStack().getAmount(), newCapacity));
    }

    @Override
    protected void splitNetwork(ServerLevel level, FluidNetwork originalNetwork, List<FluidNetwork> newSubNetworks, List<Set<BlockPos>> components) {
        long originalTotalFluid = originalNetwork.getFluidStack().getAmount();
        long totalRemainingCapacity = 0;
        long[] subCapacities = new long[components.size()];

        for (int i = 0; i < components.size(); i++) {
            subCapacities[i] = calculateTotalCapacity(level, components.get(i));
            totalRemainingCapacity += subCapacities[i];
        }

        // Survivor Network (Index 0)
        originalNetwork.tank().setCapacity(subCapacities[0]);
        double survivorRatio = totalRemainingCapacity > 0 ? (double) subCapacities[0] / totalRemainingCapacity : 0;
        originalNetwork.tank().setAmount((long) (originalTotalFluid * survivorRatio));

        // New Networks (Index 1..n)
        for (int i = 0; i < newSubNetworks.size(); i++) {
            FluidNetwork newSubNetwork = newSubNetworks.get(i);
            long subCap = subCapacities[i + 1];

            newSubNetwork.tank().setCapacity(subCap);
            double subRatio = totalRemainingCapacity > 0 ? (double) subCap / totalRemainingCapacity : 0;
            newSubNetwork.tank().setAmount((long) (originalTotalFluid * subRatio));
        }
    }

    @Override
    protected MapCodec<FluidPipeBlock> codec() {
        return RecordCodecBuilder.mapCodec(
                i -> i.group(
                        Codec.INT.fieldOf("capacity").forGetter(FluidPipeBlock::getCapacity),
                        propertiesCodec()
                ).apply(i, FluidPipeBlock::new));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new FluidPipeBlockEntity(worldPosition, blockState);
    }

    private int calculateTotalCapacity(ServerLevel level, Collection<BlockPos> cables) {
        int total = 0;
        for (BlockPos pos : cables)
            if (level.getBlockState(pos).getBlock() instanceof FluidPipeBlock pipeBlock)
                total += pipeBlock.getCapacity();

        return total;
    }

    @Override
    protected boolean isExternalConnectable(Level level, BlockPos selfPos, Direction direction, BlockPos neighborPos) {
        return Capabilities.Fluid.BLOCK.getCapability(level, neighborPos, direction.getOpposite()) != null;
    }
}
