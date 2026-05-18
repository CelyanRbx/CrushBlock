package fr.celyanrbx.crushblock.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public record CrushingRecipe(
        Ingredient input,
        List<ItemStack> outputs,
        int processingTime
) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput inv, Level level) {
        return input.test(inv.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider provider) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRUSHER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CRUSHER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CrushingRecipe> {

        public static final MapCodec<CrushingRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC.fieldOf("ingredient")
                                .forGetter(CrushingRecipe::input),
                        ItemStack.CODEC.listOf(1, 8).fieldOf("outputs")
                                .forGetter(CrushingRecipe::outputs),
                        net.minecraft.util.ExtraCodecs.POSITIVE_INT
                                .optionalFieldOf("processingTime", 100)
                                .forGetter(CrushingRecipe::processingTime)
                ).apply(instance, CrushingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CrushingRecipe::input,
                        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list(8)), CrushingRecipe::outputs,
                        ByteBufCodecs.INT, CrushingRecipe::processingTime,
                        CrushingRecipe::new
                );

        @Override
        public MapCodec<CrushingRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
