package org.exodusstudio.stellaris.common.world.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.exodusstudio.stellaris.common.registries.ProcessorsRegistry;

/**
 * Skips placing structure blocks where the target world position is air, so partial
 * shapes (e.g. meteors) only embed into existing terrain instead of floating.
 */
public class VoidProcessor extends StructureProcessor {

    public static final MapCodec<VoidProcessor> CODEC = MapCodec.unit(VoidProcessor::new);

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader worldView, BlockPos pos, BlockPos blockPos,
                                                             StructureTemplate.StructureBlockInfo structureBlockInfoLocal,
                                                             StructureTemplate.StructureBlockInfo structureBlockInfoWorld,
                                                             StructurePlaceSettings structurePlacementData) {
        if (structureBlockInfoWorld.state().is(Blocks.STRUCTURE_VOID)) {
            return null;
        }

        if (worldView.getBlockState(structureBlockInfoWorld.pos()).isAir()) {
            return null;
        }

        return structureBlockInfoWorld;
    }

    @Override
    protected StructureProcessorType<VoidProcessor> getType() {
        return ProcessorsRegistry.STRUCTURE_VOID_PROCESSOR.get();
    }
}
