package org.exodusstudio.stellaris.common.data.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.Stellaris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom recipe loader for the electrolyzer
 */
public class ElectrolyzeRecipeData extends SimpleJsonResourceReloadListener<ElectrolyzeRecipeData.ElectrolyzeRecipe> {

    public static Map<Fluid, ElectrolyzeRecipe> RECIPES = new HashMap<>();

    public ElectrolyzeRecipeData() {
        super(ElectrolyzeRecipe.CODEC, FileToIdConverter.json("electrolyze"));
    }

    @Override
    protected void apply(Map<ResourceLocation, ElectrolyzeRecipe> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        object.forEach((key, entry) -> {

            if (!RECIPES.containsKey(entry.ingredientStack.getFluid())) {
                Stellaris.LOG.error("Registering Electrolyze for {}", entry.ingredientStack.getFluid().arch$registryName());
                RECIPES.put(entry.ingredientStack.getFluid(),  entry);
            }
        });

    }

    public record ElectrolyzeRecipe(FluidStack ingredientStack, List<FluidStack> resultStacks,
                                    int energy) {

        private static final Codec<ElectrolyzeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FluidStack.CODEC.fieldOf("ingredient").forGetter(ElectrolyzeRecipe::ingredientStack),
                FluidStack.CODEC.listOf(1, 2).fieldOf("results").forGetter(ElectrolyzeRecipe::resultStacks),
                Codec.INT.fieldOf("energyContainer").forGetter(ElectrolyzeRecipe::energy)
        ).apply(instance, ElectrolyzeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> FLUID_STACK_LIST_STREAM_CODEC =
                ByteBufCodecs.collection(ArrayList::new, FluidStack.STREAM_CODEC, 2);


        public static final StreamCodec<RegistryFriendlyByteBuf, ElectrolyzeRecipe> STREAM_CODEC =
                StreamCodec.composite(FluidStack.STREAM_CODEC, ElectrolyzeRecipe::ingredientStack, FluidStack.STREAM_CODEC.apply(ByteBufCodecs.list(2)), ElectrolyzeRecipe::resultStacks, ByteBufCodecs.INT, ElectrolyzeRecipe::energy, ElectrolyzeRecipe::new);

    }
}