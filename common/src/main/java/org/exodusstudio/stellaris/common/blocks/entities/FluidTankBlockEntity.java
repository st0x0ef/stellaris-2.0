package org.exodusstudio.stellaris.common.blocks.entities;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.FluidTankBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TickingBlockEntity;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.FluidTankMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacketWithoutDirection;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//TODO implements outputable
public class FluidTankBlockEntity extends BaseContainerBlockEntity implements FluidProvider.BLOCK, TickingBlockEntity {

    private final SingleFluidStorage fluidTank;
    protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private int renderStage = -1; // -1 to force update on first tick

    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((FluidTankBlock)state.getBlock()).capacity);
    }

    public FluidTankBlockEntity(BlockPos pos, BlockState state, long capacity) {
        super(BlockEntitiesRegistry.FLUID_TANK.get(), pos, state);
        fluidTank = new SingleFluidStorage(capacity) {
            @Override
            protected void onChange() {
                setChanged();
                if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                    NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                            new SyncFluidPacketWithoutDirection(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())), 0, getBlockPos()));
                }
            }
        };
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        fluidTank.save(output, "fluid-tank");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fluidTank.load(input, "fluid_tank");

    }

    @Override
    public @Nullable SingleFluidStorage getFluidTank(@Nullable Direction direction) {
        return fluidTank;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void tick(Level level, BlockState state) {
        int initialRenderStage = renderStage;

        //First - Insert slot
        if (!items.getFirst().isEmpty())
            FluidUtil.moveFluidFromItem(0, 0, 0, items, fluidTank, 1000);

        //Last - Extract slot
        if (!items.getLast().isEmpty())
            FluidUtil.moveFluidToItem(0, fluidTank, 1, 1, items, 1000);

        FluidUtil.distributeFluidNearby(level, worldPosition, fluidTank.getFluidInTank(0).copyWithAmount(fluidTank.getFluidValueInTank() / 20));

        //Update render stage
        renderStage = Math.toIntExact((fluidTank.getFluidValueInTank() * 9) / fluidTank.getTankCapacity(0));

        if (initialRenderStage != renderStage) {
            state.setValue(FluidTankBlock.STAGE, renderStage);
            level.setBlock(getBlockPos(), state, 3);
            setChanged();
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("screen.stellaris.fluid_tank");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new FluidTankMenu(containerId, inventory, this, this);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    public SingleFluidStorage getFluidTank() {
        return fluidTank;
    }
}