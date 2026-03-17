package org.exodusstudio.stellaris.common.items.modules.space_suit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitItem;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class DamageProtectionModuleItem extends Item implements SpaceSuitModule.DamageProtectionModule {
    public ArmorMaterial equivalentMaterial;
    public String materialName;

    public DamageProtectionModuleItem(Properties properties, ArmorMaterial equivalentMaterial, String materialName) {
        super(properties);
        this.equivalentMaterial = equivalentMaterial;
        this.materialName = materialName;
    }

    @Override
    public boolean canBeAppliedToSpaceSuitPart(ItemStack part) {
        return part.getItem() instanceof SpaceSuitItem;
    }

    @Override
    public ArmorMaterial getArmorMaterialEquivalent() {
        return this.equivalentMaterial;
    }

    @Override
    public String getMaterialName() {
        return this.materialName;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Equivalent to " + materialName + " armor").withColor(Utils.getMinecraftColor("gray")));
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_spacesuit_module").withColor(Utils.getMinecraftColor("gray")));
    }
}
