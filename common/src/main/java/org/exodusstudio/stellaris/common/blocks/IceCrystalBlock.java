package org.exodusstudio.stellaris.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.SoundRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

public class IceCrystalBlock extends AbstractCrystalClusterBlock {
    private static final int IDLE_SOUND_CHANCE = 250;

    public IceCrystalBlock(Properties properties) {
        super(7, 10, properties);
    }


    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        playAmbientIceCrystalSounds(level, pos, random);
    }


    public static void playAmbientIceCrystalSounds(final Level level, final BlockPos pos, final RandomSource random) {
        if (random.nextInt(IDLE_SOUND_CHANCE) == 0 && shouldPlayAmbientIceCrystalSound(level, pos)) {
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundRegistry.ICE_CRYSTAL_AMBIENT.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
        }
    }

    private static boolean shouldPlayAmbientIceCrystalSound(final Level level, final BlockPos pos) {
        return level.getBlockState(pos.below()).is(TagsRegistry.BlockTags.TRIGGERS_AMBIENT_ICE_CRYSTAL_SOUNDS);
    }

}
