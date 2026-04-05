package com.reis.telegraph.registration;

import com.reis.telegraph.blocks.CableBlock;
import com.reis.telegraph.blocks.InsulatorBlock;
import com.reis.telegraph.blocks.RelayStationBlock;
import com.reis.telegraph.blocks.TelegraphBlock;
import com.reis.telegraph.blocks.TelegraphPoleBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "telegraph");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "telegraph");

    public static final RegistryObject<Block> TELEGRAPH_MACHINE = registerBlock("telegraph_machine",
            () -> new TelegraphBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f)));

    public static final RegistryObject<Block> CABLE_BLOCK = registerBlock("cable_block",
            () -> new CableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).strength(1.0f).noOcclusion()));

    public static final RegistryObject<Block> RELAY_STATION = registerBlock("relay_station",
            () -> new RelayStationBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.5f)));

    public static final RegistryObject<Block> TELEGRAPH_POLE = registerBlock("telegraph_pole",
            () -> new TelegraphPoleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0f)));

    public static final RegistryObject<Block> INSULATOR = registerBlock("insulator",
            () -> new InsulatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(1.0f).noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}