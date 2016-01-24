package com.supaham.supervisor.bukkit;

import com.supaham.commons.bukkit.text.FancyMessage;
import com.supaham.supervisor.service.MessageRecipient;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Created by Ali on 01/11/2015.
 */
public class CommandSenderMessageRecipient implements MessageRecipient {

    private final CommandSender commandSender;

    public CommandSenderMessageRecipient(@Nonnull CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Override public void printSuccess(@Nonnull String message) {
        this.commandSender.sendMessage(ChatColor.GREEN + message);
    }

    @Override public void printError(@Nonnull String message) {
        if (this.commandSender instanceof Player) {
            new FancyMessage().safeAppend("&c&nAn error has occurred.").tooltip(message).send(this.commandSender);
        } else {
            this.commandSender.sendMessage(message);
        }
    }

    @Override public void error(@Nonnull Throwable throwable) {
        if(this.commandSender instanceof Player) {
            new FancyMessage().safeAppend("&c&nAn error has occurred, please report this to an administrator.").tooltip(throwable.getMessage())
                .send(this.commandSender);
        } else {
            throwable.printStackTrace();
        }
    }
}
