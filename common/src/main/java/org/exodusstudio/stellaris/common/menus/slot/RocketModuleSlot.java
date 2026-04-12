package org.exodusstudio.stellaris.common.menus.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.registries.ModulesRegistry;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;

public class RocketModuleSlot extends Slot {

  public RocketModuleSlot(Container container, int slot, int x, int y) {
      super(container, slot, x, y);
  }

  public boolean mayPlace(ItemStack stack) {
      return stack.getItem() instanceof RocketModule module && StellarisRegistries.ROCKET_MODULES.containsValue(module);
  }

}
