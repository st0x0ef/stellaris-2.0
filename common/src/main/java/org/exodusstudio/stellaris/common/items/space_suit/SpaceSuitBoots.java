package org.exodusstudio.stellaris.common.items.space_suit;

import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.registries.ArmorMaterialsRegistry;

public class SpaceSuitBoots extends SpaceSuitItem {
    public SpaceSuitBoots(Properties properties) {
        super(properties.humanoidArmor(ArmorMaterialsRegistry.SPACE_SUIT, ArmorType.BOOTS));
    }
}
