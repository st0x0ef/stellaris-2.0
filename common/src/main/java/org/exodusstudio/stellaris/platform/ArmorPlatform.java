package org.exodusstudio.stellaris.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArmorPlatform {
    @FunctionalInterface
    public interface ArmorFactory {
        HumanoidModel<HumanoidRenderState> create(ModelPart root, EquipmentSlot slot, ItemStack stack, HumanoidModel<HumanoidRenderState> parentModel);
    }

    @ExpectPlatform
    public static void registerArmor(ModelLayerLocation layer, ArmorFactory factory, Identifier texture, Item... items) {
        throw new AssertionError();
    }
}
