package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerDirectionHandler {
    private static final long LOG_INTERVAL_MS = 500;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        String facing = getPlayerFacing(player);
        String blockName = getPlayerLookingAt(player);

        Logger.logToFile(facing, "facing.txt", LOG_INTERVAL_MS);
        Logger.logToFile(blockName, "block_name.txt", LOG_INTERVAL_MS);
    }

    private String getPlayerFacing(EntityPlayer player) {
        // 获取玩家的朝向
        float yaw = player.rotationYaw;  // 水平朝向
        float pitch = player.rotationPitch;  // 竖直朝向

        return "(" + yaw + ", " + pitch + ")";
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

            return "Looking at: " + blockName;
        }

        return "Looking at: Nothing";
    }

}

