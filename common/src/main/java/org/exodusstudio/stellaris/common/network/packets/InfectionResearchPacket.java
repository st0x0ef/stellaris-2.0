package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.LaboratoryBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;

public record InfectionResearchPacket(BlockPos laboratoryPos, boolean success) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<InfectionResearchPacket> TYPE = new CustomPacketPayload.Type<>(IdentifierUtils.id("infection_research"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfectionResearchPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, InfectionResearchPacket::laboratoryPos,
            ByteBufCodecs.BOOL, InfectionResearchPacket::success,
            InfectionResearchPacket::new
    );

    public static void handle(InfectionResearchPacket data, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        LaboratoryBlockEntity laboratory = (LaboratoryBlockEntity) player.level().getBlockEntity(data.laboratoryPos());

        if (laboratory != null) {
            if (data.success()) {
                int nextStage = MoonLoreUtils.getResearchProgressionStage(player) + 1;
                ItemStack sdCard = MoonLoreUtils.getSdCardForStage(nextStage);
                laboratory.setItem(1, sdCard);
                player.stellaris$saveDataAttachments(MoonLoreUtils.MOON_LORE_PROGRESSION, nextStage);
            }

            laboratory.setItem(0, ItemStack.EMPTY);
        }
    }


    @Override
    public CustomPacketPayload.Type<InfectionResearchPacket> type() {
        return TYPE;
    }
}
