package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.exodusstudio.stellaris.common.blocks.MultiblockProxyBlock;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.callback.JadeRayTraceCallback;
import snownee.jade.api.config.IWailaConfig;

public record ProxyAsMainCallback(IWailaClientRegistration registration) implements JadeRayTraceCallback {

    public static final Identifier UID = IdentifierUtils.id("proxy_as_main");

    @Override
    public @Nullable Accessor<?> onRayTrace(HitResult hitResult, @Nullable Accessor<?> accessor, @Nullable Accessor<?> originalAccessor) {
        if (!(accessor instanceof BlockAccessor block) || !(block.getBlock() instanceof MultiblockProxyBlock proxy) || !IWailaConfig.get().plugin().get(UID)) {
            return accessor;
        }
        Level level = block.getLevel();
        BlockPos mainPos = proxy.getControllerPos(block.getPosition(), block.getBlockState());
        BlockState mainState = level.getBlockState(mainPos);
        if (mainState.isAir()) {
            return accessor;
        }
        return registration.blockAccessor()
                .from(block)
                .hit(block.getHitResult().withPosition(mainPos))
                .blockState(mainState)
                .blockEntity(() -> level.getBlockEntity(mainPos))
                .build();
    }
}
