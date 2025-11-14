package org.exodusstudio.stellaris.common.data.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectrolyzerBlockEntity;
import org.exodusstudio.stellaris.common.data.recipe.input.FluidInput;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;

import java.util.ArrayList;
import java.util.List;

public record ElectrolyzeRecipe(FluidStack ingredientStack, List<FluidStack> resultStacks,
                               int energy) implements Recipe<FluidInput> {

    @Override
    public boolean matches(FluidInput container, Level level) {
        SingleFluidStorage tank = ((ElectrolyzerBlockEntity) container.entity()).ingredientTank;
        FluidStack stack = tank.getFluidInTank(0);
        return stack.isFluidEqual(ingredientStack) && stack.getAmount() >= ingredientStack.getAmount();
    }

    @Override
    public ItemStack assemble(FluidInput container, HolderLookup.Provider registries) {
        return null;
    }

    @Override
    public RecipeSerializer<ElectrolyzeRecipe> getSerializer() {
        return RecipesRegistry.ELECTROLYZE_SERIALIZER.get();
    }

    @Override
    public RecipeType<ElectrolyzeRecipe> getType() {
        return RecipesRegistry.ELECTROLYZE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CAMPFIRE;
    }

    public static class Serializer implements RecipeSerializer<ElectrolyzeRecipe> {

        private static final MapCodec<ElectrolyzeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStack.CODEC.fieldOf("ingredient").forGetter(ElectrolyzeRecipe::ingredientStack),
                FluidStack.CODEC.listOf(1, 2).fieldOf("results").forGetter(ElectrolyzeRecipe::resultStacks),
                Codec.INT.fieldOf("energyContainer").forGetter(ElectrolyzeRecipe::energy)
        ).apply(instance, ElectrolyzeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> FLUID_STACK_LIST_STREAM_CODEC =
                ByteBufCodecs.collection(ArrayList::new, FluidStack.STREAM_CODEC, 2);
        private static final StreamCodec<RegistryFriendlyByteBuf, ElectrolyzeRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {
            recipe.ingredientStack().write(buf);
            FLUID_STACK_LIST_STREAM_CODEC.encode(buf, recipe.resultStacks);
            buf.writeInt(recipe.energy);
        }, buf -> new ElectrolyzeRecipe(FluidStack.read(buf), FLUID_STACK_LIST_STREAM_CODEC.decode(buf), buf.readInt()));

        @Override
        public MapCodec<ElectrolyzeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ElectrolyzeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}