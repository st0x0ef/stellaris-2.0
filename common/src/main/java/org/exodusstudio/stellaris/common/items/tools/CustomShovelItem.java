package org.exodusstudio.stellaris.common.items.tools;

import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;

public class CustomShovelItem extends ShovelItem {
    public CustomShovelItem(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, properties);
    }
}
