package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

import java.util.UUID;

public class AntennaMenu extends BaseContainer {

    private final Player player;
    private final AntennaBlockEntity blockEntity;
    public UUID antennaId;

    //We retrieve the antenna on the server side even if it's not very needed
    public Antenna antenna;

    public static AntennaMenu create(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        AntennaBlockEntity blockEntity = (AntennaBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos());
        return new AntennaMenu(containerId, inventory, blockEntity,
                buf.readNullable((c) -> c.readUUID()),
                buf.readNullable(Antenna.STREAM_CODEC));
    }

    public AntennaMenu(int containerId, Inventory inventory, AntennaBlockEntity blockEntity, UUID antennaId, Antenna antenna) {
        super(MenuTypesRegistry.ANTENNA.get(), containerId, 0, inventory, 10, 106);
        this.player = inventory.player;
        this.blockEntity = blockEntity;
        this.antennaId = antennaId;
        this.antenna = antenna;

    }

    public Antenna getAntenna(Level level) {
        if(level.isClientSide()) return null;
        AntennaSavedData antennaSavedData = AntennaSavedData.getSavedBlockData(level.getServer());
        return antennaSavedData.getAntenna(this.antennaId);
    }


    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.level().isClientSide();
    }

    public Player getPlayer() {
        return player;
    }

    public AntennaBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
