package org.exodusstudio.stellaris.common.modules.rocket;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;

import java.util.List;

public class RocketModules extends Modules<RocketModule> {

    public static final Codec<Modules<RocketModule>> CODEC =
            createCodec(StellarisRegistries.ROCKET_MODULES, RocketModules::new, rocketModuleModules -> rocketModuleModules.modules);

    public static final StreamCodec<RegistryFriendlyByteBuf, Modules<RocketModule>> STREAM_CODEC = createStreamCodec(CODEC);

    public static RocketModules empty() {
        return new RocketModules(List.of());
    }

    public RocketModules(List<RocketModule> modules) {
        super(modules);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RocketModules other) {
            return this.modules.equals(other.modules);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
