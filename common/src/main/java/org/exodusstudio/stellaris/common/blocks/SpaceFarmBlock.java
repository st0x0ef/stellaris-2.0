package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.base.BaseTickingEntityBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.SpaceFarmBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

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
                state = state.setValue(FARM_TYPE, SpaceFarmType.DIRT);
                level.setBlockAndUpdate(pos, state);
                return InteractionResult.SUCCESS;

            } else if (stack.is(Items.WATER_BUCKET)) {
                state = state.setValue(FARM_TYPE, SpaceFarmType.WATER);
                level.setBlockAndUpdate(pos, state);
                return InteractionResult.CONSUME;

            }
//TODO change this with actual water logic
            if(state.getValue(FARM_TYPE) == SpaceFarmType.DIRT && stack.is(ItemTags.HOES))  {
                Stellaris.LOG.error("FARMLAND");
                state = state.setValue(FARM_TYPE, SpaceFarmType.FARMLAND);
                level.setBlockAndUpdate(pos, state);
                return InteractionResult.CONSUME;
            }

            if (state.getValue(FARM_TYPE) == SpaceFarmType.FARMLAND && stack.getItem() instanceof BlockItem blockItem) {
                if(blockItem.getBlock() instanceof CropBlock cropBlock) {
                    blockEntity.setCrop(cropBlock);
                }
            }
        } else  {
            if(player.isShiftKeyDown()) {
                player.sendSystemMessage(Component.literal("Crop: " + blockEntity.cropState.toString()));
            }

        }





        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
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
