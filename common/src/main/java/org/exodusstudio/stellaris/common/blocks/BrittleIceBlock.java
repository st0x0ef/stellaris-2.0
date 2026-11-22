package org.exodusstudio.stellaris.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.exodusstudio.stellaris.common.registries.SoundRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jspecify.annotations.Nullable;

public class BrittleIceBlock extends CrackingIceBlock {

    public BrittleIceBlock(Properties properties) {
        super(properties);
    }

    public void stepOn(final Level level, final BlockPos pos, final BlockState onState, final Entity entity) {
        if (level instanceof ServerLevel serverLevel
                && this.shouldEntityIncreaseCracking(entity, pos, serverLevel)
                && !serverLevel.getBlockTicks().hasScheduledTick(pos, this)) {
            if (this.slightlyMelt(onState, serverLevel, pos)) {
                this.propagateMelting(serverLevel, pos, serverLevel.getRandom());
            }

            serverLevel.scheduleTick(pos, this, this.getTickDelay(serverLevel.getRandom()));
        }
    }

    protected boolean shouldEntityIncreaseCracking(final Entity entity, final BlockPos pos, final ServerLevel level) {
        if (!(entity instanceof LivingEntity) || entity.is(TagsRegistry.EntityTags.DOES_NOT_BREAK_BRITTLE_ICE)) {
            return false;
        } else {
            return entity instanceof Player player
                    ? !level.getServer().isUnderSpawnProtection(level, pos, player)
                    : level.getGameRules().get(GameRules.MOB_GRIEFING);
        }
    }

    @Override
    protected int getTickDelay(RandomSource random) {
        return 24;
    }

    protected void melt(final BlockState state, final Level level, final BlockPos pos) {
        level.destroyBlock(pos, false);
    }

    @Override
    protected void playCrackingIceParticle(BlockState state, Level level, BlockPos pos) {
        int age = state.getValue(AGE);
        if (age == 0) {
            level.levelEvent(2014, pos, getId(state));
        }
    }

    @Override
    protected @Nullable SoundEvent getCrackingIceSound() {
        return SoundRegistry.BRITTLE_ICE_CRACK.get();
    }

    public static BlockState meltsInto() {
        return Blocks.AIR.defaultBlockState();
    }
}
