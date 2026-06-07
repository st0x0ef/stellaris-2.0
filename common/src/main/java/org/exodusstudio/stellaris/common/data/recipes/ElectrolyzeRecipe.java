package org.exodusstudio.stellaris.common.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.fluid.FluidStack;
import dev.architectury.fluid.FluidStackTemplate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectrolyzerBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.input.FluidInput;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;

import java.util.ArrayList;
import java.util.List;

public record ElectrolyzeRecipe(FluidStackTemplate ingredientStack, List<FluidStackTemplate> resultStacks, long energy) implements Recipe<FluidInput> {
    @Override
    public boolean matches(FluidInput container, Level level) {
        FluidStack stack = ((ElectrolyzerBlockEntity) container.entity()).ingredientTank.getFluidInTank(0);
        return stack.getFluid().isSame(ingredientStack.fluid().value()) && stack.getAmount() >= ingredientStack.amount();
    }

    @Override
    public ItemStack assemble(FluidInput container) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<FluidInput>> getSerializer() {
        return RecipesRegistry.ELECTROLYZE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<FluidInput>> getType() {
        return RecipesRegistry.ELECTROLYZE_RECIPE_TYPE.get();
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
        private static final Codec<FluidStackTemplate> FLUID_STACK_TEMPLATE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(stack -> BuiltInRegistries.FLUID.getKey(stack.fluid().value())),
                Codec.LONG.fieldOf("amount").forGetter(FluidStackTemplate::amount)
        ).apply(instance, (id, amount) -> {
            Holder<Fluid> fluid = BuiltInRegistries.FLUID.getValue(id).arch$holder();
            return FluidStackTemplate.of(fluid, amount);
        }));

        private static final MapCodec<ElectrolyzeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FLUID_STACK_TEMPLATE_CODEC.fieldOf("ingredient").forGetter(ElectrolyzeRecipe::ingredientStack),
                FLUID_STACK_TEMPLATE_CODEC.listOf(1, 2).fieldOf("results").forGetter(ElectrolyzeRecipe::resultStacks),
                Codec.LONG.fieldOf("energyContainer").forGetter(ElectrolyzeRecipe::energy)
        ).apply(instance, ElectrolyzeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStackTemplate>> FLUID_STACK_TEMPLATE_LIST_STREAM_CODEC =
                ByteBufCodecs.collection(ArrayList::new, FluidStackTemplate.STREAM_CODEC, 2);
        public static final StreamCodec<RegistryFriendlyByteBuf, ElectrolyzeRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {
            recipe.ingredientStack().write(buf);
            FLUID_STACK_TEMPLATE_LIST_STREAM_CODEC.encode(buf, recipe.resultStacks);
            buf.writeLong(recipe.energy);
        }, buf -> new ElectrolyzeRecipe(FluidStackTemplate.read(buf), FLUID_STACK_TEMPLATE_LIST_STREAM_CODEC.decode(buf), buf.readLong()));

        public static RecipeSerializer<ElectrolyzeRecipe> create() {
            return new RecipeSerializer<>(CODEC, STREAM_CODEC);
        }
    }
}
