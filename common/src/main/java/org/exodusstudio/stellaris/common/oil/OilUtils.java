package org.exodusstudio.stellaris.common.oil;

import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.Random;

public class OilUtils {

    public static int getRandomOilLevel() {
        Random random = new Random();
        if (random.nextInt(0, Stellaris.CONFIG.oilConfig.chunkOilChance) == 0) {
            return random.nextInt(Stellaris.CONFIG.oilConfig.minOil, Stellaris.CONFIG.oilConfig.maxOil);

        }
        return 0;
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
