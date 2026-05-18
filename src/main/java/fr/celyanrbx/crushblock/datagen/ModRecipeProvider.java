package fr.celyanrbx.crushblock.datagen;

import fr.celyanrbx.crushblock.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CRUSHER.get(), 1)
                .pattern("III")
                .pattern("SPS")
                .pattern("SSS")
                .define('P', Blocks.PISTON)
                .define('S', Blocks.STONE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_crusher", has(ModBlocks.CRUSHER.get())).save(output);
    }
}
