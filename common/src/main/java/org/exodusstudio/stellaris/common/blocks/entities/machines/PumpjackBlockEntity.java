package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.CoalGeneratorBlock;
import org.exodusstudio.stellaris.common.blocks.PumpjackBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.PumpjackMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacket;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PumpjackBlockEntity extends BaseEnergyContainerBlockEntity implements FluidProvider.BLOCK {

    public final SingleFluidStorage resultTank;

    public PumpjackBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.PUMPJACK.get(), pos, state);

        resultTank = new SingleFluidStorage(10000, 0, 10000) {

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
        FluidUtil.moveFluidToItem(0, resultTank, 0, 1, this, Long.MAX_VALUE);

        // Push extracted oil into any adjacent pipe network (or a directly-touching tank/machine).
        FluidUtil.distributeFluidNearby(level, worldPosition, resultTank.getFluidInTank(0));

        ChunkAccess access = level.getChunk(this.worldPosition);
        int chunkOil = access.stellaris$getChunkOilLevel();
        int oilToExtract = Math.min(Stellaris.CONFIG.oilConfig.oilExtractionPerTick, chunkOil);

        boolean generating = oilToExtract > 0
                && energyContainer.getEnergy() >= 2 * oilToExtract
                && resultTank.getFluidValueInTank() + oilToExtract <= resultTank.getTankCapacity(0);

        if (generating) {
            access.stellaris$setChunkOilLevel(chunkOil - oilToExtract);
            resultTank.fillWithoutLimits(FluidStack.create(FluidsRegistry.OIL_STILL.get(), oilToExtract), false);
            energyContainer.extract(2 * oilToExtract, false);
        }

        BlockState currentState = getBlockState();
        if (currentState.getValue(CoalGeneratorBlock.LIT) != generating) {
            level.setBlock(getBlockPos(), currentState.setValue(CoalGeneratorBlock.LIT, generating), 3);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("item.stellaris.pumpjack");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        if (inventory.player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new SyncFluidPacket(
                    new FluidAmountMapDataComponent(List.of(resultTank.getFluidInTank(0).getFluid()), List.of(resultTank.getFluidValueInTank())),
                    0, getBlockPos(), getBlockState().getValue(PumpjackBlock.FACING).getClockWise()));
        }
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
