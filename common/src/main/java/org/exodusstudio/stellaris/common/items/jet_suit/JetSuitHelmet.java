package org.exodusstudio.stellaris.common.items.jet_suit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.registries.ArmorMaterialsRegistry;

public class JetSuitHelmet extends Item {
    public JetSuitHelmet(Properties properties) {
        super(properties.humanoidArmor(ArmorMaterialsRegistry.JET_SUIT, ArmorType.HELMET));
    }
}
