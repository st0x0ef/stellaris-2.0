package org.exodusstudio.stellaris.common.blocks.entities;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TickingBlockEntity;
import org.exodusstudio.stellaris.common.menus.AntennaMenu;
import org.exodusstudio.stellaris.common.network.packets.AntennasOperations;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class AntennaBlockEntity extends BaseContainerBlockEntity implements TickingBlockEntity {

    public UUID launchPadId = null;
    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);


    public AntennaBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.ANTENNA.get(), blockPos, blockState);
    }


    @Override
    protected @NotNull Component getDefaultName() {
        return Component.literal("Launch Pad Creator");
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AntennaMenu(containerId, inventory, this, this.launchPadId, null);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public void setChanged() {
        if (this.level != null) {
            this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            super.setChanged();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Optional<UUID> optionalUUID = input.read("uuid", UUIDUtil.CODEC);
        optionalUUID.ifPresent(uuid -> {
            this.launchPadId = uuid;
        });
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if(this.launchPadId != null) {
            output.store("uuid", UUIDUtil.CODEC, this.launchPadId);
        }
    }

    public void setAntenna(Antenna antenna, @Nullable UUID uuid, boolean create) {
        if(uuid != null)  {
            this.launchPadId = uuid;
            return;
        }

        NetworkManager.sendToServer(new AntennasOperations(antenna, "set"));
    }

    @Override
    public void tick(Level level, BlockState state) {

    }
}