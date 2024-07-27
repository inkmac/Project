package com.example.examplemod.event;

import com.example.examplemod.connection.DataServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.IOException;

public class PlayerLoginEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // 获取登录的玩家
        EntityPlayer player = event.player;

        DataServer.setChatSender(player);
        try {
            DataServer.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
