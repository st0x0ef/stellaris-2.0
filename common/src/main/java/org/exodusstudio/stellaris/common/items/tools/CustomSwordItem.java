package org.exodusstudio.stellaris.common.items.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class CustomSwordItem extends Item {
    public CustomSwordItem(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        super(properties.sword(material, attackDamage, attackSpeed));
    }
}
