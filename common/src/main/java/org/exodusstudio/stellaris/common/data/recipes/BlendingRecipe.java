package org.exodusstudio.stellaris.common.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.recipes.input.BlenderInput;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BlendingRecipe implements Recipe<BlenderInput> {
    private final List<SizedIngredient> ingredients;
    private final ItemStackTemplate result;
    private final int energy;
    private final int duration;

    private @Nullable PlacementInfo placementInfo;

    public BlendingRecipe(List<SizedIngredient> ingredients, ItemStackTemplate result, int energy, int duration) {
        this.ingredients = List.copyOf(ingredients);
        this.result = result;
        this.energy = energy;
        this.duration = duration;
    }

    public static BlendingRecipe display(List<Ingredient> units, ItemStackTemplate result) {
        Map<Ingredient, Integer> counts = new LinkedHashMap<>();
        for (Ingredient unit : units) {
            counts.merge(unit, 1, Integer::sum);
        }

        List<SizedIngredient> grouped = new ArrayList<>(counts.size());
        counts.forEach((ingredient, count) -> grouped.add(new SizedIngredient(ingredient, count)));

        return new BlendingRecipe(grouped, result, 0, 0);
    }

    public List<SizedIngredient> ingredients() {
        return ingredients;
    }

    public ItemStackTemplate result() {
        return result;
    }

    public int energy() {
        return energy;
    }

    public int duration() {
        return duration;
    }

    @Override
    public boolean matches(BlenderInput input, Level level) {
        return input.contents().canCraft(this, null);
    }

    @Override
    public @NotNull ItemStack assemble(BlenderInput input) {
        return result.create();
    }

    public int energyCost() {
        return energy == 0 ? Stellaris.CONFIG.machineConfig.blenderEnergyPerCraft : energy;
    }

    public int blendingTime() {
        return duration == 0 ? Stellaris.CONFIG.machineConfig.blenderTicksPerCraft : duration;
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
    public RecipeSerializer<? extends Recipe<BlenderInput>> getSerializer() {
        return RecipesRegistry.BLENDING_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<BlenderInput>> getType() {
        return RecipesRegistry.BLENDING_TYPE.get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (placementInfo == null) {
            List<Ingredient> units = new ArrayList<>();
            for (SizedIngredient sized : ingredients) {
                for (int unit = 0; unit < sized.count(); unit++) {
                    units.add(sized.ingredient());
                }
            }

            placementInfo = PlacementInfo.create(units);
        }

        return placementInfo;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public record SizedIngredient(Ingredient ingredient, int count) {

        public static final MapCodec<SizedIngredient> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
                Codec.intRange(1, 64).optionalFieldOf("count", 1).forGetter(SizedIngredient::count)
        ).apply(instance, SizedIngredient::new));

        public static final Codec<SizedIngredient> CODEC = Codec.withAlternative(
                MAP_CODEC.codec(), Ingredient.CODEC, ingredient -> new SizedIngredient(ingredient, 1));

        public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SizedIngredient::ingredient,
                ByteBufCodecs.VAR_INT, SizedIngredient::count,
                SizedIngredient::new);
    }

    public static class Serializer {

        public static final MapCodec<BlendingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SizedIngredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(BlendingRecipe::ingredients),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(BlendingRecipe::result),
                Codec.INT.optionalFieldOf("energy", 0).forGetter(BlendingRecipe::energy),
                Codec.INT.optionalFieldOf("duration", 0).forGetter(BlendingRecipe::duration)
        ).apply(instance, BlendingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BlendingRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), BlendingRecipe::ingredients,
                ItemStackTemplate.STREAM_CODEC, BlendingRecipe::result,
                ByteBufCodecs.VAR_INT, BlendingRecipe::energy,
                ByteBufCodecs.VAR_INT, BlendingRecipe::duration,
                BlendingRecipe::new);

        public static RecipeSerializer<BlendingRecipe> create() {
            return new RecipeSerializer<>(CODEC, STREAM_CODEC);
        }
    }
}
