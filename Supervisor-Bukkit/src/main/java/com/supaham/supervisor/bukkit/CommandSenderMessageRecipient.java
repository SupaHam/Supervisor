package com.supaham.supervisor.bukkit;

import com.supaham.commons.bukkit.utils.ChatUtils;
import com.supaham.supervisor.service.MessageRecipient;

import net.kyori.text.TextComponent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.event.HoverEvent.Action;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;

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
            TextComponent component = TextComponent.of("An error has occurred.").color(TextColor.RED).decoration(TextDecoration.UNDERLINE, true)
                .hoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.of(message)));
            ChatUtils.sendComponent(this.commandSender, component);
        } else {
            this.commandSender.sendMessage(message);
        }
    }

    @Override public void error(@Nonnull Throwable throwable) {
        if(this.commandSender instanceof Player) {
            TextComponent component = TextComponent.of("An error has occurred, please report this to an administrator.").color(TextColor.RED)
                .decoration(TextDecoration.UNDERLINE, true)
                .hoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.of(throwable.getMessage())));
            ChatUtils.sendComponent(this.commandSender, component);
        } else {
            throwable.printStackTrace();
        }
    }
}
