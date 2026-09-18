package org.exodusstudio.stellaris.common.items.tools;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ToolMaterial;

public class CustomAxeItem extends AxeItem {
    public CustomAxeItem(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, properties);
    }
}
