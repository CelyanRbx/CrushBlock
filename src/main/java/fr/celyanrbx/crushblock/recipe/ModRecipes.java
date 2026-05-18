package fr.celyanrbx.crushblock.recipe;

import fr.celyanrbx.crushblock.CrushBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CrushBlock.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, CrushBlock.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrushingRecipe>>
            CRUSHER_SERIALIZER =
            SERIALIZERS.register("crushing", CrushingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrushingRecipe>>
            CRUSHER_TYPE =
            TYPES.register("crushing", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return CrushBlock.MODID + ":crushing";
                }
            });
}
