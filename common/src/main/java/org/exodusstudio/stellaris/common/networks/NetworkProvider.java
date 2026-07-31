package org.exodusstudio.stellaris.common.networks;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface NetworkProvider<N extends Network> {
    @Nullable N getNetwork(@Nullable Direction direction);
}
