package com.supaham.supervisor.monitoring;

import static com.supaham.commons.utils.RuntimeUtils.getAllocatedMemory;
import static com.supaham.commons.utils.RuntimeUtils.getFreeMemory;
import static com.supaham.commons.utils.RuntimeUtils.getMaximumMemory;
import static com.supaham.commons.utils.RuntimeUtils.getUsedMemory;

import com.google.common.base.Strings;

import com.supaham.commons.bukkit.Colors;
import com.supaham.commons.bukkit.text.FancyMessage;
import com.supaham.commons.bukkit.text.MessagePart;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ali on 17/07/2015.
 */
public class MemoryMonitor implements Meterable {

    private final Monitor monitor;

    private static String formatPercent(int percent, boolean discourageHigh) {
        ChatColor color;
        if (percent <= 25) {
            color = discourageHigh ? ChatColor.GREEN : ChatColor.DARK_RED;
        } else if (percent <= 50) {
            color = discourageHigh ? ChatColor.DARK_GREEN : ChatColor.RED;
        } else if (percent <= 75) {
            color = discourageHigh ? ChatColor.RED : ChatColor.DARK_GREEN;
        } else {
            color = discourageHigh ? ChatColor.DARK_RED : ChatColor.GREEN;
        }
        return ChatColor.GRAY + " (" + color + percent + ChatColor.GRAY + "%%)";
    }

    public MemoryMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override public FancyMessage getMeter() {
        FancyMessage message = new FancyMessage();
        final int allocated = getAllocatedMemory();
        final int allocatedAmount = allocated * Monitor.METER_LENGTH / getMaximumMemory();
        int usedAmount = getUsedMemory() * Monitor.METER_LENGTH / getMaximumMemory();
        int freeAmount = allocatedAmount - usedAmount;
        int unallocatedAmount = Monitor.METER_LENGTH - allocatedAmount;

        MessagePart tooltip = new MessagePart();
        tooltip.tooltip(Colors._yellow("Used:       ").blue(getUsedMemory() + "MB").gray(" (" + getUsedMemory() * 100 / allocated + "%%)").toString(),
            Colors._yellow("Free:       ").blue(getFreeMemory() + "MB").gray(" (" + getFreeMemory() * 100 / allocated + "%%)").toString(),
            Colors._yellow("Allocated: ").blue(allocated + "MB").append(formatPercent(allocated * 100 / getMaximumMemory(), true)).toString(),
            Colors._yellow("Max:        ").blue(getMaximumMemory() + "MB").toString());
        message.append(Strings.repeat(Monitor.METER_CHAR, usedAmount)).color(ChatColor.RED).applyHoverEvent(tooltip);
        message.append(Strings.repeat(Monitor.METER_CHAR, freeAmount)).color(ChatColor.DARK_GREEN).applyHoverEvent(tooltip);
        message.append(Strings.repeat(Monitor.METER_CHAR, unallocatedAmount)).color(ChatColor.GREEN).applyHoverEvent(tooltip);
        return message;
    }

    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            FancyMessage message = new FancyMessage().safeAppend("[&eMEM&r]: [").append(getMeter()).safeAppend("&r]");
            message.send(sender);
        } else {
            sender.sendMessage("[MEM]: ");
            sender.sendMessage("  Used: " + getUsedMemory() + "MB");
            sender.sendMessage("  Free: " + getFreeMemory() + "MB");
            sender.sendMessage("  Allocated: " + getAllocatedMemory() + "MB");
            sender.sendMessage("  Max: " + getMaximumMemory() + "MB");
        }
    }
}
