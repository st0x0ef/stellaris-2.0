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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.client.overlays.SpaceSuitOverlay;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitChestplate;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Vector2i;

import java.util.function.Consumer;

public class TankModuleItem extends Item implements SpaceSuitModule.CustomFuelModule {
    public int capacity;

    public TankModuleItem(Properties properties, int capacity) {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public Fluid getFuel() {
        return null;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_CHESTPLATE.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_space_suit_chestplate_module").withColor(Utils.getMinecraftColor("gray")));
    }

    @Override
    public Vector2i renderStackedGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Player player, ItemStack stack, int x, int y) {

        ItemStack chestplateStack = player.getItemBySlot(EquipmentSlot.CHEST);
        SpaceSuitModule.CustomFuelModule tankModule = ModuleUtils.getSpaceSuitModule(chestplateStack, SpaceSuitModule.CustomFuelModule.class);

        Font font = Minecraft.getInstance().font;

        if (tankModule != null && chestplateStack.getItem() instanceof SpaceSuitChestplate chestplate && chestplate.getFuelCapacity(tankModule) > 0) {
            FluidStack fuelFluid = FluidUtil.readStoredFluid(chestplateStack, DataComponentsRegistry.FLUID_LIST.get(), 0);


            long fuel = fuelFluid.getAmount();
            long maxFuel = chestplate.getFuelCapacity(tankModule);


            /** FUEL AMOUNT TEXT */
            String fuelName = fuelFluid.getName().getString();
            Component text = Component.literal(fuelName).append(": ").withStyle(ChatFormatting.RED).append("§7" + Math.round(((float) fuel / maxFuel) * 100) + "%");
            graphics.text(font, text, SpaceSuitOverlay.PADDING, y, 0xFFFFFFFF);

            return new Vector2i(0, font.lineHeight);
        }

        return CustomFuelModule.super.renderStackedGui(graphics, deltaTracker, player, stack, x, y);
    }

}
