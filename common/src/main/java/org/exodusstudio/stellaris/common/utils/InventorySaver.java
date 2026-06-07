package org.exodusstudio.stellaris.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class to save and load the contents of an inventory (Container).
 * This allows to respect item stacks positions.
 */
public record InventorySaver(List<SavedItem> savedItems) {

    public static final Codec<InventorySaver> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SavedItem.CODEC.listOf().fieldOf("savedItems").forGetter(InventorySaver::savedItems)
    ).apply(instance, InventorySaver::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InventorySaver> STREAM_CODEC = StreamCodec.composite(
            SavedItem.STREAM_CODEC.apply(ByteBufCodecs.list()), InventorySaver::savedItems,
            InventorySaver::new
    );

    public static InventorySaver fromContainer(Container container) {
        ArrayList<SavedItem> savedItems = new ArrayList<>();
        for(int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                savedItems.add(new SavedItem(i, stack));
            }
        }

        return new InventorySaver(savedItems);
    }

    public void saveInventory(ValueOutput output) {
        output.store("inventory", CODEC, this);
    }

    public static void readInventory(ValueInput input, Container container) {
        Optional<InventorySaver> inventorySaver = input.read("inventory", CODEC);
        inventorySaver.ifPresent(saver -> saver.readInventory(container));
    }

    public void readInventory(Container container) {
        for (SavedItem savedItem : this.savedItems) {
            container.setItem(savedItem.slot, savedItem.itemStack);
        }
    }

    public record SavedItem(int slot, ItemStack itemStack) {

        public static final Codec<SavedItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(SavedItem::slot),
                ItemStack.OPTIONAL_CODEC.fieldOf("itemStack").forGetter(SavedItem::itemStack)
        ).apply(instance, SavedItem::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SavedItem> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, SavedItem::slot,
                ItemStack.OPTIONAL_STREAM_CODEC, SavedItem::itemStack,
                SavedItem::new
        );
    }
}
