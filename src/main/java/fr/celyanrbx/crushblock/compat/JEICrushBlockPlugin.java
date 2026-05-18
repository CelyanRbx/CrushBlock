package fr.celyanrbx.crushblock.compat;

import fr.celyanrbx.crushblock.block.crusher.CrusherScreen;
import fr.celyanrbx.crushblock.init.ModBlocks;
import fr.celyanrbx.crushblock.recipe.CrushingRecipe;
import fr.celyanrbx.crushblock.recipe.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@JeiPlugin
public class JEICrushBlockPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("crushblock", "jei_plugin");
    }

    // ── Enregistrement de la catégorie ────────────────────────────────────────
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CrusherRecipeCategory(guiHelper));
    }

    // ── Enregistrement des recettes ───────────────────────────────────────────
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        assert Minecraft.getInstance().level != null;

        List<CrushingRecipe> recipes = Minecraft.getInstance().level
                .getRecipeManager()
                .getAllRecipesFor(ModRecipes.CRUSHER_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        registration.addRecipes(CrusherRecipeCategory.RECIPE_TYPE, recipes);
    }

    // ── Lien GUI ↔ catégorie (clic R/U dans le GUI) ───────────────────────────
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                CrusherScreen.class,
                // Zone cliquable = zone de la flèche dans le GUI
                // x=79, y=44 dans le GUI → relatif à la fenêtre
                79, 44, 22, 12,
                CrusherRecipeCategory.RECIPE_TYPE
        );
    }

    // ── Lien bloc ↔ catégorie (clic sur le bloc dans JEI) ────────────────────
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.CRUSHER.get()),
                CrusherRecipeCategory.RECIPE_TYPE
        );
    }
}
