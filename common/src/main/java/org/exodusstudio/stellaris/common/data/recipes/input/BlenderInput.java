package org.exodusstudio.stellaris.common.data.recipes.input;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class BlenderInput implements RecipeInput {
    private final List<ItemStack> items;
    private final StackedItemContents contents;

    public BlenderInput(List<ItemStack> items) {
        this.items = items;
        this.contents = new StackedItemContents();

        for (ItemStack stack : items) {
            contents.accountStack(stack);
        }
    }

    public StackedItemContents contents() {
        return contents;
    }

    public List<Holder<Item>> distinctItems() {
        List<Holder<Item>> distinct = new ArrayList<>(items.size());

        for (ItemStack stack : items) {
            if (!stack.isEmpty() && !distinct.contains(stack.typeHolder())) {
                distinct.add(stack.typeHolder());
            }
        }

        return distinct;
    }

    public ItemStack findStack(Holder<Item> item) {
        for (ItemStack stack : items) {
            if (stack.is(item.value())) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
