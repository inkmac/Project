package com.example.examplemod;

import com.example.examplemod.commands.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = MyMod.MODID, version = MyMod.VERSION)
public class MyMod {
    public static final String MODID = "mymod";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerDirectionHandler());
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        // 注册命令
        event.registerServerCommand(new CommandStartMod());
        event.registerServerCommand(new CommandStopMod());
        event.registerServerCommand(new CommandRestartMod());
        event.registerServerCommand(new CommandCopyFacing());
        event.registerServerCommand(new CommandCopyAppendFacing());
    }
}
