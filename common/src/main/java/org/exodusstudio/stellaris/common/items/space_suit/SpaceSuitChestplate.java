package org.exodusstudio.stellaris.common.items.space_suit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.registries.ArmorMaterialsRegistry;

public class SpaceSuitChestplate extends Item {
    public SpaceSuitChestplate(Properties properties) {
        super(properties.humanoidArmor(ArmorMaterialsRegistry.JET_SUIT, ArmorType.CHESTPLATE));
    }
}
