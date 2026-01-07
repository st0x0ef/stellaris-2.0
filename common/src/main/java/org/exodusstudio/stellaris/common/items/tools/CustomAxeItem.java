package org.exodusstudio.stellaris.common.items.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class CustomAxeItem extends Item {
    public CustomAxeItem(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        super(properties.axe(material, attackDamage, attackSpeed));
    }
}
