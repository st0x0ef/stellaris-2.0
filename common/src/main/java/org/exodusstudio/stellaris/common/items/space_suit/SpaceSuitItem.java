package org.exodusstudio.stellaris.common.items.space_suit;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.ArmorMaterialsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class SpaceSuitItem extends Item {
    private final ArmorType armorType;

    public SpaceSuitItem(Properties properties, ArmorType armorType) {
        super(properties.humanoidArmor(ArmorMaterialsRegistry.SPACE_SUIT, armorType));
        this.armorType = armorType;
    }

    public ArmorType getArmorType() {
        return this.armorType;
    }

    public void refreshAttributes(ItemStack stack) {
        SpaceSuitModule.DamageProtectionModule protection =
                ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.DamageProtectionModule.class);

        ArmorMaterial base = ArmorMaterialsRegistry.SPACE_SUIT;
        int armor = protection != null
                ? protection.getArmorMaterialEquivalent().defense().getOrDefault(armorType, 0)
                : base.defense().getOrDefault(armorType, 0);

        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(armorType.getSlot());
        Identifier id = Identifier.withDefaultNamespace("armor." + armorType.getName());

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR,
                        new AttributeModifier(id, armor, AttributeModifier.Operation.ADD_VALUE),
                        slotGroup)
                .add(Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(id, base.toughness(), AttributeModifier.Operation.ADD_VALUE),
                        slotGroup)
                .build());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        SpaceSuitModule.DamageProtectionModule damageProtectionModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.DamageProtectionModule.class);
        if (damageProtectionModule != null) {
            tooltipAdder.accept(Component.literal("-- Damage Protection Module --").withColor(Utils.getMinecraftColor("red")));
            tooltipAdder.accept(Component.literal("Equivalent to " + damageProtectionModule.getMaterialName() +  " armor").withColor(Utils.getMinecraftColor("red")));
        }
    }
}
