package fr.celyanrbx.crushblock;

import com.mojang.logging.LogUtils;
import fr.celyanrbx.crushblock.block.ModBlockEntities;
import fr.celyanrbx.crushblock.block.crusher.CrusherScreen;
import fr.celyanrbx.crushblock.init.ModBlocks;
import fr.celyanrbx.crushblock.init.ModItems;
import fr.celyanrbx.crushblock.init.ModMenuTypes;
import fr.celyanrbx.crushblock.init.ModTabs;
import fr.celyanrbx.crushblock.recipe.ModRecipes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(CrushBlock.MODID)
public class CrushBlock {
    public static final String MODID = "crushblock";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CrushBlock(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.TYPES.register(modEventBus);
        modEventBus.addListener(ModBlockEntities::registerCapabilities);

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
        }
    }
}
