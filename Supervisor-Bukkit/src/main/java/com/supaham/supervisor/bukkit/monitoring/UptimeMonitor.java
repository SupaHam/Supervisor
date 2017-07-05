package com.supaham.supervisor.bukkit.monitoring;

import com.supaham.commons.bukkit.utils.ChatUtils;
import com.supaham.commons.utils.TimeUtils;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class UptimeMonitor {

    private static final long initializedAt = System.currentTimeMillis();
    private final Monitor monitor;

    public UptimeMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
    
    public long getUptimeSeconds() {
        return (System.currentTimeMillis() - initializedAt) / 1000;
    }
    
    public String getReadableUptime() {
        return TimeUtils.toString(getUptimeSeconds(), false);
    }

    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            final long uptime = getUptimeSeconds();
            TextColor color;
            if(TimeUnit.SECONDS.toHours(uptime) < 12) {
                color = TextColor.GREEN;
            } else if(TimeUnit.SECONDS.toHours(uptime) < 24) {
                color = TextColor.DARK_GREEN;
            } else if(TimeUnit.SECONDS.toDays(uptime) <= 1) {
                color = TextColor.RED;
            } else {
                color = TextColor.DARK_RED;
            }
            Component component = TextComponent.of("[")
                .append(TextComponent.of("Uptime").color(TextColor.YELLOW))
                .append(ChatUtils.forceResetStyles(TextComponent.of("]: ")))
                .append(TextComponent.of(getReadableUptime()).color(color));
            ChatUtils.sendComponent(sender, component);
        } else {
            sender.sendMessage("[Uptime]: " + getReadableUptime());
        }
    }
}
