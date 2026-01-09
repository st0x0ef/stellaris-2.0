package org.exodusstudio.stellaris.common.items.jet_suit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.registries.ArmorMaterialsRegistry;

public class JetSuitChestplate extends Item {
    public JetSuitChestplate(Properties properties) {
        super(properties.humanoidArmor(ArmorMaterialsRegistry.JET_SUIT, ArmorType.CHESTPLATE));
    }
}
