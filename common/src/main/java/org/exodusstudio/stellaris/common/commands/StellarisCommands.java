package org.exodusstudio.stellaris.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.utils.WikiEntryTextRenderer;
import org.exodusstudio.stellaris.common.commands.helpers.ArgumentBuilder;
import org.exodusstudio.stellaris.common.commands.helpers.CommandBuilder;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenScreenPacket;

import java.util.ArrayList;
import org.exodusstudio.stellaris.common.commands.arguments.PlanetArgument;
import org.exodusstudio.stellaris.common.commands.helpers.ArgumentBuilder;
import org.exodusstudio.stellaris.common.commands.helpers.CommandBuilder;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.utils.PlanetUtil;

public class StellarisCommands {

    public StellarisCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        CommandBuilder builder = CommandBuilder.of(dispatcher, "stellaris").permission(2);
        screenCommand(builder);
        planetsCommand(builder);
        builder.register();
    }

    private void screenCommand(CommandBuilder builder) {
        builder.addSubCommand(
                builder.createSubCommand("screen")
                        .addSubCommand(builder.createSubCommand("tablet")
                                .execute((context) -> {
                                    MenuRegistry.openExtendedMenu(context.getPlayer(), MainTabletMenu.createProvider());
                                    return context.success();
                                })
                        )
                ).addSubCommand(builder.createSubCommand("oil")
                        .addSubCommand(builder.createSubCommand("get").execute((context -> {
                            if(!context.runByPlayer()) {
                                return context.failure();
                            }
                            ChunkAccess access = context.getPlayer().level().getChunk(context.getPlayer().getOnPos());
                            context.sendSuccess(Component.literal("Oil Level : " + access.stellaris$getChunkOilLevel()), true);

                            return 0;
                        })))
                        .addSubCommand(builder.createSubCommand("set")
                                .addArgument(ArgumentBuilder.of("quantity", IntegerArgumentType.integer(0, Stellaris.CONFIG.oilConfig.maxOil)))
                                .execute((context -> {

                                    ServerPlayer player = context.getPlayer();
                                    if(!context.runByPlayer()) {
                                        return context.failure();
                                    }

                                    int quantity = IntegerArgumentType.getInteger(context.context(), "quantity");
                                    ChunkAccess access = player.level().getChunk(context.getPlayer().getOnPos());
                                    access.stellaris$setChunkOilLevel(quantity);
                                    context.sendSuccess(Component.literal("Oil Level : " + access.stellaris$getChunkOilLevel()), true);

                                    return 0;
                                })))
                )
                .register();

    }

    private void planetsCommand(CommandBuilder builder) {
        CommandBuilder planetsCommandBuilder = builder.createSubCommand("planets").execute(wrapper -> {
            StringBuilder stringBuilder = new StringBuilder("Planets registered:\n");
            for (Planet planet : PlanetsData.PLANETS) {
                stringBuilder.append("- ").append(planet.translationKey()).append(" (").append(planet.dimension()).append(")\n");
            }
            wrapper.getPlayer().displayClientMessage(Component.literal(stringBuilder.toString()), false);
            return wrapper.success();
        });

        teleportToPlanetCommand(planetsCommandBuilder);
        planetInfoCommand(planetsCommandBuilder);

        builder.addSubCommand(planetsCommandBuilder);
    }

    private void teleportToPlanetCommand(CommandBuilder builder) {
        builder.addSubCommand(builder.createSubCommand("teleport").addArgument(ArgumentBuilder.of("planet", PlanetArgument.planet())).execute(wrapper -> {
            Planet planet = PlanetsData.PLANETS.stream().filter(p -> {
                try {
                    return p.is(PlanetArgument.getPlanet(wrapper.context(), "planet"));
                } catch (CommandSyntaxException e) {
                    wrapper.getPlayer().displayClientMessage(Component.literal("Planet not found!"), false);
                    return false;
                }
            }).findFirst().orElse(null);
            if (planet == null) {
                wrapper.getPlayer().displayClientMessage(Component.literal("Planet not found!"), false);
                return wrapper.failure();
            }
            PlanetUtil.teleportToPlanet(wrapper.getPlayer(), planet, 100);
            return wrapper.success();
        }));
    }

    private void planetInfoCommand(CommandBuilder builder) {
        CommandBuilder infoNoArg = builder.createSubCommand("info").execute(wrapper -> {
            Planet planet = PlanetsData.getPlanet(wrapper.getPlayer().level().dimension());
            if (planet != null) {
                wrapper.getPlayer().displayClientMessage(planet.getDisplayInfo(), false);
                return wrapper.success();
            } else {
                wrapper.getPlayer().displayClientMessage(Component.literal("You are not on a registered planet."), false);
                return wrapper.failure();
            }
        });

        infoNoArg.addArgument(
                ArgumentBuilder.of("planet", PlanetArgument.planet())
                        .execute(wrapper -> {
                            PlanetsData.PLANETS.stream().filter(p -> {
                                try {
                                    return p.is(PlanetArgument.getPlanet(wrapper.context(), "planet"));
                                } catch (CommandSyntaxException e) {
                                    wrapper.getPlayer().displayClientMessage(Component.literal("Planet not found!"), false);
                                    return false;
                                }
                            }).findFirst().ifPresentOrElse(planet -> {
                                wrapper.getPlayer().displayClientMessage(planet.getDisplayInfo(), false);
                            }, () -> {
                                wrapper.getPlayer().displayClientMessage(Component.literal("Planet not found!"), false);
                            });
                            return wrapper.success();
                        })
        );

        builder.addSubCommand(infoNoArg);
    }
}
