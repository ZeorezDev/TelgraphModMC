package com.reis.telegraph.registration;

import com.reis.telegraph.blocks.TelegraphBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "telegraph");

    public static final RegistryObject<BlockEntityType<TelegraphBlockEntity>> TELEGRAPH_ENTITY =
            BLOCK_ENTITIES.register("telegraph_entity",
                    () -> BlockEntityType.Builder.of(TelegraphBlockEntity::new,
                            ModBlocks.TELEGRAPH_MACHINE.get()).build(null));
}