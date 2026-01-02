package org.exodusstudio.stellaris.common.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.data.PlanetsData;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlanetArgument implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType ERROR_INVALID_VALUE;

    public Identifier parse(StringReader reader) throws CommandSyntaxException {
        return Identifier.read(reader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof SharedSuggestionProvider provider) {
            return SharedSuggestionProvider.suggestResource(
                provider.levels().stream()
                    .filter(PlanetsData.PLANETS_LEVEL::containsValue)
                    .map(ResourceKey::identifier),
                suggestionsBuilder
            );
        } else {
            return Suggestions.empty();
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static PlanetArgument planet() {
        return new PlanetArgument();
    }

    public static ServerLevel getPlanet(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        Identifier Identifier = context.getArgument(name, Identifier.class);
        ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, Identifier);
        ServerLevel serverLevel = context.getSource().getServer().getLevel(resourceKey);
        if (serverLevel == null) {
            throw ERROR_INVALID_VALUE.create(Identifier);
        } else {
            return serverLevel;
        }
    }

    static {
        EXAMPLES = Stream.of(Level.OVERWORLD).map((resourceKey) -> resourceKey.identifier().toString()).collect(Collectors.toList());
        ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> Component.translatableEscape("argument.dimension.invalid", object));
    }
}
