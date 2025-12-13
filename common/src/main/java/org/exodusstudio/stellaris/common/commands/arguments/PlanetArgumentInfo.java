package org.exodusstudio.stellaris.common.commands.arguments;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import org.exodusstudio.stellaris.common.registries.ArgumentsTypesRegistry;

public class PlanetArgumentInfo implements ArgumentTypeInfo<PlanetArgument, PlanetArgumentInfo.Template> {

    @Override
    public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {

    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
        return new Template();
    }

    @Override
    public void serializeToJson(Template template, JsonObject json) {

    }

    @Override
    public Template unpack(PlanetArgument argument) {
        return new Template();
    }

    public static class Template implements ArgumentTypeInfo.Template<PlanetArgument> {
        @Override
        public PlanetArgument instantiate(CommandBuildContext context) {
            return PlanetArgument.planet();
        }

        @Override
        public ArgumentTypeInfo<PlanetArgument, ?> type() {
            return ArgumentsTypesRegistry.PLANET.get();
        }
    }
}
