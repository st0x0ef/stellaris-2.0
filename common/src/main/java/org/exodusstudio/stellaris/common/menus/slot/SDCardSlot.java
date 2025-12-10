package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.menus.SDCardReaderApplicationMenu;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

public class SDCardSlot extends Slot {

    private final SDCardReaderApplicationMenu parentMenu;

    public SDCardSlot(SDCardReaderApplicationMenu parentMenu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.parentMenu = parentMenu;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(ItemsRegistry.SD_CARD.get());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.parentMenu.setCard(this.getItem());
    }

}
