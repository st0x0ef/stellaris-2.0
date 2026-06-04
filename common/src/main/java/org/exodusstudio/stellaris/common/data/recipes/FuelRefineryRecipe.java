package org.exodusstudio.stellaris.common.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import dev.architectury.fluid.FluidStackTemplate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.blocks.entities.machines.FuelRefineryBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.input.FluidInput;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;

public record FuelRefineryRecipe(FluidStackTemplate ingredientStack, FluidStackTemplate fuelStack, FluidStackTemplate dieselStack,
                                 int energy) implements Recipe<FluidInput> {

    public static RecipeType<FuelRefineryRecipe> Type = RecipesRegistry.FUEL_REFINERY_TYPE.get();

    @Override
    public boolean matches(FluidInput input, Level level) {
        SingleFluidStorage storage = ((FuelRefineryBlockEntity) input.entity()).getIngredientTank();
        FluidStack stack = storage.getFluidInTank(0);
        return stack.getFluid().isSame(ingredientStack.fluid().value()) && stack.getAmount() >= ingredientStack.amount();
    }

    @Override
    public ItemStack assemble(FluidInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<FluidInput>> getSerializer() {
        return RecipesRegistry.FUEL_REFINERY_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<FluidInput>> getType() {
        return RecipesRegistry.FUEL_REFINERY_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static class Serializer {

        private static final MapCodec<FuelRefineryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStackTemplate.CODEC.fieldOf("ingredient").forGetter(FuelRefineryRecipe::ingredientStack),
                FluidStackTemplate.CODEC.fieldOf("fuel").forGetter(FuelRefineryRecipe::fuelStack),
                FluidStackTemplate.CODEC.fieldOf("diesel").forGetter(FuelRefineryRecipe::dieselStack),
                Codec.INT.fieldOf("energyContainer").forGetter(FuelRefineryRecipe::energy)
        ).apply(instance, FuelRefineryRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FuelRefineryRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {
            recipe.ingredientStack().write(buf);
            recipe.fuelStack().write(buf);
            recipe.dieselStack().write(buf);
            buf.writeInt(recipe.energy());
        }, buf -> new FuelRefineryRecipe(FluidStackTemplate.read(buf), FluidStackTemplate.read(buf), FluidStackTemplate.read(buf), buf.readInt()));

        public static RecipeSerializer<FuelRefineryRecipe> create() {
            return new RecipeSerializer<>(CODEC, STREAM_CODEC);
        }
    }
}
