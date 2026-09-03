package org.exodusstudio.stellaris.client.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import org.exodusstudio.stellaris.client.StellarisClient;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.data.Temperature;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.Optional;

public class TemperatureOverlay {

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;

        if (level != null && level.dimensionType().defaultClock().isPresent() && StellarisClient.CLIENT_CONFIG.temperatureOverlay) {
            Optional<Holder.Reference<Timeline>> timelineReference = level.registryAccess().get(ResourceKey.create(Registries.TIMELINE, level.dimension().identifier()));
            Planet planet = PlanetsData.getPlanet(level.dimension());

            if (planet != null && planet.temperature().isPresent() && timelineReference.isPresent()) {
                float temp = Utils.getCurrentTemperature(planet, timelineReference.get(), level);
                String text = Math.round(temp) + " °C";
                graphics.text(mc.font, text, graphics.guiWidth() - mc.font.width(text) - 4, graphics.guiHeight() - mc.font.lineHeight - 4, Utils.getMinecraftColor("white"));
            }
        }
    }
}
