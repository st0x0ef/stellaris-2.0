package org.exodusstudio.stellaris.common.registries;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class ModWoodTypes {
    public static final BlockSetType LUNAR = new BlockSetType(IdentifierUtils.id("lunar").toString());
    public static final WoodType LUNAR_WOOD_TYPE = new WoodType(IdentifierUtils.id("lunar").toString(), LUNAR);
}


