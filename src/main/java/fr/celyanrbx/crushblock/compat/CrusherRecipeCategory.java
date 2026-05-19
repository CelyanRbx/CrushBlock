package fr.celyanrbx.crushblock.compat;

import fr.celyanrbx.crushblock.CrushBlock;
import fr.celyanrbx.crushblock.block.crusher.CrusherBlockEntity;
import fr.celyanrbx.crushblock.init.ModBlocks;
import fr.celyanrbx.crushblock.recipe.CrushingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CrusherRecipeCategory implements IRecipeCategory<CrushingRecipe> {

    public static final RecipeType<CrushingRecipe> RECIPE_TYPE = RecipeType.create(
            "crushblock", "crushing", CrushingRecipe.class);
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrushBlock.MODID,
            "textures/gui/crusher_gui.png");

    private static final int WIDTH  = 176;
    private static final int HEIGHT = 120;

    private final IDrawable background;
    private final IDrawable icon;

    public CrusherRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 29, 5, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.CRUSHER.get()));
    }

    @Override
    public RecipeType<CrushingRecipe> getRecipeType() { return RECIPE_TYPE; }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.crushblock.crushing");
    }

    @Override
    public IDrawable getBackground() { return background; }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> outputs = recipe.outputs();

        builder.addSlot(RecipeIngredientRole.INPUT, 79, 26)
                .addIngredients(recipe.input());

        int[][] positions = {
                {44, 73}, {67, 73}, {91, 73}, {114, 73},
                {44, 96}, {67, 96}, {91, 96}, {114, 96}
        };

        for (int i = 0; i < outputs.size() && i < positions.length; i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT,
                            positions[i][0], positions[i][1])
                    .addItemStack(outputs.get(i));
        }
    }

    @Override
    public void draw(CrushingRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics graphics, double mouseX, double mouseY) {

        int ticks = recipe.processingTime();
        float seconds = ticks / 20f;
        Component timeText = Component.literal(
                String.format("%.1fs", seconds));

        Component energyText = Component.literal(CrusherBlockEntity.ENERGY_PER_TICK + " FE/t");

        graphics.drawString(
                net.minecraft.client.Minecraft.getInstance().font,
                energyText, 5, 5, 0xFF555555, false);

        graphics.drawString(
                net.minecraft.client.Minecraft.getInstance().font,
                timeText, 5, 15, 0xFF555555, false);
    }
}
