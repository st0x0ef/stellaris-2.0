package org.exodusstudio.stellaris.client.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.exodusstudio.stellaris.common.entities.vehicles.LanderEntity;
import org.exodusstudio.stellaris.common.registries.SoundRegistry;

public class LanderSounds {

    public static void start(LanderEntity lander) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().play(new Beep(lander));
        minecraft.getSoundManager().play(new Thruster(lander));
    }

    private static boolean ridesLander(LanderEntity lander) {
        return lander.hasPassenger(Minecraft.getInstance().player);
    }

    private abstract static class LanderSound extends AbstractTickableSoundInstance {
        private static final float FADE_STEP = 0.05F;

        protected final LanderEntity lander;
        private final float maxVolume;

        LanderSound(LanderEntity lander, SoundEvent sound, float pitch, float maxVolume) {
            super(sound, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
            this.lander = lander;
            this.maxVolume = maxVolume;
            this.looping = true;
            this.pitch = pitch;
            this.volume = 0.0F;
            this.followLander();
        }

        protected abstract boolean isPlaying();

        @Override
        public boolean canPlaySound() {
            return !this.lander.isSilent();
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }

        @Override
        public void tick() {
            if (this.lander.isRemoved()) {
                this.stop();
                return;
            }

            this.followLander();
            this.volume = this.isPlaying()
                    ? Math.min(this.maxVolume, this.volume + FADE_STEP)
                    : Math.max(0.0F, this.volume - FADE_STEP);
        }

        private void followLander() {
            this.x = this.lander.getX();
            this.y = this.lander.getY();
            this.z = this.lander.getZ();
        }
    }

    private static class Beep extends LanderSound {
        Beep(LanderEntity lander) {
            super(lander, SoundRegistry.BEEP_SOUND.get(), 1.0F, 0.5F);
        }

        @Override
        protected boolean isPlaying() {
            return ridesLander(this.lander) && !this.lander.hasLanded() && !this.lander.onGround()
                    && !this.lander.isInWater() && !this.lander.isThrusting();
        }
    }

    private static class Thruster extends LanderSound {
        Thruster(LanderEntity lander) {
            super(lander, SoundRegistry.BOOST_SOUND.get(), 0.8F, 0.5F);
        }

        @Override
        protected boolean isPlaying() {
            return this.lander.isThrusting() && !this.lander.isInWater();
        }
    }
}
