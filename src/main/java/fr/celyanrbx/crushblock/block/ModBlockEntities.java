package fr.celyanrbx.crushblock.block;

import fr.celyanrbx.crushblock.block.crusher.CrusherBlockEntity;
import fr.celyanrbx.crushblock.init.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "crushblock");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrusherBlockEntity>> CRUSHER_BE =
            BLOCK_ENTITIES.register("crusher_be",
                    () -> BlockEntityType.Builder
                            .of(CrusherBlockEntity::new, ModBlocks.CRUSHER.get())
                            .build(null));

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CRUSHER_BE.get(),
                (be, side) -> be.getItemHandler(side)
        );
    }
}