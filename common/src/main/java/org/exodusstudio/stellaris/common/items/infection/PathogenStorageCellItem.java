package org.exodusstudio.stellaris.common.items.infection;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.components.PathogenStorageComponent;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

import java.util.function.Consumer;

public class PathogenStorageCellItem extends Item {
    private static final PathogenStorageComponent DEFAULT_COMPONENT = new PathogenStorageComponent(0, 500);

    public PathogenStorageCellItem(Properties properties) {
        super(properties.component(DataComponentsRegistry.PATHOGEN_STORED.get(), DEFAULT_COMPONENT));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        PathogenStorageComponent pathogenStorageComponents = stack.getOrDefault(DataComponentsRegistry.PATHOGEN_STORED.get(), DEFAULT_COMPONENT);
        String stored = String.valueOf(pathogenStorageComponents.stored());
        String capacity = String.valueOf(pathogenStorageComponents.capacity());

        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.pathogen_storage_cell_info"));
        tooltipAdder.accept(Component.literal("Parasites ").append(Component.literal(stored).append(" / ").append(capacity).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            Inventory inventory = player.getInventory();

            ItemStack storageCellStack = player.getItemInHand(hand);
            PathogenStorageComponent pathogenStorageComponents = storageCellStack.getOrDefault(DataComponentsRegistry.PATHOGEN_STORED.get(), DEFAULT_COMPONENT);

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (inventory.getItem(i).getItem() instanceof ParasiteItem) {
                    if (pathogenStorageComponents.stored() < pathogenStorageComponents.capacity()) {
                        inventory.removeItem(i, 1);

                        pathogenStorageComponents = new PathogenStorageComponent(
                                pathogenStorageComponents.stored() + 1,
                                pathogenStorageComponents.capacity()
                        );

                        storageCellStack.set(DataComponentsRegistry.PATHOGEN_STORED.get(), pathogenStorageComponents);
                    }
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}
