package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.InventorySaver;


public record SyncBlenderState(InventorySaver inventorySaver, BlockPos pos, int blendTime) implements CustomPacketPayload {

    public static final Type<SyncBlenderState> TYPE = new Type<>(IdentifierUtils.id("sync_blender_items"));



    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlenderState> STREAM_CODEC = StreamCodec.composite(
            InventorySaver.STREAM_CODEC, SyncBlenderState::inventorySaver,
            BlockPos.STREAM_CODEC, SyncBlenderState::pos,
            ByteBufCodecs.INT, SyncBlenderState::blendTime,
            SyncBlenderState::new
    );


    public static void handle(SyncBlenderState packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player != null && player.level() != null) {
                if(player.level().getBlockEntity(packet.pos()) instanceof BlenderBlockEntity blockEntity) {
                    for (int slot = 0; slot < blockEntity.getContainerSize(); slot++) {
                        blockEntity.setItem(slot, ItemStack.EMPTY);
                    }
                    blockEntity.setBlendTime(packet.blendTime());
                    packet.inventorySaver.savedItems().forEach((slot) -> blockEntity.setItem(slot.slot(), slot.itemStack()));
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
