package org.exodusstudio.stellaris.neoforge.datagen;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.model.templates.StellarisModelTemplates;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class StellarisModelGenerator extends ModelProvider {
    private static final VariantMutator Z_ROT_180 = VariantMutator.Z_ROT.withValue(Quadrant.R180);
    private final ResourceManager resourceManager;
    public StellarisModelGenerator(PackOutput output, ResourceManager resourceManager) {
        super(output, Stellaris.MOD_ID);
        this.resourceManager = resourceManager;
    }

    private static ItemModelGenerators itemModels;
    private static BlockModelGenerators blockModels;

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
        StellarisModelGenerator.itemModels = itemModels;
        StellarisModelGenerator.blockModels = blockModels;

        blockModels.createTrivialCube(BlocksRegistry.MARS_IRON_ORE.block().get());
        blockModels.createTrivialCube(BlocksRegistry.MARS_WEATHERED_STONE.block().get());
        blockModels.createTrivialCube(BlocksRegistry.MARS_WEATHERED_SAND.block().get());
        blockModels.createTrivialCube(BlocksRegistry.MARS_REGOLITH.block().get());
        blockModels.createTrivialCube(BlocksRegistry.MARS_REGOLITH_BRICKS.block().get());
        createIcicle();
        createBrittleIce();
        createIceCrystal();

    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return super.getKnownBlocks().filter(holder -> resourceManager.getResource(holder.getKey().identifier().withPrefix("blockstates/").withSuffix(".json")).isEmpty());
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(itemHolder -> resourceManager.getResource(itemHolder.getKey().identifier().withPrefix("items/").withSuffix(".json")).isEmpty());
    }

    private void createIceCrystal() {
        createCrystalCluster(BlocksRegistry.ICE_CRYSTAL.block().get());
        blockModels.registerSimpleFlatItemModel(BlocksRegistry.ICE_CRYSTAL.block().get());
    }

    private void createIcicle() {
        Block block = BlocksRegistry.ICICLE.block().get();
        var generator = PropertyDispatch.initial(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS, BlockStateProperties.ATTACHED);
        Material topTexture = TextureMapping.getBlockTexture(block, "_top");
        Material sideTexture = TextureMapping.getBlockTexture(block, "_side");

        for (DripstoneThickness speleothemThickness : DripstoneThickness.values()) {
            String thicknessSuffix = "_" + speleothemThickness.getSerializedName();
            Material thicknessTexture = TextureMapping.getBlockTexture(block, thicknessSuffix);
            TextureMapping mapping = TextureMapping.cross(thicknessTexture);
            TextureMapping baseMapping = new TextureMapping()
                    .put(TextureSlot.CROSS, thicknessTexture)
                    .put(TextureSlot.TOP, topTexture)
                    .put(TextureSlot.SIDE, sideTexture);
            Identifier icicleTemplate = StellarisModelTemplates.ICICLE.createWithSuffix(block, thicknessSuffix, mapping, blockModels.modelOutput);
            Identifier icicleBaseTemplate = StellarisModelTemplates.ICICLE_BASE.createWithSuffix(block, "_base" + thicknessSuffix, baseMapping, blockModels.modelOutput);
            generator.select(Direction.DOWN, speleothemThickness, false, plainVariant(icicleTemplate));
            generator.select(Direction.DOWN, speleothemThickness, true, plainVariant(icicleBaseTemplate).with(BlockModelGenerators.UV_LOCK));
            generator.select(Direction.UP, speleothemThickness, false, plainVariant(icicleTemplate).with(StellarisModelGenerator.Z_ROT_180));
            generator.select(Direction.UP, speleothemThickness, true, plainVariant(icicleBaseTemplate).with(BlockModelGenerators.X_ROT_180));
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(generator));
        itemModels.declareCustomModelItem(BlocksRegistry.ICICLE.item().get());
    }

    private void createBrittleIce() {
        this.createCrackingIce(BlocksRegistry.BRITTLE_ICE.block().get());
        blockModels.registerSimpleItemModel(BlocksRegistry.BRITTLE_ICE.block().get(), ModelLocationUtils.getModelLocation(BlocksRegistry.BRITTLE_ICE.block().get(), "_0"));
    }

    private void createCrackingIce(final Block block) {
        blockModels.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block)
                                .with(
                                        PropertyDispatch.initial(BlockStateProperties.AGE_3)
                                                .select(0, plainVariant(blockModels.createSuffixedVariant(block, "_0", ModelTemplates.CUBE_ALL, TextureMapping::cube)))
                                                .select(1, plainVariant(blockModels.createSuffixedVariant(block, "_1", ModelTemplates.CUBE_ALL, TextureMapping::cube)))
                                                .select(2, plainVariant(blockModels.createSuffixedVariant(block, "_2", ModelTemplates.CUBE_ALL, TextureMapping::cube)))
                                                .select(3, plainVariant(blockModels.createSuffixedVariant(block, "_3", ModelTemplates.CUBE_ALL, TextureMapping::cube)))
                                )
                );
    }

    private void createCrystalCluster(final Block crytsalClusterBlock) {
        MultiVariant model = plainVariant(ModelTemplates.CROSS.create(crytsalClusterBlock, TextureMapping.cross(crytsalClusterBlock), blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(crytsalClusterBlock, model).with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));
    }
}
