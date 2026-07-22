package org.exodusstudio.stellaris.client.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.entities.vehicles.LanderEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class RocketBarOverlay {

    public static final Identifier ROCKET = IdentifierUtils.texture("planet_bar/rocket");

    private static final Identifier DEFAULT_BAR = IdentifierUtils.texture("planet_bar/earth_planet_bar");

    private static final int BAR_WIDTH = 16;
    private static final int BAR_HEIGHT = 128;
    private static final int MARKER_WIDTH = 8;
    private static final int MARKER_HEIGHT = 11;
    private static final float MARKER_TRAVEL = 113f;
    private static final float MARKER_BASE_OFFSET = 103f / 2f;

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = mc.level;

        if (player == null || level == null) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof RocketEntity) && !(vehicle instanceof LanderEntity)) {
            return;
        }

        BlockPos playerPos = player.blockPosition();
        double min = level.getMinY();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(playerPos.getX(), 0, playerPos.getZ());
        for (int y = level.getMinY(); y < level.getMaxY(); y++) {
            if (level.getBlockState(cursor.setY(y)).getBlock() != Blocks.AIR) {
                min = y;
            }
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        double playerY = Mth.lerp(partialTick, player.yOld, player.getY());

        double tpHeight = Stellaris.CONFIG.vehicleConfig.rocketTpHeight;
        float yHeight = (float) ((playerY - min) / (tpHeight - min)) * MARKER_TRAVEL;

        Planet planet = PlanetsData.getPlanet(level.dimension());
        Identifier planetBar = planet != null ? planet.planetBar().orElse(DEFAULT_BAR) : DEFAULT_BAR;

        int barY = graphics.guiHeight() / 2 - BAR_HEIGHT / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, planetBar, 0, barY, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

        float markerY = graphics.guiHeight() / 2f + MARKER_BASE_OFFSET - yHeight;
        int markerYFloor = Mth.floor(markerY);

        graphics.pose().pushMatrix();
        graphics.pose().translate(0f, markerY - markerYFloor);
        graphics.blit(RenderPipelines.GUI_TEXTURED, ROCKET, 4, markerYFloor, 0, 0, MARKER_WIDTH, MARKER_HEIGHT, MARKER_WIDTH, MARKER_HEIGHT);
        graphics.pose().popMatrix();
    }
}