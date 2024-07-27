package com.example.examplemod.commands;

import com.example.examplemod.PlayerDirectionHandler;
import com.example.examplemod.logger.SocketLogger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CommandStartMod extends CommandBase implements ICommand {

    @Override
    public String getCommandName() {
        return "start_mod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/start_mod";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
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
