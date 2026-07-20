package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.exodusstudio.stellaris.common.blocks.MultiblockProxyBlock;
import org.exodusstudio.stellaris.common.blocks.entities.FlagBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum FlagOwnerProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Identifier UID = IdentifierUtils.id("flag_owner");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(getFlagBlockEntity(accessor) instanceof FlagBlockEntity flag)) {
            return;
        }
        ResolvableProfile profile = flag.getGameProfile();
        if (profile == null) {
            return;
        }
        String name = profile.name().orElse("");
        if (name.isBlank()) {
            return;
        }
        tooltip.add(Component.translatable("jade.stellaris.flag_owner", Component.literal(name))
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    private static BlockEntity getFlagBlockEntity(BlockAccessor accessor) {
        if (accessor.getBlock() instanceof MultiblockProxyBlock proxy) {
            BlockPos mainPos = proxy.getControllerPos(accessor.getPosition(), accessor.getBlockState());
            return accessor.getLevel().getBlockEntity(mainPos);
        }
        return accessor.getBlockEntity();
    }
}
