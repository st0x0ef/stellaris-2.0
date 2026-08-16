package org.exodusstudio.stellaris.common.items.modules.space_suit;

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
import org.exodusstudio.stellaris.client.overlays.SpaceSuitOverlay;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.ModulesRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Vector2i;

import java.util.function.Consumer;

public class NightVisionModuleItem extends Item implements SpaceSuitModule {

    public NightVisionModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public SpaceSuitFeature getSpaceSuitFeature() {
        return SpaceSuitFeature.VISION;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_HELMET.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.night_vision_module").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_space_suit_helmet_module").withColor(Utils.getMinecraftColor("gray")));
    }

    public static void switchNightVision(Player player) {
        ItemStack helmetStack = player.getItemBySlot(EquipmentSlot.HEAD);

        if(helmetStack.getItem() instanceof SpaceSuitHelmet) {

            Modules<SpaceSuitModule> modules = ModuleUtils.getSpaceSuitModules(helmetStack);
            if (modules.contains(ModulesRegistry.NIGHT_VISION_MODULE.get())) {
                helmetStack.set(DataComponentsRegistry.NIGHT_VISION.get(), !helmetStack.getOrDefault(DataComponentsRegistry.NIGHT_VISION.get(), false));
            }
        }
    }

    @Override
    public Vector2i renderStackedGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Player player, ItemStack stack, int x, int y) {

        boolean nightVisionActive = stack.getOrDefault(DataComponentsRegistry.NIGHT_VISION.get(), false);

        Font font = Minecraft.getInstance().font;

        Component component = Component.translatable("text.stellaris.night_vision.mode")
                .append(": ")
                .append(Component.translatable(nightVisionActive ? "text.stellaris.night_vision.mode.on" : "text.stellaris.night_vision.mode.off"))
                .withStyle(nightVisionActive ? ChatFormatting.GREEN : ChatFormatting.RED);

        x = SpaceSuitOverlay.PADDING;
        graphics.text(font, component, x, y, Utils.getMinecraftColor("white"));


        return new Vector2i(0, font.lineHeight);
    }
}
