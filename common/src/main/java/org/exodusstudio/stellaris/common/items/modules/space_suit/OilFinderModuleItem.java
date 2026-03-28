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

public class OilFinderModuleItem extends Item implements SpaceSuitModule.OilFinderModule {
    private final int range;

    public OilFinderModuleItem(Properties properties, int range) {
        super(properties);
        this.range = range;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_HELMET.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Find oil in a " + this.range + " x " + this.range + " chunks area.").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_spacesuit_module").withColor(Utils.getMinecraftColor("gray")));
    }
}
