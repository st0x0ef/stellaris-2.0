package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyBlockEntity;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaterPumpBlockEntity extends BaseEnergyBlockEntity implements FluidProvider.BLOCK {

    private static final int NEEDED_ENERGY = 100;

    private final SingleFluidStorage waterTank = new SingleFluidStorage(1000) {
        @Override
        protected void onChange() {
            setChanged();
        }
    };

    public WaterPumpBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.WATER_PUMP.get(), pos, state, 100);
    }

    @Override
    public void tick(Level level, BlockState state) {
        if (energyContainer.getEnergy() < NEEDED_ENERGY) {
            return;
        }

        BlockPos belowPos = worldPosition.below();
        FluidState belowFluidState = level.getFluidState(belowPos);

        if (belowFluidState.is(Fluids.WATER) && belowFluidState.isSource()) {
            BlockState belowState = level.getBlockState(belowPos);
            if (waterTank.getFluidInTank(0).isEmpty()) {
                if (belowState.getBlock() instanceof BucketPickup bucketPickup) {
                    if (!bucketPickup.pickupBlock(null, level, belowPos, belowState).isEmpty()) {
                        waterTank.fill(FluidStack.create(Fluids.WATER, 1000), false);
                        energyContainer.extract(NEEDED_ENERGY, false);
                    }
                }
            }
        }

        FluidUtil.distributeFluidNearby(level, worldPosition, waterTank.getFluidInTank(0), List.of(Direction.UP));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        waterTank.load(input, "water");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        waterTank.save(output, "water");
    }

    public SingleFluidStorage getWaterTank() {
        return waterTank;
    }

    @Override
    public @NotNull UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        return this.waterTank;
    }
}
