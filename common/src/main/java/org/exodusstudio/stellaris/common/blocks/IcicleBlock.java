package org.exodusstudio.stellaris.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class IcicleBlock extends SpeleothemBlock {
    private static final int MAX_GROWING_LENGTH = 4;
    private static final int BREAKS_IF_ABOVE_LIGHT_LEVEL = 4;
    private static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    private static final List<Direction> ONLY_GROW_STALACTITES = List.of(Direction.DOWN);
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK = 0.02F;

    public IcicleBlock(BlockState blockToGrowOn, Properties properties) {
        super(blockToGrowOn, properties);
    }

    @Override
    protected int getStalactiteLandingSound() {
        return 1055;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ATTACHED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }

        boolean attached = isStalactiteStartPos(state, context.getLevel(), context.getClickedPos())
                || isStalagmiteStartPos(state, context.getLevel(), context.getClickedPos());
        return state.setValue(ATTACHED, attached);
    }

    @Override
    protected int getMaxGrowthLength() {
        return MAX_GROWING_LENGTH;
    }

    @Override
    protected boolean canGrow(LevelReader level, BlockPos pos) {
        if (!super.canGrow(level, pos)) {
            return false;
        } else {
            return !this.wouldMelt(level, pos) && isStalactite(level.getBlockState(pos));
        }
    }

    @Override
    protected boolean shouldMergeTips() {
        return false;
    }

    @Override
    protected List<Direction> getNaturalGrowthDirections() {
        return ONLY_GROW_STALACTITES;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (isFreeHangingStalactite(state)) {
            float randomValue = random.nextFloat();
            if (!(randomValue > DRIP_PROBABILITY_PER_ANIMATE_TICK)) {
                this.spawnDripParticle(level, pos, state, pos.above(), null);
            }
        }
    }

    @Override
    protected @Nullable ParticleOptions getDripParticle(Level level, @Nullable Fluid fluidAbove, BlockPos posAbove) {
        return new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SNOW.defaultBlockState());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!this.isStartPos(state, level, pos) && this.wouldMelt(level, pos)) {
            if (!isStalagmite(state) && (!isTip(state, true) || level.getBlockState(pos.below()).isAir())) {
                spawnFallingStalactite(state, level, pos);
            } else {
                level.destroyBlock(pos, true);
            }
        } else {
            super.randomTick(state, level, pos, random);
        }
    }

    private boolean isStartPos(BlockState state, LevelReader level, BlockPos pos) {
        return isStalactiteStartPos(state, level, pos) || isStalagmiteStartPos(state, level, pos);
    }

    private boolean wouldMelt(LevelReader level, BlockPos pos) {
        return level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)
                || this.isTooBright(level, pos)
                || this.isAboveMeltingBlock(level, pos);
    }

    private boolean isAboveMeltingBlock(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(TagsRegistry.BlockTags.MELTS_ICICLE_ABOVE) || level.getBlockState(pos.below(2)).is(TagsRegistry.BlockTags.MELTS_ICICLE_ABOVE);
    }

    private boolean isTooBright(LevelReader level, BlockPos pos) {
        return level.getBrightness(LightLayer.BLOCK, pos) > BREAKS_IF_ABOVE_LIGHT_LEVEL;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        super.fallOnDamage(level, state, pos, entity, fallDistance);
    }
}
