package org.exodusstudio.stellaris.common.items;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.components.GeigerCounterComponent;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.SoundRegistry;
import org.exodusstudio.stellaris.common.utils.RadiationUtils;

import java.util.List;
import java.util.function.Consumer;

public class GeigerCounterItem extends Item implements CustomSwingItem {

    private final int maxRadius;

    public GeigerCounterItem(Properties properties, int maxRadius) {
        super(properties);
        this.maxRadius = maxRadius;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) {
            return InteractionResult.FAIL;
        }

        if(level instanceof ServerLevel serverLevel) {
            ItemStack stack = player.getItemInHand(usedHand);
            GeigerCounterComponent geigerCounter = stack.getOrDefault(DataComponentsRegistry.GEIGER_COUNTER.get(), new GeigerCounterComponent(GeigerCounterComponent.Type.TOTAL_RADIATION, 0));

            if(player.isShiftKeyDown()) {

                GeigerCounterComponent.Type newType = GeigerCounterComponent.Type.values()[(geigerCounter.type().ordinal() + 1) % GeigerCounterComponent.Type.values().length];

                player.sendOverlayMessage(Component.translatable("text.stellaris.geiger_counter." + newType).withStyle(ChatFormatting.GREEN));

                stack.set(DataComponentsRegistry.GEIGER_COUNTER.get(), new GeigerCounterComponent(newType, geigerCounter.radius()));
                return InteractionResult.FAIL;
            }

            List<Pair<BlockPos, Float>> radiationBlocks = RadiationUtils.getRadioactiveBlocksInRadius(serverLevel,
                    player.blockPosition(), geigerCounter.radius());

            float radiationValue = geigerCounter.type().calculate(radiationBlocks);
            double avgRadiation = radiationBlocks.stream().mapToDouble(Pair::getSecond).average().orElse(0.0f);

            SoundEvent geigerSound = getGeigerSound(avgRadiation);

            level.playSound(null, player.blockPosition(), geigerSound, SoundSource.PLAYERS, 0.5f, 1.0f);

            player.sendOverlayMessage(Component.translatable("text.stellaris.geiger_counter." + geigerCounter.type() + ".result", geigerCounter.radius(), radiationValue));
            return InteractionResult.CONSUME;

        }


        return super.use(level, player, usedHand);
    }


    public boolean onSwing(InteractionHand hand, LivingEntity owner) {
        Level level = owner.level();
        ItemStack stack = owner.getItemInHand(hand);
        if(!level.isClientSide() && level instanceof ServerLevel && owner instanceof Player player) {

            GeigerCounterComponent geigerCounter = stack.getOrDefault(DataComponentsRegistry.GEIGER_COUNTER.get(), new GeigerCounterComponent(GeigerCounterComponent.Type.TOTAL_RADIATION, 0));


            int radiusDelta = player.isShiftKeyDown() ? -1 : 1;
            int newRadius = Math.clamp(geigerCounter.radius() + radiusDelta, 1, this.maxRadius);
            stack.set(DataComponentsRegistry.GEIGER_COUNTER.get(), new GeigerCounterComponent(geigerCounter.type(), newRadius));
            player.sendOverlayMessage(Component.translatable("tooltip.stellaris.geiger_counter.radius", newRadius).withStyle(ChatFormatting.GREEN));
            return true;
        }

        return false;

    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        GeigerCounterComponent geigerCounter = stack.getOrDefault(DataComponentsRegistry.GEIGER_COUNTER.get(), new GeigerCounterComponent(GeigerCounterComponent.Type.TOTAL_RADIATION, 0));

        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.geiger_counter").withStyle(ChatFormatting.GRAY));
        tooltipAdder.accept(Component.literal("Type : ")
                .append(Component.translatable("text.stellaris.geiger_counter." + geigerCounter.type())
                .append(Component.literal(" | "))
                .append(Component.translatable("tooltip.stellaris.geiger_counter.radius", geigerCounter.radius()))).withStyle(ChatFormatting.GRAY));
    }

    public static SoundEvent getGeigerSound(double avgRadiation) {
        if(avgRadiation < 40f) {
            return SoundRegistry.GEIGER_LOW.get();
        } else if(avgRadiation < 120f) {
            return SoundRegistry.GEIGER_MEDIUM.get();
        } else {
            return SoundRegistry.GEIGER_HIGH.get();
        }
    }
}
