package org.exodusstudio.stellaris.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.Consumable;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public class ConsumableMixin {


    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    public void canConsume(LivingEntity entity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Item waterBottle = PotionContents.createItemStack(Items.POTION, Potions.WATER).getItem();
        if (stack.is(waterBottle)) {
            if (entity.getMainHandItem().equals(stack) && entity.getOffhandItem().is(TagsRegistry.ItemTags.CAN)) {
                cir.setReturnValue(false);
            } else if (entity.getOffhandItem().equals(stack) && entity.getMainHandItem().is(TagsRegistry.ItemTags.CAN)) {
                cir.setReturnValue(false);
            }
        } else if (stack.is(TagsRegistry.ItemTags.CAN)) {
            if (!entity.getMainHandItem().is(waterBottle) && !entity.getOffhandItem().is(waterBottle)) {
                cir.setReturnValue(false);
            }
        }
    }
}
