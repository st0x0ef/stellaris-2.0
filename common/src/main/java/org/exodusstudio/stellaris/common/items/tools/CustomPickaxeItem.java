package org.exodusstudio.stellaris.common.items.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class CustomPickaxeItem extends Item {
    public CustomPickaxeItem(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        super(properties.pickaxe(material, attackDamage, attackSpeed));
    }
}
