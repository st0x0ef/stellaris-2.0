package org.exodusstudio.stellaris.common.registries;

import org.exodusstudio.stellaris.common.network.packets.OpenBlockEntityMenusPacket;
import org.exodusstudio.stellaris.common.utils.MenuUtils;

public class MenuProviderRegistry {
    // Engineering Station
    public static final OpenBlockEntityMenusPacket.BlockEntityMenuProvider ROCKET_CRAFTING = new OpenBlockEntityMenusPacket.BlockEntityMenuProvider("crafting", MenuUtils::createRocketStationMenu);
    public static final OpenBlockEntityMenusPacket.BlockEntityMenuProvider ROCKET_UPGRADE = new OpenBlockEntityMenusPacket.BlockEntityMenuProvider("upgrade", MenuUtils::createRocketUpgraderMenu);
    public static final OpenBlockEntityMenusPacket.BlockEntityMenuProvider SPACE_STATION_PLANNER = new OpenBlockEntityMenusPacket.BlockEntityMenuProvider("space_station", MenuUtils::createSpaceStationPlannerMenu);

    // Laboratory
    public static final OpenBlockEntityMenusPacket.BlockEntityMenuProvider VACCINE = new OpenBlockEntityMenusPacket.BlockEntityMenuProvider("vaccine", MenuUtils::createVaccineMenu);
    public static final OpenBlockEntityMenusPacket.BlockEntityMenuProvider RESEARCH = new OpenBlockEntityMenusPacket.BlockEntityMenuProvider("research", MenuUtils::createResearchMenu);

    public static void register() {}
}
