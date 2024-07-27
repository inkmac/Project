package com.example.examplemod.event;

import com.example.examplemod.connection.DataServer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerDirectionHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        String facing = getPlayerFacing(player);
        String blockName = getPlayerLookingAt(player);
        String data = facing + " | " + blockName;

        DataServer.updateData(data);
    }

    private String getPlayerFacing(EntityPlayer player) {
        // 获取玩家的朝向
        float yaw = player.rotationYaw;  // 水平朝向
        float pitch = player.rotationPitch;  // 竖直朝向

        // 调整 yaw 到 (-180, 180)
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
        Vec3 end = start.addVector(look.xCoord * 4.5, look.yCoord * 4.5, look.zCoord * 4.5);  // 4.5 blocks distance
        MovingObjectPosition rayTraceResult = world.rayTraceBlocks(start, end, false, true, false);

        if (rayTraceResult != null && rayTraceResult.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = rayTraceResult.getBlockPos();
            IBlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            ItemStack itemStack = new ItemStack(block, 1, block.getMetaFromState(blockState));
            String blockName = itemStack.getDisplayName();

            return blockName;
        }

        return "Nothing";
    }
}

