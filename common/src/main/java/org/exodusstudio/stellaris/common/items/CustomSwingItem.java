package org.exodusstudio.stellaris.common.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public interface CustomSwingItem {

    boolean onSwing(InteractionHand hand, LivingEntity owner);
}
