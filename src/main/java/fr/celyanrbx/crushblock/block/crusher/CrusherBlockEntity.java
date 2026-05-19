package fr.celyanrbx.crushblock.block.crusher;

import fr.celyanrbx.crushblock.block.ModBlockEntities;
import fr.celyanrbx.crushblock.recipe.CrushingRecipe;
import fr.celyanrbx.crushblock.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CrusherBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOT    = 0;
    public static final int OUTPUT_SLOTS  = 8;
    public static final int TOTAL_SLOTS   = 9;

    public static final int MAX_ENERGY    = 100_000;
    public static final int ENERGY_PER_TICK = 200;

    private int progress    = 0;
    private int maxProgress = 100;

    public final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == INPUT_SLOT;
        }
    };

    public final EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate) setChanged();
            return received;
        }
    };

    private final IItemHandler inputHandler = new RangedWrapper(inventory, INPUT_SLOT, INPUT_SLOT + 1) {
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };

    private final IItemHandler outputHandler = new RangedWrapper(inventory, INPUT_SLOT + 1, TOTAL_SLOTS) {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    };

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == null)           return inventory;
        if (side == Direction.DOWN) return outputHandler;
        return inputHandler;
    }

    public IEnergyStorage getEnergyHandler() { return energyStorage; }

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.crushblock.crusher");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new CrusherMenu(id, playerInventory, this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrusherBlockEntity be) {
        if (level.isClientSide()) return;

        Optional<RecipeHolder<CrushingRecipe>> recipeHolder = be.getCurrentRecipe();

        if (recipeHolder.isPresent()
                && be.canOutput(recipeHolder.get().value().outputs())
                && be.hasEnoughEnergy()) {

            be.maxProgress = recipeHolder.get().value().processingTime();

            // Consomme l'énergie
            be.energyStorage.extractEnergy(ENERGY_PER_TICK, false);
            be.progress++;

            if (be.progress >= be.maxProgress) {
                be.craft(recipeHolder.get().value());
                be.progress = 0;
            }
            setChanged(level, pos, state);
        } else {
            be.progress = 0;
        }
    }

    private boolean hasEnoughEnergy() {
        return energyStorage.getEnergyStored() >= ENERGY_PER_TICK;
    }

    private Optional<RecipeHolder<CrushingRecipe>> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        ItemStack input = inventory.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(
                ModRecipes.CRUSHER_TYPE.get(),
                new SingleRecipeInput(input),
                level
        );
    }

    private boolean canOutput(List<ItemStack> outputs) {
        for (ItemStack output : outputs) {
            boolean canPlace = false;
            for (int i = 1; i < TOTAL_SLOTS; i++) {
                ItemStack existing = inventory.getStackInSlot(i);
                if (existing.isEmpty()) { canPlace = true; break; }
                if (ItemStack.isSameItemSameComponents(existing, output)
                        && existing.getCount() + output.getCount() <= existing.getMaxStackSize()) {
                    canPlace = true; break;
                }
            }
            if (!canPlace) return false;
        }
        return true;
    }

    private void craft(CrushingRecipe recipe) {
        for (ItemStack output : recipe.outputs())
            placeInOutputSlot(output.copy());
        ItemStack input = inventory.getStackInSlot(INPUT_SLOT);
        input.shrink(1);
        inventory.setStackInSlot(INPUT_SLOT, input);
    }

    private void placeInOutputSlot(ItemStack result) {
        for (int i = 1; i < TOTAL_SLOTS; i++) {
            ItemStack existing = inventory.getStackInSlot(i);
            if (existing.isEmpty()) { inventory.setStackInSlot(i, result); return; }
            if (ItemStack.isSameItemSameComponents(existing, result)
                    && existing.getCount() + result.getCount() <= existing.getMaxStackSize()) {
                existing.grow(result.getCount());
                inventory.setStackInSlot(i, existing);
                return;
            }
        }
    }

    public int getProgress()      { return progress; }
    public int getMaxProgress()   { return maxProgress; }
    public int getEnergy()        { return energyStorage.getEnergyStored(); }
    public int getMaxEnergy()     { return energyStorage.getMaxEnergyStored(); }
    public float getEnergyPercent() {
        return getMaxEnergy() == 0 ? 0f : (float) getEnergy() / getMaxEnergy();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("energy", energyStorage.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        energyStorage.receiveEnergy(tag.getInt("energy"), false);
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++)
            container.setItem(i, inventory.getStackInSlot(i));
        Containers.dropContents(this.level, this.worldPosition, container);
    }
}
