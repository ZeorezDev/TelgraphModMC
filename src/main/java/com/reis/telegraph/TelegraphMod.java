package com.reis.telegraph;

import com.reis.telegraph.registration.ModBlocks;
import com.reis.telegraph.registration.ModBlockEntities;
import com.reis.telegraph.registration.ModSounds;
import com.reis.telegraph.commands.TelegraphCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("telegraph")
public class TelegraphMod {
    public TelegraphMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Sadece gerekli kayitlar
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);

        // Komutlar icin en sade kayit yontemi
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRegisterCommands);
    }

    // Bu metod sadece komutu sisteme ekler, sol tıkla işi olmaz
    public void onRegisterCommands(RegisterCommandsEvent event) {
        TelegraphCommand.register(event.getDispatcher());
    }
}