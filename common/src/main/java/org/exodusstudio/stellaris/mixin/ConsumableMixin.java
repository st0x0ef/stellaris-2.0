package org.exodusstudio.stellaris.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public class ConsumableMixin {


    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    public void canConsume(LivingEntity entity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.has(DataComponents.FOOD) && !stack.is(TagsRegistry.ItemTags.CAN)
                && !OxygenUtils.isOxygenated(entity.level(), entity.blockPosition())) {
            cir.setReturnValue(false);
            return;
        }

        if (stack.is(Items.POTION)) {
            if (entity.getMainHandItem().is(Items.POTION) && entity.getOffhandItem().is(TagsRegistry.ItemTags.CAN)) {
                cir.setReturnValue(false);
            } else if (entity.getOffhandItem().is(Items.POTION) && entity.getMainHandItem().is(TagsRegistry.ItemTags.CAN)) {
                cir.setReturnValue(false);
            }
        } else if (stack.is(TagsRegistry.ItemTags.CAN)) {
            if (!entity.getMainHandItem().is(Items.POTION) && !entity.getOffhandItem().is(Items.POTION)) {
                cir.setReturnValue(false);
            }
        }
    }
}
