package fr.celyanrbx.crushblock.block.crusher;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrusherScreen extends AbstractContainerScreen<CrusherMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("crushblock", "textures/gui/crusher_gui.png");

    // Flèche : position dans le GUI et taille du sprite
    private static final int ARROW_X      = 85;
    private static final int ARROW_Y      = 41;  // y dans le GUI
    private static final int ARROW_W      = 9;   // largeur totale flèche
    private static final int ARROW_H      = 14;  // hauteur totale flèche
    private static final int ARROW_U      = 176; // x du sprite dans la texture
    private static final int ARROW_V      = 41;  // y du sprite dans la texture

    public CrusherScreen(CrusherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth      = 176;
        this.imageHeight     = 196;
        this.titleLabelX     = 8;
        this.titleLabelY     = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 102;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width  - this.imageWidth)  / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Fond principal
        graphics.blit(TEXTURE, x, y, 0, 0, 176, 196, 196, 196);

        // Flèche animée : on dessine progressivement de haut en bas
        float progress = this.menu.getProgressPercent();
        int filledHeight = (int) (ARROW_H * progress); // 0 → 14px

        if (filledHeight > 0) {
            graphics.blit(TEXTURE,
                    x + ARROW_X,           // destination x
                    y + ARROW_Y,           // destination y
                    ARROW_U,               // source u (sprite dans texture)
                    ARROW_V,               // source v
                    ARROW_W,               // largeur à dessiner
                    filledHeight,          // hauteur progressivement remplie
                    196, 196               // taille totale texture
            );
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title,
                this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle,
                this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}