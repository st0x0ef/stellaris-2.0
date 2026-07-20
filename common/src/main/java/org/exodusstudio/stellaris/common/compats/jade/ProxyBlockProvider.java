package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.MultiblockProxyBlock;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;

/**
 * Makes a multiblock proxy block appear as its controller block in the Jade overlay: replaces the
 * icon and the object-name line with those of the main block. Purely client-side — the controller's
 * state is always loaded (it is adjacent to the proxy).
 */
public enum ProxyBlockProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Identifier UID = IdentifierUtils.id("proxy_as_main");

    @Override
    public @Nullable Element getIcon(BlockAccessor accessor, IPluginConfig config, @Nullable Element currentIcon) {
        BlockState mainState = getControllerState(accessor);
        if (mainState == null) {
            return null;
        }
        return JadeUI.item(new ItemStack(mainState.getBlock()));
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState mainState = getControllerState(accessor);
        if (mainState == null) {
            return;
        }
        // Match Jade's own ObjectNameProvider, which styles the name with the theme's title color
        // (otherwise the replaced text falls back to the default gray tooltip color).
        tooltip.replace(JadeIds.CORE_OBJECT_NAME, IThemeHelper.get().title(mainState.getBlock().getName()));
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    /**
     * @return the controller block state, or {@code null} if the block isn't a proxy or the
     * controller is missing (e.g. mid-removal).
     */
    private static @Nullable BlockState getControllerState(BlockAccessor accessor) {
        if (!(accessor.getBlock() instanceof MultiblockProxyBlock proxy)) {
            return null;
        }
        BlockPos mainPos = proxy.getControllerPos(accessor.getPosition(), accessor.getBlockState());
        BlockState mainState = accessor.getLevel().getBlockState(mainPos);
        return mainState.isAir() ? null : mainState;
    }
}
