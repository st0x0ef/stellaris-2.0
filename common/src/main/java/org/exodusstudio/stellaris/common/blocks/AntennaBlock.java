package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.blocks.base.BaseMachineBlock;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.exodusstudio.stellaris.common.menus.AntennaMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AntennaBlock extends BaseMachineBlock {

    public AntennaBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AntennaBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.ANTENNA.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }


    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(AntennaBlock::new);
    }


    @Nullable
    @Override
    protected ExtendedMenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AntennaBlockEntity padCreatorBlock && !level.isClientSide()) {

            AntennaSavedData antennaSavedData = AntennaSavedData.getSavedAntennas(level.getServer());
            Antenna antenna = antennaSavedData.getAntenna(padCreatorBlock.launchPadId);
            Stellaris.LOG.info(" " + antenna);

            return new ExtendedMenuProvider() {
                @Override
                public void saveExtraData(FriendlyByteBuf buf) {
                    buf.writeBlockPos(blockEntity.getBlockPos());
                    buf.writeNullable(padCreatorBlock.launchPadId, (buffer, uuid) -> buffer.writeUUID(uuid));

                    buf.writeNullable(antenna, Antenna.STREAM_CODEC);

                }

                @Override
                public Component getDisplayName() {
                    return padCreatorBlock.getDisplayName();
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

                    buf.writeBlockPos(blockEntity.getBlockPos());
                    buf.writeNullable(padCreatorBlock.launchPadId, (buffer, uuid) -> buffer.writeUUID(uuid));
                    buf.writeNullable(antenna, Antenna.STREAM_CODEC);
                    return AntennaMenu.create(containerId, inventory, buf);
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {

            AntennaSavedData antennaSavedData = AntennaSavedData.getSavedAntennas(level.getServer());
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AntennaBlockEntity antennaBlock) {
                if (antennaBlock.launchPadId == null || antennaSavedData.getAntenna(antennaBlock.launchPadId).ownerUUID.equals(player.getGameProfile().id())) {
                    super.useWithoutItem(state, level, pos, player, hitResult);
                } else {
                    // If the player is not the owner of the launch pad, do not open the menu
                    // You can also send a message to the player if needed
                    player.displayClientMessage(Component.translatable("message.stellaris.not_owner_of_launch_pad"), false);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

}
