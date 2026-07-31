package org.exodusstudio.stellaris.common.networking.packets;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.BaseFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

public record SyncFluidPacketWithoutDirection(FluidAmountMapDataComponent fluid, int tank, BlockPos pos) implements CustomPacketPayload {

    public static final Type<SyncFluidPacketWithoutDirection> TYPE = new Type<>(IdentifierUtils.id("fluid_sync_packet_without_direction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFluidPacketWithoutDirection> STREAM_CODEC = StreamCodec.composite(
            FluidAmountMapDataComponent.STREAM_CODEC, SyncFluidPacketWithoutDirection::fluid,
            ByteBufCodecs.VAR_INT, SyncFluidPacketWithoutDirection::tank,
            BlockPos.STREAM_CODEC, SyncFluidPacketWithoutDirection::pos,
            SyncFluidPacketWithoutDirection::new
    );

    @Override
    public @NotNull Type<SyncFluidPacketWithoutDirection> type() {
        return TYPE;
    }

    public static void handle(final SyncFluidPacketWithoutDirection data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.getBlockEntity(data.pos) instanceof FluidProvider.BLOCK fluidProvider) {
                if (fluidProvider.getFluidTank(null) instanceof BaseFluidStorage fluidStorage) {
                    fluidStorage.setFluidInTank(data.tank, FluidStack.create(data.fluid.getAsFluidStack(0), data.fluid.getAmount(0)));
                }
                else if (fluidProvider.getFluidTank(null) instanceof SingleFluidStorage fluidStorage) {
                    fluidStorage.setFluidInTank(FluidStack.create(data.fluid.getAsFluidStack(0), data.fluid.getAmount(0)));
                }
            }
        });
    }
}
