package org.exodusstudio.stellaris.common.items.infection;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.common.components.TimerComponent;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class ParasiteItem extends Item {
    private final static TimerComponent DEFAULT_TIMER = new TimerComponent(5 * 60);

    private record TickData(long lastTime, double accumulatedTime) {}
    private final Map<ItemStack, TickData> tickDataMap = new WeakHashMap<>();

    public ParasiteItem(Properties properties) {
        super(properties.stacksTo(1).component(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (entity instanceof LivingEntity) {
            TickData data = tickDataMap.get(stack);
            long currentTime = level.getGameTime();

            if (data == null) {
                tickDataMap.put(stack, new TickData(currentTime, 0));
                return;
            }

            TimerComponent timer = stack.getOrDefault(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER);
            if (timer.timeLeft() > 0) {
                long elapsed = currentTime - data.lastTime();
                if (elapsed < 0) elapsed += 24000;

                double newTime = data.accumulatedTime() + elapsed / 20.0;

                if (newTime > 1.0) {
                    stack.update(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER, TimerComponent::tick);
                    newTime -= 1.0;
                }

                tickDataMap.put(stack, new TickData(currentTime, newTime));
            } else {
                if (entity instanceof Player player && !MoonLoreUtils.isPlayerImmunisedToInfection(player)) {
                    player.addEffect(new MobEffectInstance(EffectsRegistry.getHolder(EffectsRegistry.INFECTED), 5 * 60 * 20, 0));
                    stack.consume(1, player);
                }
            }
        }
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        super.onDestroyed(itemEntity);
        tickDataMap.remove(itemEntity.getItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        TimerComponent timer = stack.getOrDefault(DataComponentsRegistry.TIMER.get(), DEFAULT_TIMER);
        int minutes = timer.timeLeft() / 60;
        int seconds = timer.timeLeft() % 60;
        if (minutes > 0) {
            tooltipAdder.accept(Component.literal("Propagation in " + minutes + "min " + seconds + "s").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipAdder.accept(Component.literal("Propagation in ").withStyle(ChatFormatting.GRAY).append(Component.literal(seconds + "s").withStyle(ChatFormatting.RED)));
        }
    }
}
