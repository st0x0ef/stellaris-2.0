package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.menus.OxygenDistributorMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class OxygenDistributorBlockEntity extends BaseEnergyContainerBlockEntity {

    private final Set<BlockPos> oxygenatedPosition;
    private final Set<ChunkPos> coveredChunks;

    private int oxygenDistributedTickCounter = 0;
    private boolean isActive = false;

    public OxygenDistributorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.OXYGEN_DISTRIBUTOR.get(), blockPos, blockState);

        this.oxygenatedPosition = new HashSet<>();
        this.coveredChunks = new HashSet<>();
    }

    @Override
    public void tick(Level level, BlockState state) {
        if (!Stellaris.CONFIG.oxygenConfig.enableOxygenSystem) {
            return;
        }

        if (oxygenDistributedTickCounter > 0) {
            oxygenDistributedTickCounter--;
            return;
        } else {
            oxygenDistributedTickCounter = Stellaris.CONFIG.oxygenConfig.oxygenUpdateInterval;
        }

        oxygenatedPosition.clear();
        coveredChunks.clear();

        if (energyContainer.getEnergy() > 0) {
            UniversalFluidItemStorage itemStorage = Capabilities.Fluid.ITEM.getCapability(getItem(0));
            if (itemStorage != null) {
                FluidStack fluidStack = itemStorage.getFluidInTank(0);
                if (!fluidStack.isEmpty()) {
                    Set<ChunkPos> allowedChunks = OxygenUtils.getAllowedChunks(level, worldPosition);
                    coveredChunks.addAll(allowedChunks);

                    Set<BlockPos> newOxygenatedPosition = OxygenUtils.propagateOxygen(level, worldPosition, coveredChunks);
                    if (!newOxygenatedPosition.isEmpty()) {
                        oxygenatedPosition.addAll(newOxygenatedPosition);

                        int livingEntitiesCount = OxygenUtils.getEntityWhoNeedsOxygen(level, coveredChunks);
                        if (livingEntitiesCount > 0 && fluidStack.getAmount() >= livingEntitiesCount) {
                            itemStorage.drain(fluidStack.copyWithAmount(livingEntitiesCount), false);
                            energyContainer.extract(1, false);
                        }
                    }
                }
            }
        }

        boolean newIsActive = !oxygenatedPosition.isEmpty();
        if (newIsActive != isActive) {
            isActive = newIsActive;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        setChanged();
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("item.stellaris.oxygen_distributor");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new OxygenDistributorMenu(containerId, inventory, this, this);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public boolean isOxygenated(BlockPos pos) {
        return coversChunk(pos) && oxygenatedPosition.contains(pos);
    }

    public boolean coversChunk(BlockPos pos) {
        return coveredChunks.contains(ChunkPos.containing(pos));
    }

    public Set<ChunkPos> getCoveredChunks() {
        return coveredChunks;
    }

    public Set<BlockPos> getOxygenatedPositions() {
        return Collections.unmodifiableSet(oxygenatedPosition);
    }
}
