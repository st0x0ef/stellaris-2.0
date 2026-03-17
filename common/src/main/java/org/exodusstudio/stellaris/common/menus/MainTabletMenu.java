package org.exodusstudio.stellaris.common.menus;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.Nullable;


public class MainTabletMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;
    @Nullable
    public final Identifier nextScreen;

    public MainTabletMenu(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        this(syncId, inventory, buffer.readNullable(FriendlyByteBuf::readIdentifier));
    }

    public MainTabletMenu(int syncId, Inventory playerInventory, @Nullable Identifier nextScreen) {
        super(MenuTypesRegistry.TABLET.get(), syncId);

        this.playerInventory = playerInventory;
        this.nextScreen = nextScreen;
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
        return createProvider(null);
    }

    /**
     * Use this method to create the tablet and optionally specify the application to open.
     * @param identifier the identifier to a registered application, if null it will just open the tablet without any application
     * @return an ExtendedMenuProvider that can be used to open the tablet
     */
    public static ExtendedMenuProvider createProvider(@Nullable
                                                      Identifier identifier) {
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeNullable(identifier, FriendlyByteBuf::writeIdentifier);
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                return new MainTabletMenu(syncId, inventory, identifier);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.stellaris.tablet");
            }
        };
    }


}
