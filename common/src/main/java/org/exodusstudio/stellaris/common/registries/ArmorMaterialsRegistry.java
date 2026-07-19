package org.exodusstudio.stellaris.common.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.EnumMap;

public class ArmorMaterialsRegistry {
    private static final ResourceKey<Registry<EquipmentAsset>> key = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));
    public static final ArmorMaterial SPACE_SUIT = new ArmorMaterial(
            10,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 0);
            }),
            10,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            2.0F, 0.0F,
            TagsRegistry.ItemTags.TITANIUM_INGOTS,
            ResourceKey.create(key, IdentifierUtils.id("space_suit")));
}
