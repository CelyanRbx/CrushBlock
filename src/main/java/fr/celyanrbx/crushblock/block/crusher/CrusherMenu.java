package fr.celyanrbx.crushblock.block.crusher;

import fr.celyanrbx.crushblock.init.ModBlocks;
import fr.celyanrbx.crushblock.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CrusherMenu extends AbstractContainerMenu {
    private final CrusherBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    private final DataSlot progressData    = DataSlot.standalone();
    private final DataSlot maxProgressData = DataSlot.standalone();
    private final DataSlot energyHigh      = DataSlot.standalone();
    private final DataSlot energyLow       = DataSlot.standalone();

    public CrusherMenu(int id, Inventory playerInventory, CrusherBlockEntity be) {
        super(ModMenuTypes.CRUSHER_MENU.get(), id);
        this.blockEntity = be;
        this.levelAccess = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());
        addBlockEntitySlots(be.inventory);
        addPlayerSlots(playerInventory);
        addDataSlot(progressData);
        addDataSlot(maxProgressData);
        addDataSlot(energyHigh);
        addDataSlot(energyLow);
    }

    public CrusherMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, getBlockEntity(playerInventory, buf));
    }

    private static CrusherBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf buf) {
        BlockEntity be = inv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof CrusherBlockEntity crusher) return crusher;
        throw new IllegalStateException("Block entity incorrect : " + be);
    }

    private void addBlockEntitySlots(IItemHandler handler) {
        this.addSlot(new SlotItemHandler(handler, CrusherBlockEntity.INPUT_SLOT, 81, 22));
        for (int i = 0; i < 4; i++)
            this.addSlot(new OutputSlot(handler, i + 1, 54 + i * 18, 58));
        for (int i = 0; i < 4; i++)
            this.addSlot(new OutputSlot(handler, i + 5, 54 + i * 18, 76));
    }

    private void addPlayerSlots(Inventory playerInventory) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        10 + col * 18, 116 + row * 18));
        for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInventory, col, 10 + col * 18, 174));
    }

    private static class OutputSlot extends SlotItemHandler {
        public OutputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }
        @Override
        public boolean mayPlace(ItemStack stack) { return false; }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();
            if (index < 9) {
                if (!this.moveItemStackTo(slotStack, 9, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(slotStack, 0, 1, false))
                    return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return returnStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player,
                ModBlocks.CRUSHER.get());
    }

    @Override
    public void broadcastChanges() {
        progressData.set(blockEntity.getProgress());
        maxProgressData.set(blockEntity.getMaxProgress());
        // Énergie encodée sur 2 shorts (DataSlot = short 16bit)
        int energy = blockEntity.getEnergy();
        energyHigh.set((energy >> 16) & 0xFFFF);
        energyLow.set(energy & 0xFFFF);
        super.broadcastChanges();
    }

    public int getProgress()    { return progressData.get(); }
    public int getMaxProgress() { return maxProgressData.get(); }
    public float getProgressPercent() {
        int max = getMaxProgress();
        return max == 0 ? 0f : (float) getProgress() / max;
    }

    public int getEnergy() {
        return ((energyHigh.get() & 0xFFFF) << 16) | (energyLow.get() & 0xFFFF);
    }
    public int getMaxEnergy() { return CrusherBlockEntity.MAX_ENERGY; }
    public float getEnergyPercent() {
        return getMaxEnergy() == 0 ? 0f : (float) getEnergy() / getMaxEnergy();
    }
}
