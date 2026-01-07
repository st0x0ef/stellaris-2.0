package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.menus.OxygenDistributorMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class OxygenDistributorBlockEntity extends BaseEnergyContainerBlockEntity {

    private final Set<BlockPos> oxygenatedPosition;
    private Set<ChunkPos> coveredChunks;

    private int oxygenDistributedTickCounter = 0;

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

        if (energyContainer.getEnergy() == 0) {
            return;
        }

        UniversalFluidItemStorage itemStorage =  Capabilities.Fluid.ITEM.getCapability(getItem(0));
        if (itemStorage == null) {
            return;
        }

        FluidStack fluidStack = itemStorage.getFluidInTank(0);
        if (!fluidStack.isEmpty()) {
            Set<ChunkPos> allowedChunks = OxygenUtils.getAllowedChunks(level, worldPosition);
            coveredChunks.addAll(allowedChunks);

            int livingEntitiesCount = Utils.getSurvivalLivingEntityCountInChunks(level, coveredChunks);
            if (livingEntitiesCount > 0 && fluidStack.getAmount() >= livingEntitiesCount) {
                Set<BlockPos> newOxygenatedPosition = OxygenUtils.propagateOxygen(level, worldPosition, coveredChunks);
                if (!newOxygenatedPosition.isEmpty()) {
                    itemStorage.drain(fluidStack.copyWithAmount(livingEntitiesCount), false);
                    energyContainer.extract(1, false);
                    oxygenatedPosition.addAll(newOxygenatedPosition);
                }
            }
        }
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
        return coveredChunks.contains(new ChunkPos(pos));
    }

    public Set<ChunkPos> getCoveredChunks() {
        return coveredChunks;
    }
}
