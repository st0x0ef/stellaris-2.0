package org.exodusstudio.stellaris.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class TagsRegistry {
    /** ITEMS */
    public static final TagKey<Item> COAL_GENERATOR_FUEL = TagKey.create(Registries.ITEM, ResourceLocationUtils.id("coal_generator_fuel"));

    public static final TagKey<Item> CAN = TagKey.create(Registries.ITEM, ResourceLocationUtils.id("can"));
}
