package org.exodusstudio.stellaris.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.components.TimerComponents;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ParasiteItem extends Item {
    private final static TimerComponents DEFAULT_TIMER = new TimerComponents(5 * 60 * 20); // 5 minutes, TODO : make configurable

    public ParasiteItem(Properties properties) {
        super(properties.component(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (!level.isClientSide()) {
            TimerComponents timer = stack.getOrDefault(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER);
            if (timer.timeLeft() > 0) {
                stack.set(DataComponentsRegistry.TIMER.get(), timer.tick(2)); // TODO : find why we need to tick 2x faster
            } else {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED)));
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        TimerComponents timer = stack.getOrDefault(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER);
        int minutes = (timer.timeLeft() / 20) / 60;
        int seconds = (timer.timeLeft() / 20) % 60;
        if (minutes > 0) {
            tooltipAdder.accept(Component.literal("Propagation in " + minutes + "min " + seconds + "s").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipAdder.accept(Component.literal("Propagation in ").withStyle(ChatFormatting.GRAY).append(Component.literal(+ seconds + "s").withStyle(ChatFormatting.RED)));
        }
    }
}
