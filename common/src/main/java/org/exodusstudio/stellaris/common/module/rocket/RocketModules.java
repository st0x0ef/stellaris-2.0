package org.exodusstudio.stellaris.common.module.rocket;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.exodusstudio.stellaris.common.module.Module;
import org.exodusstudio.stellaris.common.module.Modules;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;

import java.util.List;

public class RocketModules extends Modules<RocketModule> {

    public static final Codec<Modules<RocketModule>> CODEC =
            createCodec(StellarisRegistries.ROCKET_MODULE, RocketModules::new, rocketModuleModules -> rocketModuleModules.modules);

    public static final StreamCodec<RegistryFriendlyByteBuf, Modules<RocketModule>> STREAM_CODEC = createStreamCodec(CODEC);

    public static RocketModules empty() {
        return new RocketModules(List.of());
    }

    public RocketModules(List<RocketModule> modules) {
        super(modules);
    }
}