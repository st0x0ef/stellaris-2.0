package org.exodusstudio.stellaris.common.menus;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WikiApplicationMenu extends AbstractContainerMenu {

    private final Container inventory;
    private final Player player;
    public Identifier openedEntryInfo;

    public static WikiApplicationMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new WikiApplicationMenu(syncId, inventory, new SimpleContainer(0), data.readNullable(FriendlyByteBuf::readIdentifier));
    }

    public WikiApplicationMenu(int syncId, Inventory playerInventory, Container container, @Nullable Identifier entry) {
        super(MenuTypesRegistry.WIKI.get(), syncId);

        this.inventory = container;
        this.player = playerInventory.player;
        this.openedEntryInfo = entry;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }


    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {

        return ItemStack.EMPTY;
    }

    public Player getPlayer() {
        return player;
    }

    public static ExtendedMenuProvider createProvider(@Nullable Identifier entryId) {
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeNullable(entryId, FriendlyByteBuf::writeIdentifier);
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                return new WikiApplicationMenu(syncId, inventory, new SimpleContainer(0), entryId);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.stellaris.sd_card_reader");
            }
        };
    }

}
