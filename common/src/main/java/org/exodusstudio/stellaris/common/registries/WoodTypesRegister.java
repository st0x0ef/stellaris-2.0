package org.exodusstudio.stellaris.common.registries;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class WoodTypesRegister {
    public static final BlockSetType LUNAR = new BlockSetType(IdentifierUtils.id("lunar").toString());
    public static final WoodType LUNAR_WOOD_TYPE = WoodType.register(new WoodType(IdentifierUtils.id("lunar").toString(), LUNAR));

    public static void register() {
        Sheets.SIGN_SPRITES.put(LUNAR_WOOD_TYPE, Sheets.SIGN_MAPPER.apply(Identifier.parse(LUNAR_WOOD_TYPE.name())));
        Sheets.HANGING_SIGN_SPRITES.put(LUNAR_WOOD_TYPE, Sheets.HANGING_SIGN_MAPPER.apply(Identifier.parse(LUNAR_WOOD_TYPE.name())));
    }
}


