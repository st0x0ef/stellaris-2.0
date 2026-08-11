package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.base.BaseTickingEntityBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.SpaceFarmBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SpaceFarmBlock extends BaseTickingEntityBlock {

    public static final EnumProperty<SpaceFarmType> FARM_TYPE = EnumProperty.create("farm_type", SpaceFarmType.class);
    public static final MapCodec<SpaceFarmBlock> CODEC = simpleCodec(SpaceFarmBlock::new);

    public SpaceFarmBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(FARM_TYPE, SpaceFarmType.EMPTY));

    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.SPACE_FARM.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        SpaceFarmBlockEntity blockEntity = (SpaceFarmBlockEntity) level.getBlockEntity(pos);
        if(level.isClientSide() || blockEntity == null) {
            return InteractionResult.SUCCESS;
        }


        if(blockEntity.cropState == null) {
            if(stack.is(Items.DIRT)) {
                setFarmState(state, pos, level, SpaceFarmType.DIRT);
                level.playSound(null, pos, Blocks.DIRT.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS);
                stack.shrink(1);
                return InteractionResult.SUCCESS;

            } else if (stack.is(Items.WATER_BUCKET)) {
                setFarmState(state, pos, level, SpaceFarmType.WATER);
                this.updateNearSpaceFarm(state, pos, level);

                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS);

                player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                return InteractionResult.CONSUME;

            }

            if (state.getValue(FARM_TYPE) == SpaceFarmType.FARMLAND && stack.getItem() instanceof BlockItem blockItem) {
                if(blockItem.getBlock() instanceof CropBlock cropBlock) {
                    blockEntity.setCrop(cropBlock);
                    stack.shrink(1);

                }
            }

        } else  {

             if(stack.is(ItemTags.HOES)) {
                CropBlock block = (CropBlock) blockEntity.cropState.getBlock();
                if(block.isMaxAge(blockEntity.cropState)) {

                    List<ItemStack> drops = blockEntity.cropState.getDrops(new LootParams.Builder((ServerLevel) level)
                            .withParameter(LootContextParams.TOOL, stack)
                            .withParameter(LootContextParams.BLOCK_STATE, blockEntity.cropState)
                            .withParameter(LootContextParams.ORIGIN, player.position()));

                    for(ItemStack drop : drops) {
                        ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), drop);
                        level.addFreshEntity(entity);
                    }

                    //We replant the crop
                    blockEntity.setCrop(block);
                    stack.setDamageValue(stack.getDamageValue() + 1);
                    level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);


                }
            } else if (stack.is(Items.BONE_MEAL)) {
                 blockEntity.performBoneMeal();
                 stack.shrink(1);
                 return InteractionResult.CONSUME;
             }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    public void updateNearSpaceFarm(BlockState ourState, BlockPos pos, Level level) {
        for(int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
            for(int z = pos.getZ() - 1; z <= pos.getZ() + 1; z++) {
                BlockPos checkPos = new BlockPos(x, pos.getY(), z);
                BlockState checkState = level.getBlockState(checkPos);

                if(!checkState.hasProperty(FARM_TYPE)) {
                    continue;
                }

                if(checkState.getValue(SpaceFarmBlock.FARM_TYPE) == SpaceFarmType.WATER && ourState.getValue(SpaceFarmBlock.FARM_TYPE) == SpaceFarmType.DIRT) {
                    setFarmState(ourState, pos, level, SpaceFarmType.FARMLAND);

                } else if (checkState.getValue(SpaceFarmBlock.FARM_TYPE) == SpaceFarmType.DIRT && ourState.getValue(SpaceFarmBlock.FARM_TYPE) == SpaceFarmType.WATER) {
                    setFarmState(checkState, checkPos, level, SpaceFarmType.FARMLAND);
                }
            }
        }
    }

    public void setFarmState(BlockState blockState, BlockPos pos, Level level, SpaceFarmType farmType) {
        blockState = blockState.setValue(FARM_TYPE, farmType);
        level.setBlockAndUpdate(pos, blockState);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        updateNearSpaceFarm(state, pos, level);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack destroyedWith) {
        super.playerDestroy(level, player, pos, state, blockEntity, destroyedWith);

        switch (state.getValue(FARM_TYPE)) {
            case DIRT -> {
                ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.DIRT));
                level.addFreshEntity(itemEntity);
            }
            case WATER -> {
                Stellaris.LOG.error("ee");
                level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                //ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.WATER_BUCKET));
                //level.addFreshEntity(itemEntity);
            }
            case FARMLAND -> {
                if(blockEntity instanceof SpaceFarmBlockEntity spaceFarmBlockEntity && spaceFarmBlockEntity.cropState != null) {
                    Block cropBlock = spaceFarmBlockEntity.cropState.getBlock();
                    ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(cropBlock.asItem()));
                    level.addFreshEntity(itemEntity);
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FARM_TYPE);
    }

    public enum SpaceFarmType implements StringRepresentable {
        EMPTY,
        DIRT,
        WATER,
        FARMLAND;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
