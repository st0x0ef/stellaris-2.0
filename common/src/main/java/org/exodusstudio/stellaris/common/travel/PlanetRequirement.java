package org.exodusstudio.stellaris.common.travel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;
import org.exodusstudio.stellaris.platform.RegistrarUtilPlatform;

import java.util.function.Function;

public interface PlanetRequirement {
    Codec<PlanetRequirement> CODEC = Codec.lazyInitialized(() -> RegistrarUtilPlatform.getByNameCodec(StellarisRegistries.PLANET_REQUIREMENT_TYPES).dispatch(PlanetRequirement::codec, Function.identity()));

    MapCodec<? extends PlanetRequirement> codec();

    boolean test(Player player, Modules<RocketModule> modules);

    Component description();
}
