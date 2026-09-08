package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.assistant.AssistantManager;
import org.exodusstudio.stellaris.common.blocks.entities.machines.LaboratoryBlockEntity;
import org.exodusstudio.stellaris.common.components.PathogenStorageComponent;
import org.exodusstudio.stellaris.common.data.assistant.AssistantTrigger;
import org.exodusstudio.stellaris.common.menus.laboratory.ResearchMenu;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;

public record InfectionResearchPacket(BlockPos laboratoryPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<InfectionResearchPacket> TYPE = new CustomPacketPayload.Type<>(IdentifierUtils.id("infection_research"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfectionResearchPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, InfectionResearchPacket::laboratoryPos,
            InfectionResearchPacket::new
    );

    public static void handle(InfectionResearchPacket data, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (!(context.getPlayer() instanceof ServerPlayer player)) {
                return;
            }

            if (!(player.containerMenu instanceof ResearchMenu menu)
                    || !menu.blockEntity.getBlockPos().equals(data.laboratoryPos())) {
                return;
            }

            LaboratoryBlockEntity laboratory = menu.blockEntity;

            if (laboratory.isRemoved()
                    || laboratory.getLevel() != player.level()
                    || !player.isWithinBlockInteractionRange(laboratory.getBlockPos(), 4.0F)) {
                return;
            }

            if (laboratory.progressTickLeft > 0) {
                return;
            }

            ItemStack storageCell = laboratory.getItem(0);
            if (!storageCell.is(ItemsRegistry.PATHOGEN_STORAGE_CELL.get())) {
                return;
            }

            int parasiteStored = storageCell
                    .getOrDefault(DataComponentsRegistry.PATHOGEN_STORED.get(), PathogenStorageComponent.DEFAULT)
                    .stored();

            int currentStage = MoonLoreUtils.getResearchProgressionStage(player);
            boolean success = MoonLoreUtils.tryIncrementResearchProgressionStageIfLucky(player, parasiteStored);

            if (success) {
                int nextStage = Math.min(currentStage + 1, MoonLoreUtils.MAX_STAGE);
                laboratory.setItem(1, MoonLoreUtils.getSdCardForStage(nextStage));
                player.stellaris$saveDataAttachments(MoonLoreUtils.MOON_LORE_PROGRESSION, nextStage);
            }

            announceResearch(player, currentStage, success);

            ItemStack spentCell = storageCell.copy();
            spentCell.set(DataComponentsRegistry.PATHOGEN_STORED.get(), PathogenStorageComponent.DEFAULT);
            laboratory.setItem(0, spentCell);

            laboratory.progressTickLeft = Stellaris.CONFIG.parasiteConfig.researchDelay;

            NetworkManager.sendToPlayer(player, new InfectionResearchResultPacket(success));
        });
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
