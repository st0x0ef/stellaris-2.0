package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.InventorySaver;


public record SyncBlenderItems(InventorySaver inventorySaver, BlockPos pos) implements CustomPacketPayload {

    public static final Type<SyncBlenderItems> TYPE = new Type<>(IdentifierUtils.id("sync_blender_items"));



    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlenderItems> STREAM_CODEC = StreamCodec.composite(
            InventorySaver.STREAM_CODEC, SyncBlenderItems::inventorySaver,
            BlockPos.STREAM_CODEC, SyncBlenderItems::pos,
            SyncBlenderItems::new
    );


    public static void handle(SyncBlenderItems packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player != null && player.level() != null) {
                if(player.level().getBlockEntity(packet.pos()) instanceof BlenderBlockEntity blockEntity) {
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
