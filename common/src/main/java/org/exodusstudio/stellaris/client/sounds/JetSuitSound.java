package org.exodusstudio.stellaris.client.sounds;

import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.components.JetComponent;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitChestplate;
import org.exodusstudio.stellaris.common.keybinds.KeyVariables;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.SoundRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class JetSuitSound extends AbstractTickableSoundInstance {
    private static final float MAX_VOLUME = 0.7F;
    private static final float FADE_STEP = 0.05F;

    private static JetSuitSound current;

    private final LocalPlayer player;

    private JetSuitSound(LocalPlayer player) {
        super(SoundRegistry.BOOST_SOUND.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.looping = true;
        this.volume = 0.0F;
        this.followPlayer();
    }

    public static void clientTick(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null || !hasJetModule(player)) {
            return;
        }

        if (current != null && !current.isStopped() && minecraft.getSoundManager().isActive(current)) {
            return;
        }

        current = new JetSuitSound(player);
        minecraft.getSoundManager().play(current);
    }

    private static boolean hasJetModule(LocalPlayer player) {
        return Utils.isLivingInSpaceSuit(player)
                && ModuleUtils.getSpaceSuitModule(player.getItemBySlot(EquipmentSlot.FEET), SpaceSuitModule.JetModule.class) != null;
    }

    private static boolean isFiring(LocalPlayer player) {
        if (player.isPassenger() || player.isSpectator() || !hasFuel(player)) {
            return false;
        }

        JetComponent jet = player.getItemBySlot(EquipmentSlot.FEET).get(DataComponentsRegistry.JET_COMPONENT.get());
        if (jet == null) {
            return false;
        }

        return switch (jet.type()) {
            case NORMAL -> player.getAbilities().flying && !player.isCreative();
            case HOVER -> !player.onGround() && !player.isInWater() && !player.isInLava() && KeyVariables.isHoldingJump(player);
            case ELYTRA -> player.isFallFlying() && KeyVariables.isHoldingUp(player);
            case DISABLED -> false;
        };
    }

    private static boolean hasFuel(LocalPlayer player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof SpaceSuitChestplate chestplate)) {
            return false;
        }

        UniversalFluidItemStorage storage = chestplate.getFluidTank(chest);
        return storage != null && !storage.getFluidInTank(0).isEmpty();
    }

    @Override
    public boolean canPlaySound() {
        return !this.player.isSilent();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (this.player.isRemoved() || !hasJetModule(this.player)) {
            this.stop();
            return;
        }

        this.followPlayer();
        this.volume = isFiring(this.player)
                ? Math.min(MAX_VOLUME, this.volume + FADE_STEP)
                : Math.max(0.0F, this.volume - FADE_STEP);
    }

    private void followPlayer() {
        this.x = this.player.getX();
        this.y = this.player.getY();
        this.z = this.player.getZ();
    }
}
