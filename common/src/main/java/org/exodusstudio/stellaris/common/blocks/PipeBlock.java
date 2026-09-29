package org.exodusstudio.stellaris.common.blocks;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.base.BaseCableBlock;
import org.exodusstudio.stellaris.common.blocks.entities.PipeBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PipeBlock extends BaseCableBlock {

    public final int capacity;
    public final int maxIn;
    public final int maxOut;

    public PipeBlock(BlockBehaviour.Properties properties, int capacity, int maxIn, int maxOut) {
        super(properties);
        this.capacity = capacity;
        this.maxIn = maxIn;
        this.maxOut = maxOut;
    }

    @Override
    public boolean isConnectable(Level level, BlockPos pos, Direction direction) {
        BlockState targetState = level.getBlockState(pos);

        // Pipes are bufferless and expose no capability, so connect to sibling pipes by block type.
        if (targetState.getBlock() instanceof PipeBlock) {
            return true;
        }

        if (Capabilities.Fluid.BLOCK.getCapability(level, pos, direction) != null) {
            return true;
        }

        if (targetState.getBlock() instanceof PumpjackProxyBlock) {
            BlockPos mainPos = PumpjackProxyBlock.getMainPos(pos, targetState);
            return Capabilities.Fluid.BLOCK.getCapability(level, mainPos, direction) != null;
        }

        return false;
    }

    @Override
    protected @NotNull MapCodec<? extends PipeBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec(),
                Codec.INT.fieldOf("capacity").forGetter(pipe -> pipe.capacity),
                Codec.INT.fieldOf("maxIn").forGetter(pipe -> pipe.maxIn),
                Codec.INT.fieldOf("maxOut").forGetter(pipe -> pipe.maxOut)
        ).apply(instance, PipeBlock::new));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.PIPE_ENTITY.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return false;
    }


    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        if(level.getBlockEntity(pos) instanceof PipeBlockEntity pipeBlockEntity && pipeBlockEntity.facadeState != null) {
            return pipeBlockEntity.facadeState.getShape(level, pos);
        }

        return this.shapeByIndex[this.getAABBIndex(state)];
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {


        if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipeBlockEntity) {

            if (player.isShiftKeyDown() && (itemStack.isEmpty() || pipeBlockEntity.facadeState != null)) {
                dropFacadeBlock(pos, pipeBlockEntity);
                pipeBlockEntity.setFacadeState(null);
                return InteractionResult.SUCCESS;

            } else if(itemStack.getItem() instanceof BlockItem blockItem)  {

                BlockState itemBlockState = blockItem.getBlock().defaultBlockState();

                if(!itemBlockState.is(TagsRegistry.BlockTags.PIPE_FACADE_BLACKLIST) &&//We don't want thoses block
                   !(itemBlockState.getBlock() instanceof BaseEntityBlock) &&//we don't want block entity
                   Block.isShapeFullBlock(itemBlockState.getShape(level, pos))) //And we only want full block
                {


                    if(itemBlockState.hasProperty(RotatedPillarBlock.AXIS)) {
                        itemBlockState = itemBlockState.setValue(RotatedPillarBlock.AXIS, hitResult.getDirection().getAxis());
                    }

                    itemStack.shrink(1);
                    dropFacadeBlock(pos, pipeBlockEntity);

                    pipeBlockEntity.setFacadeState(itemBlockState);

                    return InteractionResult.SUCCESS;

                }
            }
        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {

        if(!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipeBlockEntity) {
                dropFacadeBlock(pos, pipeBlockEntity);
            }
            Stellaris.LOG.error("destroy");

        }
        super.destroy(level, pos, state);
    }

    public void dropFacadeBlock(BlockPos pos, PipeBlockEntity pipeBlockEntity) {
        if(pipeBlockEntity.facadeState != null) {

            ItemEntity entity = new ItemEntity(pipeBlockEntity.getLevel(), pos.getX(), pos.getY() + 1, pos.getZ(),
                    pipeBlockEntity.facadeState.getCloneItemStack(pipeBlockEntity.getLevel(), pos, true));

            pipeBlockEntity.getLevel().addFreshEntity(entity);

        }

    }

}
