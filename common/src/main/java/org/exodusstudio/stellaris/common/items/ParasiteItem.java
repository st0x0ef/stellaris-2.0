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
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.components.TimerComponents;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ParasiteItem extends Item {
    private final static TimerComponents DEFAULT_TIMER = new TimerComponents(5 * 60); // 5 minutes, TODO : make configurable

    private long lastTime = -1;
    private double time;

    public ParasiteItem(Properties properties) {
        super(properties.stacksTo(1).component(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (entity instanceof LivingEntity livingEntity) {
            if (lastTime == -1) {
                lastTime = level.getGameTime();
                return;
            }
            TimerComponents timer = stack.getOrDefault(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER);
            if (timer.timeLeft() > 0) {
                long elapsed = level.getGameTime() - lastTime;
                if (elapsed < 0) elapsed += 24000;

                time += elapsed / 20.0;
                lastTime = level.getGameTime();

                if (time > 1.0) {
                    stack.update(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER, TimerComponents::tick);
                    time -= 1.0;
                }
            } else {
                livingEntity.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED), 5 * 60 * 20, 0)); // 5 minutes of infected effect TODO : make configurable
                stack.consume(1, livingEntity);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        TimerComponents timer = stack.getOrDefault(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER);
        int minutes =  timer.timeLeft() / 60;
        int seconds = timer.timeLeft() % 60;
        if (minutes > 0) {
            tooltipAdder.accept(Component.literal("Propagation in " + minutes + "min " + seconds + "s").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipAdder.accept(Component.literal("Propagation in ").withStyle(ChatFormatting.GRAY).append(Component.literal(+ seconds + "s").withStyle(ChatFormatting.RED)));
        }
    }
}
