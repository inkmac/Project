package com.example.examplemod;

import com.example.examplemod.commands.CommandStartMod;
import com.example.examplemod.commands.CommandStopMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerDirectionHandler());
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        // 注册命令
        event.registerServerCommand(new CommandStartMod());
        event.registerServerCommand(new CommandStopMod());
    }
}
