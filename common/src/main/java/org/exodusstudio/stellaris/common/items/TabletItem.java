package org.exodusstudio.stellaris.common.items;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.data.wiki.EntryInfo;
import org.exodusstudio.stellaris.common.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.common.menus.WikiApplicationMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.network.packets.OpenWikiEntry;
import org.exodusstudio.stellaris.common.registries.AdvancementTriggerRegistry;

import java.util.Map;

public class TabletItem extends Item {

    public TabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        if(player.isShiftKeyDown()){
            return InteractionResult.PASS;
        }

        NetworkManager.sendToServer(new OpenMenuPacket("main_tablet"));

        if(player instanceof ServerPlayer serverPlayer){
            AdvancementTriggerRegistry.TABLET_USED.get().trigger(serverPlayer);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        if(player instanceof ServerPlayer serverPlayer){
            if(!player.isShiftKeyDown()) return InteractionResult.PASS;

            BlockState blockUsedOn =  context.getLevel().getBlockState(context.getClickedPos());
            Identifier blockId = blockUsedOn.getBlock().arch$registryName();
            if(blockId == null) {
                return super.useOn(context);
            }

            //First we check if we have the block
            ResourceKey<Block> blockResourceKey = ResourceKey.create(Registries.BLOCK, blockId);
            if(WikiPacks.EntryInfoPack.BLOCK_ENTRY_RESOLVER.containsKey(blockResourceKey)) {
                MenuRegistry.openExtendedMenu(serverPlayer, WikiApplicationMenu.createProvider(WikiPacks.EntryInfoPack.BLOCK_ENTRY_RESOLVER.get(blockResourceKey)));
                return InteractionResult.SUCCESS;
            }

            //Then we check if we have a tag associated
            for(Map.Entry<TagKey<Block>, Identifier> entry : WikiPacks.EntryInfoPack.TAG_ENTRY_RESOLVER.entrySet()) {
                if(blockUsedOn.is(entry.getKey())) {
                    MenuRegistry.openExtendedMenu(serverPlayer, WikiApplicationMenu.createProvider(entry.getValue()));
                    return InteractionResult.SUCCESS;
                }
            }

            //Then we do some weird things
            WikiPacks.ENTRY_COMPONENTS.forEach((entryId, entry) -> {

                String entryName = entryId.getPath().split("/")[1];

                if(entryId.toString().contains(blockId.getPath()) || blockId.getPath().contains(entryName)) {
                    MenuRegistry.openExtendedMenu(serverPlayer, WikiApplicationMenu.createProvider(entryId));
                }
            });
        }

        return super.useOn(context);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {

        if(other.isEmpty() || other.getItem().arch$registryName() == null) {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }

        Identifier otherId = other.getItem().arch$registryName();

        for(Map.Entry<Identifier, EntryInfo> entry :  WikiPacks.ENTRY_COMPONENTS.entrySet()) {
            if(entry.getKey().toString().contains(otherId.getPath())) {
                NetworkManager.sendToServer(new OpenWikiEntry(entry.getKey()));
                return true;
            }
        }


        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }
}
