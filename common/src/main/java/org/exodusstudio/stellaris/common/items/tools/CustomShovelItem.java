package org.exodusstudio.stellaris.common.items.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class CustomShovelItem extends Item {
    public CustomShovelItem(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        super(properties.shovel(material, attackDamage, attackSpeed));
    }
}
