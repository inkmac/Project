package com.example.examplemod;

import com.example.examplemod.logger.SocketLogger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerDirectionHandler {
    public static boolean isModEnable = false;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!isModEnable) {
            return;
        }

        EntityPlayer player = event.player;

        String facing = getPlayerFacing(player);
        String blockName = getPlayerLookingAt(player);

        player.addChatMessage(new ChatComponentText(facing + " | " + blockName));

        if (SocketLogger.isSocketConnecting) {
            SocketLogger.send(facing + " | " + blockName);
        }
    }

    private String getPlayerFacing(EntityPlayer player) {
        // 获取玩家的朝向
        float yaw = player.rotationYaw;  // 水平朝向
        float pitch = player.rotationPitch;  // 竖直朝向

        // 规范化 yaw 到 (-180, 180)
        while (yaw <= -180) yaw += 360;
        while (yaw > 180) yaw -= 360;

        // 保留一位小数
        float roundedYaw = Math.round(yaw * 10) / 10.0f;
        float roundedPitch = Math.round(pitch * 10) / 10.0f;

        return "(" + roundedYaw + ", " + roundedPitch + ")";
    }

    private String getPlayerLookingAt(EntityPlayer player) {
        World world = player.worldObj;
        Vec3 start = player.getPositionEyes(1.0F);
        Vec3 look = player.getLook(1.0F);
        Vec3 end = start.addVector(look.xCoord * 5, look.yCoord * 5, look.zCoord * 5); // 5 blocks distance
        MovingObjectPosition rayTraceResult = world.rayTraceBlocks(start, end, false, true, false);

        if (rayTraceResult != null && rayTraceResult.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = rayTraceResult.getBlockPos();
            IBlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            String blockName = block.getLocalizedName(); // Get the block's localized name

            return blockName;
        }

        return "Nothing";
    }

}

