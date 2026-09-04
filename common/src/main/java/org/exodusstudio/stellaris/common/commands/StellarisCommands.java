package org.exodusstudio.stellaris.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.overlays.FadingHolder;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.commands.arguments.PlanetArgument;
import org.exodusstudio.stellaris.common.commands.helpers.ArgumentBuilder;
import org.exodusstudio.stellaris.common.commands.helpers.CommandBuilder;
import org.exodusstudio.stellaris.common.commands.helpers.CommandSourceWrapper;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.entities.vehicles.LanderEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.network.packets.CountdownOverlayPacket;
import org.exodusstudio.stellaris.common.network.packets.StartFadePacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;
import org.exodusstudio.stellaris.common.utils.PlanetUtil;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StellarisCommands {

    public StellarisCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        CommandBuilder builder = CommandBuilder.of(dispatcher, "stellaris").permission(2);
        screenCommand(builder);
        planetsCommand(builder);
        testCommand(builder);
        adminCommand(builder);
        antennaCommands(builder);
        countdownCommand(builder);

        infectionCommand(builder);
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
            wrapper.getPlayer().sendSystemMessage(Component.literal(stringBuilder.toString()));
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
                    wrapper.getPlayer().sendSystemMessage(Component.literal("Planet not found!"));
                    return false;
                }
            }).findFirst().orElse(null);
            if (planet == null) {
                wrapper.getPlayer().sendSystemMessage(Component.literal("Planet not found!"));
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
                wrapper.getPlayer().sendSystemMessage(planet.getDisplayInfo());
                return wrapper.success();
            } else {
                wrapper.getPlayer().sendSystemMessage(Component.literal("You are not on a registered planet."));
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
                                    wrapper.getPlayer().sendSystemMessage(Component.literal("Planet not found!"));
                                    return false;
                                }
                            }).findFirst().ifPresentOrElse(planet -> wrapper.getPlayer().sendSystemMessage(planet.getDisplayInfo()), () -> wrapper.getPlayer().sendSystemMessage(Component.literal("Planet not found!")));
                            return wrapper.success();
                        })
        );

        builder.addSubCommand(infoNoArg);
    }

    private void testCommand(CommandBuilder builder) {
        builder.addSubCommand(
                builder.createSubCommand("test")
                        .addSubCommand(builder.createSubCommand("fade")
                                .execute((context) -> {

                                    NetworkManager.sendToPlayer(context.getPlayer(), new StartFadePacket(new FadingHolder(true, 0f)));
                                    return context.success();
                                })
                        )
                        .addSubCommand(builder.createSubCommand("unfade")
                                .execute((context) -> {
                                    NetworkManager.sendToPlayer(context.getPlayer(), new StartFadePacket(new FadingHolder(false, 1f)));
                                    return context.success();
                                })
                        )
                        .addSubCommand(builder.createSubCommand("afterfade")
                                .execute(context -> {

                                    Utils.executeWithFade(context.getPlayer(), () -> MenuRegistry.openExtendedMenu(context.getPlayer(), MainTabletMenu.createProvider(IdentifierUtils.id("applications/planet_selection"))), true);

                                    return context.success();
                                })
                        )
                        .addSubCommand(builder.createSubCommand("testLander")
                                .execute((context) -> {
                                    ServerPlayer player = context.getPlayer();

                                    Entity vehicle = player.getVehicle();
                                    if( vehicle instanceof RocketEntity rocketEntity) {
                                        LanderEntity landerEntity = new LanderEntity(player.level(), true);
                                        landerEntity.setPos(rocketEntity.getPosition(1f));
                                        player.level().addFreshEntity(landerEntity);

                                        landerEntity.fillInventoryFromRocket(rocketEntity);
                                        rocketEntity.remove(Entity.RemovalReason.DISCARDED);
                                    }

                                    return context.success();
                                })
                        )
        );
    }

    private void adminCommand(CommandBuilder builder) {
        CommandBuilder baseAdmin = builder.createSubCommand("admin");

        baseAdmin.addSubCommand(
                builder.createSubCommand("menuState")
                        .addArgument(ArgumentBuilder.of("state", BoolArgumentType.bool())
                                .execute(commandSourceWrapper -> {
                                    boolean open = BoolArgumentType.getBool(commandSourceWrapper.context(), "state");
                                    commandSourceWrapper.getPlayer().stellaris$setPlanetMenuOpen(open, commandSourceWrapper.getPlayer(), true);
                                    commandSourceWrapper.sendSuccess(Component.literal("Menu state is now " + open), false);
                                    return commandSourceWrapper.success();

                                }))
        );

        builder.addSubCommand(baseAdmin);
    }

    private void antennaCommands(CommandBuilder builder) {
        CommandBuilder baseAdmin = builder.createSubCommand("antennas");

        baseAdmin.addSubCommand(
                builder.createSubCommand("list")
                        .execute((c) -> {
                            AntennaSavedData  antennaSavedData = AntennaSavedData.getSavedAntennas(c.getServer());

                            Map<UUID, Antenna> antennas = antennaSavedData.getAntennas(null);

                            MutableComponent component = Component.literal("Antenna List");

                            for(Map.Entry<UUID, Antenna> entry : antennas.entrySet()) {
                                UUID uuid = entry.getKey();
                                Antenna antenna = entry.getValue();
                                component.append(Component.literal("\n- " + uuid + " : " + antenna.name + " at " + antenna.blockPos));
                            }

                            c.sendSuccess(component, false);
                            return c.success();
                        })
        );

        baseAdmin.addSubCommand(
                builder.createSubCommand("add")
                        .addArgument(ArgumentBuilder.of("name", StringArgumentType.string())
                                .addArgument(ArgumentBuilder.of("public", BoolArgumentType.bool())
                                        .execute(c -> {

                                            Player player = c.getPlayer();
                                            BlockPos pos = player.getOnPos();
                                            boolean isPublic = BoolArgumentType.getBool(c.context(), "public");
                                            String name = StringArgumentType.getString(c.context(), "name");

                                            Antenna antenna = new Antenna(pos, player.level().dimension(), name, isPublic, player.getGameProfile().id(), List.of());
                                            AntennaSavedData antennaSavedData = AntennaSavedData.getSavedAntennas(c.getServer());
                                            antennaSavedData.addAntenna(antenna);

                                            c.sendSuccess(Component.literal("Antenna added at " + pos), false);
                                            return c.success();

                                        })))
        );

        baseAdmin.addSubCommand(builder.createSubCommand("remove")
                .addArgument(ArgumentBuilder.of("uuid-or-name", StringArgumentType.string())
                        .execute(c -> {
                            String name = StringArgumentType.getString(c.context(), "uuid-or-name");
                            AntennaSavedData antennaSavedData = AntennaSavedData.getSavedAntennas(c.getServer());

                            try {
                                UUID uuid = UUID.fromString(name);
                                antennaSavedData.removeAntenna(uuid);

                            } catch(IllegalArgumentException e) {
                                Map.Entry<UUID, Antenna> antenna = antennaSavedData.getAntenna(name);
                                antennaSavedData.removeAntenna(antenna.getKey());
                            }

                            c.sendSuccess(Component.literal("Antenna " + name + " removed "), false);

                            return c.success();
                        })));



        builder.addSubCommand(baseAdmin);
    }


    private void countdownCommand(CommandBuilder builder) {
        CommandBuilder countdown = builder.createSubCommand("countdown");

        countdown.addArgument(
                ArgumentBuilder.of("number", IntegerArgumentType.integer(1, 10))
                        .execute(wrapper -> setCountdown(wrapper, IntegerArgumentType.getInteger(wrapper.context(), "number")))
        );

        countdown.addSubCommand(
                builder.createSubCommand("off").execute(wrapper -> setCountdown(wrapper, 0))
        );

        builder.addSubCommand(countdown);
    }

    private int setCountdown(CommandSourceWrapper wrapper, int number) {
        if (!wrapper.runByPlayer()) {
            return wrapper.failure();
        }

        NetworkManager.sendToPlayer(wrapper.getPlayer(), new CountdownOverlayPacket(number));
        return wrapper.success();
    }

    private void infectionCommand(CommandBuilder builder) {
        CommandBuilder infection = builder.createSubCommand("infection");

        // get stage
        CommandBuilder getStageNoArgs = builder.createSubCommand("getStage").execute(commandSourceWrapper -> {
            ServerPlayer player = commandSourceWrapper.getPlayer();
            int stage = MoonLoreUtils.getResearchProgressionStage(player);
            player.sendSystemMessage(Component.literal("Current stage : " + stage));
            return commandSourceWrapper.success();
        });

        getStageNoArgs.addArgument(ArgumentBuilder.of("player", EntityArgument.player()).execute(commandSourceWrapper  -> {
            ServerPlayer player;
            try {
                player = EntityArgument.getPlayer(commandSourceWrapper.context(), "player");
            } catch (CommandSyntaxException e) {
                commandSourceWrapper.sendFailure(Component.literal("Player not found!"));
                return commandSourceWrapper.failure();
            }
            int stage = MoonLoreUtils.getResearchProgressionStage(player);
            commandSourceWrapper.sendSuccess(Component.literal("Current stage for " + player.getName().getString() + " : " + stage), false);
            return commandSourceWrapper.success();
        }));

        // set stage
        CommandBuilder setStageCurrentPlayer = builder.createSubCommand("setStage").addArgument(ArgumentBuilder.of("stage", IntegerArgumentType.integer(-1, MoonLoreUtils.MAX_STAGE)).execute(commandSourceWrapper -> {
            ServerPlayer player = commandSourceWrapper.getPlayer();
            int stage = IntegerArgumentType.getInteger(commandSourceWrapper.context(), "stage");
            player.stellaris$saveDataAttachments(MoonLoreUtils.MOON_LORE_PROGRESSION, stage);
            player.sendSystemMessage(Component.literal("Stage set to " + stage));
            return commandSourceWrapper.success();
        }));

        setStageCurrentPlayer.addSubCommand(builder.createSubCommand("for").addArgument(ArgumentBuilder.of("player", EntityArgument.player()).addArgument(ArgumentBuilder.of("stage", IntegerArgumentType.integer()).execute(commandSourceWrapper -> {
            ServerPlayer player;
            try {
                player = EntityArgument.getPlayer(commandSourceWrapper.context(), "player");
            } catch (CommandSyntaxException e) {
                commandSourceWrapper.sendFailure(Component.literal("Player not found!"));
                return commandSourceWrapper.failure();
            }
            int stage = IntegerArgumentType.getInteger(commandSourceWrapper.context(), "stage");
            player.stellaris$saveDataAttachments(MoonLoreUtils.MOON_LORE_PROGRESSION, stage);
            commandSourceWrapper.sendSuccess(Component.literal("Stage set to " + stage + " for " + player.getName().getString()), false);
            return commandSourceWrapper.success();
        }))));

        // is immunised
        CommandBuilder isImmunisedNoArgs = builder.createSubCommand("isImmunised").execute(commandSourceWrapper -> {
            ServerPlayer player = commandSourceWrapper.getPlayer();
            boolean immunised = MoonLoreUtils.isPlayerImmunisedToInfection(player);
            player.sendSystemMessage(immunised ? Component.literal("You are immunised to the parasite.") : Component.literal("You are vulnerable to the parasite."));
            return commandSourceWrapper.success();
        });

        isImmunisedNoArgs.addArgument(ArgumentBuilder.of("player", EntityArgument.player()).execute(commandSourceWrapper  -> {
            ServerPlayer player;
            try {
                player = EntityArgument.getPlayer(commandSourceWrapper.context(), "player");
            } catch (CommandSyntaxException e) {
                commandSourceWrapper.sendFailure(Component.literal("Player not found!"));
                return commandSourceWrapper.failure();
            }
            boolean immunised = MoonLoreUtils.isPlayerImmunisedToInfection(player);
            commandSourceWrapper.sendSuccess(immunised ? Component.literal(player.getName().getString() + " is immunised to the parasite.") : Component.literal(player.getName().getString() + " is vulnerable to the parasite."), false);
            return commandSourceWrapper.success();
        }));

        // set immunised
        CommandBuilder setImmunisedCurrentPlayer = builder.createSubCommand("setImmunised").addArgument(ArgumentBuilder.of("immunised", BoolArgumentType.bool()).execute(commandSourceWrapper -> {
            ServerPlayer player = commandSourceWrapper.getPlayer();
            boolean immunised = BoolArgumentType.getBool(commandSourceWrapper.context(), "immunised");
            player.stellaris$saveDataAttachments(MoonLoreUtils.PLAYER_IMMUNISED_TO_INFECTION, immunised);
            player.sendSystemMessage(immunised ? Component.literal("You are now immunised to the infection.") : Component.literal("You are no longer immunised to the infection."));
            return commandSourceWrapper.success();
        }));

        setImmunisedCurrentPlayer.addSubCommand(builder.createSubCommand("for").addArgument(ArgumentBuilder.of("player", EntityArgument.player()).addArgument(ArgumentBuilder.of("immunised", BoolArgumentType.bool()).execute(commandSourceWrapper -> {
            ServerPlayer player;
            try {
                player = EntityArgument.getPlayer(commandSourceWrapper.context(), "player");
            } catch (CommandSyntaxException e) {
                commandSourceWrapper.sendFailure(Component.literal("Player not found!"));
                return commandSourceWrapper.failure();
            }
            boolean immunised = BoolArgumentType.getBool(commandSourceWrapper.context(), "immunised");
            player.stellaris$saveDataAttachments(MoonLoreUtils.PLAYER_IMMUNISED_TO_INFECTION, immunised);
            commandSourceWrapper.sendSuccess(immunised ? Component.literal(player.getName().getString() + " is now immunised to the infection.") : Component.literal(player.getName().getString() + " is no longer immunised to the infection."), false);
            return commandSourceWrapper.success();
        }))));

        builder.addSubCommand(infection);
        infection.addSubCommand(getStageNoArgs);
        infection.addSubCommand(setStageCurrentPlayer);
        infection.addSubCommand(isImmunisedNoArgs);
        infection.addSubCommand(setImmunisedCurrentPlayer);
    }
}
