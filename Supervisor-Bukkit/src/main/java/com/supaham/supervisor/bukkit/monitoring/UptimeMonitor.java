package com.supaham.supervisor.bukkit.monitoring;

import com.supaham.commons.bukkit.text.FancyMessage;
import com.supaham.commons.utils.TimeUtils;

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
            final FancyMessage message = new FancyMessage().safeAppend("[&eUptime&r]: ");
            final long uptime = getUptimeSeconds();
            ChatColor color;
            if(TimeUnit.SECONDS.toHours(uptime) < 12) {
                color = ChatColor.GREEN;
            } else if(TimeUnit.SECONDS.toHours(uptime) < 24) {
                color = ChatColor.DARK_GREEN;
            } else if(TimeUnit.SECONDS.toDays(uptime) <= 1) {
                color = ChatColor.RED;
            } else {
                color = ChatColor.DARK_RED;
            }
            message.then(getReadableUptime()).color(color).send(sender);
        } else {
            sender.sendMessage("[Uptime]: " + getReadableUptime());
        }
    }
}
