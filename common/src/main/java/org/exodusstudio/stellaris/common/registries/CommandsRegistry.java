package org.exodusstudio.stellaris.common.registries;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import org.exodusstudio.stellaris.common.commands.StellarisCommands;

public class CommandsRegistry {
    public static void init() {
        CommandRegistrationEvent.EVENT.register(StellarisCommands::new);
    }
}
