package org.exodusstudio.stellaris.client;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.exodusstudio.stellaris.common.entities.vehicles.LanderEntity;

/**
 * Swings the camera out to third person while the player is riding a lander, so the descent is
 * actually visible, and puts it back the way it was once they are off.
 */
public final class LanderCameraController {

    /** The view the player had before boarding, restored when they leave the lander. */
    private static CameraType previousCameraType;

    private LanderCameraController() {
    }

    public static void init() {
        ClientTickEvent.CLIENT_POST.register(LanderCameraController::clientTick);
    }

    private static void clientTick(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null) {
            // Left the world mid-flight: forget the saved view rather than restoring it later.
            previousCameraType = null;
            return;
        }

        boolean ridingLander = player.getVehicle() instanceof LanderEntity;

        if (ridingLander) {
            if (previousCameraType == null && StellarisClient.CLIENT_CONFIG.landerThirdPerson) {
                previousCameraType = minecraft.options.getCameraType();
                if (previousCameraType != CameraType.THIRD_PERSON_BACK) {
                    minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
                }
            }
            return;
        }

        if (previousCameraType != null) {
            // Only take the view back if the player has not since chosen one themselves.
            if (minecraft.options.getCameraType() == CameraType.THIRD_PERSON_BACK) {
                minecraft.options.setCameraType(previousCameraType);
            }
            previousCameraType = null;
        }
    }
}
