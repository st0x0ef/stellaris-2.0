package org.exodusstudio.stellaris.common.oil;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.config.CommonConfig;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class OilUtils {

    private static Identifier pumpjackFluidId;
    private static Fluid pumpjackFluid;

    public static Fluid getPumpjackFluid() {
        Identifier id = Stellaris.CONFIG.oilConfig.pumpjackFluid;
        if (pumpjackFluid == null || !Objects.equals(id, pumpjackFluidId)) {
            pumpjackFluidId = id;
            pumpjackFluid = resolvePumpjackFluid(id);
        }
        return pumpjackFluid;
    }

    private static Fluid resolvePumpjackFluid(Identifier id) {
        Fluid fluid = id == null ? Fluids.EMPTY : BuiltInRegistries.FLUID.getValue(id);
        if (fluid == Fluids.EMPTY) {
            Stellaris.LOG.warn("Config value oilConfig.pumpjackFluid = {} is not a registered fluid; using stellaris:oil", id);
            return FluidsRegistry.OIL_STILL.get();
        }
        return fluid;
    }

    public static int getRandomOilLevel() {
        CommonConfig.OilConfig config = Stellaris.CONFIG.oilConfig;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (config.chunkOilChance > 1 && random.nextInt(config.chunkOilChance) != 0) {
            return 0;
        }

        int min = Math.max(0, config.minOil);
        return config.maxOil > min ? random.nextInt(min, config.maxOil) : min;
    }

    public static int getOilLevelColor(int oilLevel) {
        if (oilLevel > 40000) {
            return Utils.getMinecraftColor("green");
        }
        else if (oilLevel > 0) {
            return Utils.getMinecraftColor("orange");
        }
        else {
            return Utils.getMinecraftColor("red");
        }
    }

}
