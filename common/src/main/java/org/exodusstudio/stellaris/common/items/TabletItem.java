package org.exodusstudio.stellaris.common.items;

import dev.architectury.networking.NetworkManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;

public class TabletItem extends Item {

    public TabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        NetworkManager.sendToServer(new OpenMenuPacket("main_tablet"));

        return InteractionResult.SUCCESS;
    }


}
