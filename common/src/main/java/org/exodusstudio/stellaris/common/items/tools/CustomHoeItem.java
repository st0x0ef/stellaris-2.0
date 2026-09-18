package org.exodusstudio.stellaris.common.items.tools;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ToolMaterial;

public class CustomHoeItem extends HoeItem {
    public CustomHoeItem(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, properties);
    }
}
