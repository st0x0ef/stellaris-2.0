package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.client.overlays.SpaceSuitOverlay;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Vector2i;

import java.util.function.Consumer;

public class OxygenModuleItem extends Item implements SpaceSuitModule.OxygenModule {

    public static final Identifier OXYGEN_TANK = IdentifierUtils.texture("overlay/oxygen_tank");
    public static final Identifier OXYGEN_TANK_FULL = IdentifierUtils.texture("overlay/oxygen_tank_full");


    private final int oxygenCapacity;

    public OxygenModuleItem(Properties properties, int oxygenCapacity) {
        super(properties);
        this.oxygenCapacity = oxygenCapacity;
    }

    @Override
    public SpaceSuitFeature getSpaceSuitFeature() {
        return SpaceSuitFeature.OXYGEN;
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

    @Override
    public Vector2i renderStackedGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Player player, ItemStack stack, int x, int y) {

        Font font = Minecraft.getInstance().font;
        x = SpaceSuitOverlay.PADDING;

        if (stack.getItem() instanceof SpaceSuitHelmet && SpaceSuitHelmet.getOxygenCapacity(stack) > 0) {
            long oxygen = FluidUtil.readStoredFluid(stack, DataComponentsRegistry.FLUID_LIST.get(), 0).getAmount();
            long maxOxygen = SpaceSuitHelmet.getOxygenCapacity(stack);


            int textureWidth = 124 / 2;
            int textureHeight = 104 / 2;

            /** DRAW OXYGEN TANK */
            graphics.blit(RenderPipelines.GUI_TEXTURED, OXYGEN_TANK, x, y, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

            int filledHeight = (int) Mth.clamp(((float) oxygen / maxOxygen) * textureHeight, 0, textureHeight);
            int yFilledOffset = textureHeight - filledHeight;
            graphics.blit(RenderPipelines.GUI_TEXTURED, OXYGEN_TANK_FULL, x, y + yFilledOffset, 0, yFilledOffset, textureWidth, filledHeight, textureWidth, textureHeight);

            /** OXYGEN AMOUNT TEXT */
            Component text = Component.translatable("fluid.stellaris.oxygen").append(": ").withStyle(ChatFormatting.BLUE).append("§7" + Math.round(((float) oxygen / maxOxygen) * 100) + "%");
            graphics.text(font, text, x, y + textureHeight + 3, 0xFFFFFFFF);

            return new Vector2i(textureWidth, textureHeight + font.lineHeight);
        }

        return new Vector2i();
    }

    @Override
    public int renderPriority() {
        return 9999;
    }
}
