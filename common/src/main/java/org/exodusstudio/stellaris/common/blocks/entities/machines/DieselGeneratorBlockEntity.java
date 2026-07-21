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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.DieselGeneratorBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.DieselGeneratorMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacketWithoutDirection;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DieselGeneratorBlockEntity extends BaseGeneratorBlockEntity implements FluidProvider.BLOCK {

    private int litTime;

    private final SingleFluidStorage dieselTank;

    public DieselGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(BlockEntitiesRegistry.DIESEL_GENERATOR.get(), blockPos, blockState, 5, 60000);
    }

    public DieselGeneratorBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState, int energyGeneratedPT, int maxCapacity) {
        super(entityType, blockPos, blockState, energyGeneratedPT, maxCapacity);

        this.dieselTank = new SingleFluidStorage(3000) {
            @Override
            protected void onChange() {
                setChanged();
                if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                    NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                            new SyncFluidPacketWithoutDirection(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())), 0, getBlockPos()));
                }
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return stack.getFluid().isSame(FluidsRegistry.FLOWING_DIESEL.get());
            }
        };
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        if (inventory.player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new SyncFluidPacketWithoutDirection(
                    new FluidAmountMapDataComponent(List.of(dieselTank.getFluidInTank(0).getFluid()), List.of(dieselTank.getFluidValueInTank())),
                    0, getBlockPos()));
        }
        return new DieselGeneratorMenu(containerId, inventory, this, this);
    }

    @Override
    public void tick(Level level, BlockState state) {
        boolean wasLit = isLit();
        boolean shouldUpdate = false;

        if (canGenerate()) {
            --litTime;
        }

        FluidUtil.moveFluidFromItem(0, 0, 1, this, dieselTank, Long.MAX_VALUE);

        if (!dieselTank.isEmpty() && !isLit()) {
            // TODO : use recipe to manage diesel consumption and energy production
            litTime = 10;
            dieselTank.drain(dieselTank.getFluidInTank(0).copyWithAmount(5), false);
            shouldUpdate = true;
        }

        if (isLit()) {
            energyContainer.insertWithoutLimits(energyGeneratedPT, false);
        }

        if (wasLit != isLit()) {
            shouldUpdate = true;
            BlockState newState = state.setValue(DieselGeneratorBlock.LIT, isLit());
            level.setBlock(getBlockPos(), newState, 3);
        }
        if (shouldUpdate) {
            setChanged();
        }

        EnergyUtil.distributeEnergyNearby(level, worldPosition, maxCapacity);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public boolean canGenerate() {
        boolean isMaxEnergy = energyContainer.getEnergy() == energyContainer.getMaxEnergy();
        return isLit() && !isMaxEnergy;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        litTime = input.getIntOr("BurnTime", 0);
        dieselTank.load(input, "diesel");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("BurnTime", this.litTime);
        dieselTank.save(output, "diesel");
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("item.stellaris.diesel_generator");
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    public SingleFluidStorage getDieselTank() {
        return dieselTank;
    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        return dieselTank;
    }
}
