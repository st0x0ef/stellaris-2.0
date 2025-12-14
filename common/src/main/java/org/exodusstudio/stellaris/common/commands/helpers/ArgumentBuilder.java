package org.exodusstudio.stellaris.common.commands.helpers;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ArgumentBuilder<T> {

    private final String argumentName;
    private final ArgumentType<T> argumentType;

    private ArgumentBuilder<?> subArgumentBuilder;
    private Function<CommandSourceWrapper, Integer> executor;

    private ArgumentBuilder(String argumentName, ArgumentType<T> argumentType) {
        this.argumentName = argumentName;
        this.argumentType = argumentType;
    }

    public static <T> ArgumentBuilder<T> of(String argumentName, ArgumentType<T> argumentType) {
        return new ArgumentBuilder<>(argumentName, argumentType);
    }

    public ArgumentBuilder<T> execute(Function<CommandSourceWrapper, Integer> executor) {
        this.executor = executor;
        return this;
    }

    public ArgumentBuilder<T> addArgument(ArgumentBuilder<?> subArgumentBuilder) {
        this.subArgumentBuilder = subArgumentBuilder;
        return this;
    }

    public RequiredArgumentBuilder<CommandSourceStack, ?> build(Function<CommandSourceWrapper, Integer> commandFunction, @Nullable RequiredArgumentBuilder<CommandSourceStack, ?> parentArgument) {
        RequiredArgumentBuilder<CommandSourceStack, ?> builder = RequiredArgumentBuilder.argument(argumentName, argumentType);

        if (subArgumentBuilder != null) {
            builder.then(subArgumentBuilder.build(commandFunction, null));
        }

        Function<CommandSourceWrapper, Integer> effectiveExecutor = (this.executor != null) ? this.executor : commandFunction;
        builder.executes((c) -> effectiveExecutor.apply(new CommandSourceWrapper(c)));

        if (parentArgument != null) {
            parentArgument.then(builder);
            return parentArgument;
        }

        return builder;
    }
}
