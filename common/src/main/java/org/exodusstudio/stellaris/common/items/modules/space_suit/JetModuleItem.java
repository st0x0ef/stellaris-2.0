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

public class JetModuleItem extends Item implements SpaceSuitModule.JetModule {
    private final long consumptionPerTick;

    public JetModuleItem(Properties properties, long consumptionPerTick) {
        super(properties);
        this.consumptionPerTick = consumptionPerTick;
    }

    @Override
    public long getConsumptionPerTick() {
        return consumptionPerTick;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_BOOTS.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Consumes " + consumptionPerTick + "mb of fuel per tick to allow you to fly.").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_spacesuit_module").withColor(Utils.getMinecraftColor("gray")));
    }
}
