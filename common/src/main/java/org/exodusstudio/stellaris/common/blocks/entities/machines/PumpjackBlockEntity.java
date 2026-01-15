package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.CoalGeneratorBlock;
import org.exodusstudio.stellaris.common.blocks.ElectrolyzerBlock;
import org.exodusstudio.stellaris.common.blocks.PumpjackBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.PumpjackMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacket;
import org.exodusstudio.stellaris.common.network.packets.SyncOilLevelPacket;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PumpjackBlockEntity extends BaseEnergyContainerBlockEntity implements FluidProvider.BLOCK {

    private boolean isGenerating = false;
    private static final long oilToExtract = 10;
    public final SingleFluidStorage resultTank;

    public PumpjackBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.PUMPJACK.get(), pos, state);

        resultTank = new SingleFluidStorage(10000) {

            @Override
            protected void onChange() {
                setChanged();
                if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                    NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                            new SyncFluidPacket(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())),
                                    0, getBlockPos(), getBlockState().getValue(PumpjackBlock.FACING).getClockWise()));
                }
            }
        };
    }

    @Override
    public void tick(Level level, BlockState state) {
        FluidUtil.moveFluidToItem(0, resultTank, 0, 0, items, 1000);

        ChunkAccess access = level.getChunk(this.worldPosition);

        if (!level.isClientSide()) {
            ChunkPos pos = access.getPos();
            NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(), new SyncOilLevelPacket(access.stellaris$getChunkOilLevel(), pos.x, pos.z));
        }

        int actualOilToExtract = (int) oilToExtract;

        if (access.stellaris$getChunkOilLevel() < oilToExtract) {
            actualOilToExtract = access.stellaris$getChunkOilLevel();

            if (actualOilToExtract == 0) {
                return;
            }
        }

        if (energyContainer.getEnergy() >= 2 * actualOilToExtract) {
            if (resultTank.getFluidValueInTank() + actualOilToExtract <= resultTank.getTankCapacity(0)) {
                access.stellaris$setChunkOilLevel(access.stellaris$getChunkOilLevel() - actualOilToExtract);
                resultTank.fill(FluidStack.create(FluidsRegistry.OIL_STILL.get(), actualOilToExtract), false);

                energyContainer.extract(2 * actualOilToExtract, false);
                isGenerating = true;
                setChanged();
            }
            else {
                isGenerating = false;
            }
        }

        if (isGenerating) {
            state = getBlockState().setValue(CoalGeneratorBlock.LIT, true);
        }
        else {
            state = getBlockState().setValue(CoalGeneratorBlock.LIT, false);
        }
        level.setBlock(getBlockPos(), state, 3);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.stellaris.pumpjack");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PumpjackMenu(containerId, inventory, this, this);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        resultTank.load(input, "oil");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        resultTank.save(output, "oil");
    }

    public SingleFluidStorage getResultTank() {
        return resultTank;
    }

    public int chunkOilLevel(Level level) {
        return level.getChunk(getBlockPos()).stellaris$getChunkOilLevel();
    }


    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        return this.resultTank;
    }

}
