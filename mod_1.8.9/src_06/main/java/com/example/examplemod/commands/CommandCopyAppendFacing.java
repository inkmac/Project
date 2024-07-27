package com.example.examplemod.commands;

import com.example.examplemod.PlayerDirectionHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class CommandCopyAppendFacing extends CommandBase implements ICommand {

    private final PlayerDirectionHandler playerDirectionHandler = new PlayerDirectionHandler();

    @Override
    public String getCommandName() {
        return "copy_append_facing"; // 主要命令名称
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/copy_append_facing"; // 命令用法
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>(); // 没有别名，返回空列表
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            String facing = playerDirectionHandler.getPlayerFacing(player);

            // 获取剪贴板内容
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            try {
                String currentContent = (String) clipboard.getData(DataFlavor.stringFlavor);
                if (currentContent == null || currentContent.isEmpty()) {
                    currentContent = "";
                }
                currentContent += ", " + facing;
                StringSelection stringSelection = new StringSelection(currentContent.trim());
                clipboard.setContents(stringSelection, null);

                sender.addChatMessage(new ChatComponentText("当前朝向已追加到剪贴板: " + currentContent.trim()));
            } catch (Exception e) {
                sender.addChatMessage(new ChatComponentText("无法访问剪贴板内容。"));
            }
        }
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

