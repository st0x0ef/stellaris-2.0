package org.exodusstudio.stellaris.common.menus;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;


public class MainTabletMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;

    public MainTabletMenu(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        this(syncId, inventory);
    }

    public MainTabletMenu(int syncId, Inventory playerInventory) {
        super(MenuTypesRegistry.TABLET_MENU.get(), syncId);

        this.playerInventory = playerInventory;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static ExtendedMenuProvider createProvider() {
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {

            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                return new MainTabletMenu(syncId, inventory);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.stellaris.tablet");
            }
        };
    }
}
