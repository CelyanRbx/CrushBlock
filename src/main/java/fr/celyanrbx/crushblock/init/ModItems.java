package fr.celyanrbx.crushblock.init;

import fr.celyanrbx.crushblock.CrushBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrushBlock.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}