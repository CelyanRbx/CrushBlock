package fr.celyanrbx.crushblock.block.crusher;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class CrusherScreen extends AbstractContainerScreen<CrusherMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("crushblock", "textures/gui/crusher_gui.png");

    private static final int ARROW_X = 85, ARROW_Y = 41;
    private static final int ARROW_W = 9,  ARROW_H = 14;
    private static final int ARROW_U = 176, ARROW_V = 41;

    private static final int BAR_X = 8,  BAR_Y = 21;
    private static final int BAR_W = 8,  BAR_H = 52;
    private static final int BAR_U = 185, BAR_V = 21;

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

        graphics.blit(TEXTURE, x, y, 0, 0, 176, 196, 196, 196);

        int progressH = (int)(ARROW_H * this.menu.getProgressPercent());
        if (progressH > 0) {
            graphics.blit(TEXTURE,
                    x + ARROW_X, y + ARROW_Y,
                    ARROW_U, ARROW_V,
                    ARROW_W, progressH,
                    196, 196);
        }

        float energyPct = this.menu.getEnergyPercent();
        int energyH = (int)(BAR_H * energyPct);
        if (energyH > 0) {
            graphics.blit(TEXTURE,
                    x + BAR_X + 1,
                    y + BAR_Y + 1 + (BAR_H - energyH),
                    BAR_U, BAR_V + (BAR_H - energyH),
                    BAR_W, energyH,
                    196, 196);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        int x = (this.width  - this.imageWidth)  / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (mouseX >= x + BAR_X && mouseX <= x + BAR_X + 10
                && mouseY >= y + BAR_Y && mouseY <= y + BAR_Y + 54) {
            graphics.renderTooltip(this.font,
                    List.of(
                            Component.literal(this.menu.getEnergy() + " / "
                                    + this.menu.getMaxEnergy() + " FE")
                    ),
                    java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title,
                this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle,
                this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}