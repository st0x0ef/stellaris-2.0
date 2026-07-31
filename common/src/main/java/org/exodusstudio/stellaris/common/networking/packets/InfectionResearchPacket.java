package org.exodusstudio.stellaris.common.networking.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.assistant.AssistantManager;
import org.exodusstudio.stellaris.common.blocks.entities.machines.LaboratoryBlockEntity;
import org.exodusstudio.stellaris.common.data.assistant.AssistantTrigger;
import org.exodusstudio.stellaris.common.components.PathogenStorageComponent;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
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
            int currentStage = MoonLoreUtils.getResearchProgressionStage(player);

            if (data.success()) {
                int nextStage = currentStage + 1;
                ItemStack sdCard = MoonLoreUtils.getSdCardForStage(nextStage);
                laboratory.setItem(1, sdCard);
                player.stellaris$saveDataAttachments(MoonLoreUtils.MOON_LORE_PROGRESSION, nextStage);
            }

            if (player instanceof ServerPlayer serverPlayer) {
                announceResearch(serverPlayer, currentStage, data.success());
            }

            ItemStack slot0ItemToReturn = laboratory.getItem(0).copy();
            slot0ItemToReturn.set(DataComponentsRegistry.PATHOGEN_STORED.get(), PathogenStorageComponent.DEFAULT);
            laboratory.setItem(0, slot0ItemToReturn);
        }
    }


    private static void announceResearch(ServerPlayer player, int stageBeforeResearch, boolean success) {
        if (stageBeforeResearch >= MoonLoreUtils.MAX_STAGE) {
            AssistantManager.fire(player, AssistantTrigger.RESEARCH_COMPLETE);
            return;
        }

        if (success) {
            int reachedStage = stageBeforeResearch + 1;

            if (reachedStage >= MoonLoreUtils.MAX_STAGE) {
                AssistantManager.fire(player, AssistantTrigger.RESEARCH_COMPLETE);
            } else {
                AssistantManager.fire(player, AssistantTrigger.RESEARCH_SUCCESS,
                        reachedStage, MoonLoreUtils.getParasitesNeededToLeaveStage(reachedStage));
            }
        } else {
            AssistantManager.fire(player, AssistantTrigger.RESEARCH_FAILURE,
                    Math.max(0, stageBeforeResearch), MoonLoreUtils.getParasitesNeededToLeaveStage(stageBeforeResearch));
        }
    }

    @Override
    public CustomPacketPayload.Type<InfectionResearchPacket> type() {
        return TYPE;
    }
}
