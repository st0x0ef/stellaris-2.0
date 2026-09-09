package org.exodusstudio.stellaris.client.renderers;

import net.minecraft.client.Minecraft;
import org.exodusstudio.stellaris.mixin.client.BossHealthOverlayAccessor;
import java.util.UUID;

public final class StellarisBossHudSlots {
    public static int slot(UUID id, int height) {
        if (id == null) return -1;
        int y = 12;
        for (UUID event : ((BossHealthOverlayAccessor) Minecraft.getInstance().gui.getBossOverlay()).stellaris$events().keySet()) {
            if (y >= height / 3) break;
            if (event.equals(id)) return y;
            y += 19;
        }
        return -1;
    }

    private StellarisBossHudSlots() {}
}
