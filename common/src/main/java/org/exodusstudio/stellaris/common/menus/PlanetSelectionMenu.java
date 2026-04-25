package org.exodusstudio.stellaris.common.menus;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.utils.InventorySaver;
import org.jetbrains.annotations.Nullable;


public class PlanetSelectionMenu extends AbstractContainerMenu {

    public Player player;
    public Container inventory;
    public AntennaSavedData antennaSavedData;

    public static PlanetSelectionMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new PlanetSelectionMenu(syncId, inventory, new SimpleContainer(0), AntennaSavedData.STREAM_CODEC.decode(data));
    }

    public PlanetSelectionMenu(int syncId, Inventory playerInventory, Container container, AntennaSavedData antennaSavedData) {
        super(MenuTypesRegistry.PLANET_SELECTION_MENU.get(), syncId);

        this.inventory = container;
        this.player = playerInventory.player;
        this.antennaSavedData = antennaSavedData;


    }



    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    public static ExtendedMenuProvider createProvider(MinecraftServer server) {
        AntennaSavedData availableAntennas = AntennaSavedData.getSavedAntennas(server);
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                AntennaSavedData.STREAM_CODEC.encode(buf, availableAntennas);
            }

            @Override
            public PlanetSelectionMenu createMenu(int syncId, Inventory inventory, Player player) {
                return new PlanetSelectionMenu(syncId, inventory, new SimpleContainer(0), availableAntennas);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.stellaris.sd_card_reader");
            }
        };
    }
}
