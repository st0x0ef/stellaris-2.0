package org.exodusstudio.stellaris.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fr.tathan.exoconfig.client.screen.ConfigScreen;
import org.exodusstudio.stellaris.Stellaris;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (previous) -> new ConfigScreen<>(previous, Stellaris.CONFIG);
    }
}