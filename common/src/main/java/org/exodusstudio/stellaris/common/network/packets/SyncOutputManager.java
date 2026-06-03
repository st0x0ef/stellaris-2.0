package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.FluidOutputable;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jspecify.annotations.Nullable;

public abstract class SyncOutputManager implements CustomPacketPayload {

    BlockPos pos;
    Direction direction;
    FluidStack stack;


    public SyncOutputManager(BlockPos pos, Direction direction, FluidStack stack) {
        this.pos = pos;
        this.direction = direction;
        this.stack = stack;
    }

    public static void handle(SyncOutputManager packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Player player = context.getPlayer();
            if(player.level().getBlockEntity(packet.pos) instanceof FluidOutputable blockEntity) {
                if(packet.stack.isEmpty()) {
                    blockEntity.getFluidOutputManager().outputs.remove(packet.direction);
                    return;
                }
                blockEntity.getFluidOutputManager().outputs.put(packet.direction, packet.stack);
            }
        });
    }

    public static class C2S extends SyncOutputManager {

        public static final StreamCodec<RegistryFriendlyByteBuf, SyncOutputManager> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, (s) -> s.pos,
                Direction.STREAM_CODEC, (s) -> s.direction,
                FluidStack.STREAM_CODEC, (s) -> s.stack,
                SyncOutputManager.C2S::new
        );

        public static final CustomPacketPayload.Type<SyncOutputManager> TYPE = new CustomPacketPayload.Type<>(IdentifierUtils.id("sync_output_manager"));

        public C2S(BlockPos pos, Direction direction, @Nullable FluidStack stack) {
            super(pos, direction, stack);
        }


        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static class S2C extends SyncOutputManager {

        public static final StreamCodec<RegistryFriendlyByteBuf, SyncOutputManager> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, (s) -> s.pos,
                Direction.STREAM_CODEC, (s) -> s.direction,
                FluidStack.STREAM_CODEC, (s) -> s.stack,
                SyncOutputManager.S2C::new
        );

        public static final CustomPacketPayload.Type<SyncOutputManager> TYPE = new CustomPacketPayload.Type<>(IdentifierUtils.id("sync_output_manager_s2c"));

        public S2C(BlockPos pos, Direction direction, FluidStack stack) {
            super(pos, direction, stack);
        }



        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
