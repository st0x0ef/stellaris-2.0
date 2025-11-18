package org.exodusstudio.stellaris.neoforge.datagen.tags.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public class StellarisItemTags {
    public static final TagKey<Item> STELLARIS_ORES = create("stellaris_ores");
    public static final TagKey<Item> STELLARIS_INGOTS = create("stellaris_ingots");

    private static TagKey<Item> create(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(MOD_ID, name));
    }
    public StellarisItemTags() {}
}
