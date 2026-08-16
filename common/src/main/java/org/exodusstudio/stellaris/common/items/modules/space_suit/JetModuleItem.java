package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.client.overlays.SpaceSuitOverlay;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitBoots;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Vector2i;

import java.util.function.Consumer;

public class JetModuleItem extends Item implements SpaceSuitModule.JetModule {
    private final long consumptionPerSecond;
    private final double maxUpwardSpeed;

    public JetModuleItem(Properties properties, long consumptionPerSecond, double maxUpwardSpeed) {
        super(properties);
        this.consumptionPerSecond = consumptionPerSecond;
        this.maxUpwardSpeed = maxUpwardSpeed;
    }

    @Override
    public SpaceSuitFeature getSpaceSuitFeature() {
        return SpaceSuitFeature.JET;
    }

    @Override
    public long getConsumptionPerSecond() {
        return consumptionPerSecond;
    }

    @Override
    public double getMaxUpwardSpeed() {
        return maxUpwardSpeed;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.is(ItemsRegistry.SPACE_SUIT_BOOTS.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Consumes " + consumptionPerSecond + "mb of fuel per second to allow you to fly.").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.literal("Climbs at up to " + maxUpwardSpeed + " blocks per tick.").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_space_suit_boots_module").withColor(Utils.getMinecraftColor("gray")));
    }

    @Override
    public Vector2i renderStackedGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Player player, ItemStack stack, int x, int y) {

        x = SpaceSuitOverlay.PADDING;
        Font font = Minecraft.getInstance().font;

        SpaceSuitModule.JetModule jetModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.JetModule.class);
        if (jetModule != null) {
            MutableComponent mode = SpaceSuitBoots.getModeType(stack).getMutableComponent();
            Component text = Component.translatable("text.stellaris.jet.mode").append(": ").withStyle(ChatFormatting.GRAY).append(mode);
            graphics.text(font, text, x, y, Utils.getMinecraftColor("white"));

            return new Vector2i(0, font.lineHeight + 5);
        }

        return new Vector2i(0);
    }
}
