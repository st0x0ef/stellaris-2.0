package org.exodusstudio.stellaris.common.items;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.data.wiki.WikiPacks;
import org.exodusstudio.stellaris.client.screens.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.client.screens.tablet.application.wiki.WikiApplicationScreen;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.menus.WikiApplicationMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.network.packets.OpenWikiEntry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

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

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        if(player instanceof ServerPlayer serverPlayer){
            if(!player.isShiftKeyDown()) return InteractionResult.PASS;

            BlockState blockUsedOn =  context.getLevel().getBlockState(context.getClickedPos());

            Identifier identifier = blockUsedOn.getBlock().arch$registryName();
            WikiPacks.ENTRY_COMPONENTS.forEach((entryId, entry) -> {
                if(entryId.toString().contains(identifier.getPath())) {
                    MenuRegistry.openExtendedMenu(serverPlayer, WikiApplicationMenu.createProvider(entryId));
                }
            });


        }

        return super.useOn(context);
    }
}
