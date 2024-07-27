package com.example.examplemod.commands;

import com.example.examplemod.PlayerDirectionHandler;
import com.example.examplemod.logger.SocketLogger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class CommandRestartMod extends CommandBase implements ICommand {

    @Override
    public String getCommandName() {
        return "restart_mod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/restart_mod";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("Mod 正在重新启动！"));
        PlayerDirectionHandler.isPlayerDirectionActive = false;
        SocketLogger.close(sender);

        PlayerDirectionHandler.isPlayerDirectionActive = true;
        new Thread(() -> SocketLogger.init(sender)).start();
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
