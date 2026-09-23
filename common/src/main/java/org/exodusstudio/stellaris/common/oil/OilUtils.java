package org.exodusstudio.stellaris.common.oil;

import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.config.CommonConfig;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

public class OilUtils {

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
