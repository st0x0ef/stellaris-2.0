package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TickingBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class SpaceFarmBlockEntity extends BlockEntity implements TickingBlockEntity {

    public BlockState cropState = null;

    public SpaceFarmBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntitiesRegistry.SPACE_FARM.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if(cropState != null) {
            output.store("crop", BlockState.CODEC, cropState);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        cropState = input.read("crop", BlockState.CODEC).orElse(null);
    }

    @Override
    public void tick(Level level, BlockState state) {

        if(cropState == null) {
            return;
        }

        Block block = cropState.getBlock();

        if(block instanceof CropBlock cropBlock && level instanceof ServerLevel) {

            int age = cropBlock.getAge(cropState);
            if (age < cropBlock.getMaxAge()) {
                float growthSpeed = 1.0F;

                RandomSource random = level.getRandom();

                //TODO it's growing very fast so we need to see
                if (random.nextInt((int)(75.0f / growthSpeed) + 1) == 0) {
                    this.cropState = cropBlock.getStateForAge(age + 1);
                }
            }
        }
    }

    public void setCrop(CropBlock crop) {
        this.cropState = crop.defaultBlockState();
        setChanged();
    }
}
