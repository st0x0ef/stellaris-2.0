package org.exodusstudio.stellaris.common.items.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class CustomHoeItem extends Item {
    public CustomHoeItem(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        super(properties.hoe(material, attackDamage, attackSpeed));
    }
}
