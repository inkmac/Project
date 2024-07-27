package com.example.examplemod.event;

import com.example.examplemod.connection.DataServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.IOException;

public class PlayerLogoutEventHandler {
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        try {
            DataServer.closeServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
