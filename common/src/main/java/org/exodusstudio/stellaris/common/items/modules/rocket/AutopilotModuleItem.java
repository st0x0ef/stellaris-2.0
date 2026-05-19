package org.exodusstudio.stellaris.common.items.modules.rocket;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.Objects;
import java.util.function.Consumer;

public class AutopilotModuleItem extends Item implements RocketModule {
    public AutopilotModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, MainTabletMenu.createProvider(IdentifierUtils.id("applications/planet_selection")));
        }

        return super.use(level, player, hand);
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.OTHER;
    }

    @Override
    public String getDisplayName() {
        return "Autopilot Module";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rocket_module").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.empty()); // new line

        if (stack.has(DataComponentsRegistry.AUTOPILOT.get())) {
            String destination = Component.translatable(Objects.requireNonNull(stack.get(DataComponentsRegistry.AUTOPILOT.get())).translationKey()).getString();
            tooltipAdder.accept(Component.literal("Autopilot Destination: " + destination).withColor(Utils.getMinecraftColor("yellow")));
            tooltipAdder.accept(Component.literal("Right-click to change destination").withColor(Utils.getMinecraftColor("gray")));
        } else {
            tooltipAdder.accept(Component.literal("Right-click to set destination").withColor(Utils.getMinecraftColor("gray")));
        }
    }
}
