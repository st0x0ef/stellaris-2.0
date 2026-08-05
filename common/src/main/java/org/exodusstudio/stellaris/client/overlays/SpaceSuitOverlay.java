package org.exodusstudio.stellaris.client.overlays;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpaceSuitOverlay {


    public static int PADDING = 5;

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null && Utils.isLivingInSpaceSuit(player)) {
            Vector2i position = new Vector2i(PADDING, PADDING); // Starting position for rendering modules

            HashMap<SpaceSuitModule, ItemStack> moduleCounts = new HashMap<>();

            for(EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack equipmentStack = player.getItemBySlot(slot);
                ModuleUtils.getSpaceSuitModules(equipmentStack).getModules().forEach(module -> moduleCounts.put(module, equipmentStack));
            }

            List<SpaceSuitModule> modules = new ArrayList<>(moduleCounts.keySet());

            modules.sort((SpaceSuitModule m1, SpaceSuitModule m2) -> {
                int order1 = m1.renderPriority();
                int order2 = m2.renderPriority();
                return Integer.compare(order2, order1);
            });

            Vector2i energySize = renderEnergy(graphics, position, player.getItemBySlot(EquipmentSlot.HEAD));
            position.add(energySize).add(PADDING, PADDING);

            for(SpaceSuitModule module : modules) {

                Vector2i moduleSize = module.renderStackedGui(graphics, deltaTracker, player, moduleCounts.get(module), position.x, position.y);

                if(position.x != moduleSize.x + position.x)
                    position.x = moduleSize.x + position.x + PADDING;

                if(position.y != moduleSize.y + position.y)
                    position.y = moduleSize.y + position.y + PADDING;
            }
        }
    }

    public static Vector2i renderEnergy(GuiGraphicsExtractor graphics, Vector2i pos, ItemStack helmetStack) {

        if(!helmetStack.isEmpty() && helmetStack.getItem() instanceof SpaceSuitHelmet helmet) {

            UniversalEnergyStorage energyStorage =  helmet.getEnergy(helmetStack);
            if(energyStorage != null) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, GUISprites.SPACESUIT_ENERGY_BAR, pos.x, pos.y, 0, 0, 37, 10, 37, 10);

                int i = Mth.ceil(Mth.clamp((float) energyStorage.getEnergy() / energyStorage.getMaxEnergy(),
                        0.0F, 1.0F) * (24 - 1));

                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, GUISprites.SPACESUIT_FULL_BAR_SPRITE, 24, 4, 0, 0,  pos.x + 10, pos.y + 3, i, 4);

                return new Vector2i(37, 10);
            }

        }



        return new Vector2i();
    }
}
