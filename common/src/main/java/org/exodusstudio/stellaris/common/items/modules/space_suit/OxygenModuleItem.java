package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class OxygenModuleItem extends Item implements SpaceSuitModule.OxygenModule {
    private final int oxygenCapacity;

    public OxygenModuleItem(Properties properties, int oxygenCapacity) {
        super(properties);
        this.oxygenCapacity = oxygenCapacity;
    }

    @Override
    public int getCapacity() {
        return oxygenCapacity;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_HELMET.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Store up to " + this.oxygenCapacity + "mb of oxygen on your space suit.").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_space_suit_helmet_module").withColor(Utils.getMinecraftColor("gray")));
    }
}
