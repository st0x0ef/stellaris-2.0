package org.exodusstudio.stellaris.common.items.modules.space_suit;

import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitChestplate;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Vector2i;

import java.util.function.Consumer;

public class HydrogenTankModuleItem extends TankModuleItem {
    public HydrogenTankModuleItem(Properties properties, int hydrogenCapacity) {
        super(properties, hydrogenCapacity);
    }

    @Override
    public Fluid getFuel() {
        return FluidsRegistry.HYDROGEN_FLOWING.get();
    }

    @Override
    public boolean isCompatibleWith(SpaceSuitModule module) {
        return !(module instanceof SpaceSuitModule.CustomFuelModule);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Store up to " + this.capacity + "mb of hydrogen on your space suit.").withColor(Utils.getMinecraftColor("gray")));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}
