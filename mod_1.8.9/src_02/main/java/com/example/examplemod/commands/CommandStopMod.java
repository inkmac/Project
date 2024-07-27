package com.example.examplemod.commands;

import com.example.examplemod.PlayerDirectionHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class CommandStopMod extends CommandBase implements ICommand {

    @Override
    public String getCommandName() {
        return "stop_mod"; // 主要命令名称
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stop_mod"; // 命令用法
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();  // 没有别名，返回空列表
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        PlayerDirectionHandler.loggingEnable = false;
        sender.addChatMessage(new ChatComponentText("Mod 已停止！"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return this.getCommandName().compareTo(o.getCommandName());
    }
}
