package fr.celyanrbx.crushblock.init;

import fr.celyanrbx.crushblock.CrushBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrushBlock.MODID);

    public static final Supplier<CreativeModeTab> CRUSHBLOCK = CREATIVE_MODE_TABS.register("crushblock",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.CRUSHER.get()))
                    .title(Component.translatable("item_group.crushblock.crushblock"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.CRUSHER.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}