package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.energy.BaseEnergyStorage;
import com.fej1fun.potentials.providers.EnergyProvider;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.network.packets.SyncEnergyPacketWithoutDirection;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for block entities that have an energy storage and an inventory.
 */
public abstract class BaseEnergyContainerBlockEntity extends BaseContainerBlockEntity implements EnergyProvider.BLOCK, ImplementedInventory, TickingBlockEntity {

    public static final String ENERGY_TAG = "stellaris.energyContainer";

    protected EnergyStorage energyContainer;
    protected NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

    public BaseEnergyContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int initialMaxCapacity, int initialMaxInsert, int initialMaxExtract) {
        super(type, pos, state);
        this.energyContainer = new EnergyStorage(initialMaxCapacity, initialMaxInsert, initialMaxExtract) {
            @Override
            protected void onChange() {
                setChanged();
                if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                    NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                            new SyncEnergyPacketWithoutDirection(energyContainer.getEnergy(), getBlockPos()));
                }
            }
        };
    }

    public BaseEnergyContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int initialMaxCapacity) {
        this(type, pos, state, initialMaxCapacity, initialMaxCapacity, initialMaxCapacity);
    }

    public BaseEnergyContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, 12800);
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
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (input.getInt(ENERGY_TAG).isPresent()) {
            getEnergy(null).setEnergyStored(input.getInt(ENERGY_TAG).get());
        }

        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(ENERGY_TAG, getEnergy(null).getEnergy());
        ContainerHelper.saveAllItems(output, items);
    }

    @Override
    public @NotNull BaseEnergyStorage getEnergy(@Nullable Direction direction) {
        return energyContainer;
    }
}