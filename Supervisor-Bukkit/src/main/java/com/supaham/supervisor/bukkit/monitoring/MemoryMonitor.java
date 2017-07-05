package com.supaham.supervisor.bukkit.monitoring;

import static com.supaham.commons.utils.RuntimeUtils.getAllocatedMemory;
import static com.supaham.commons.utils.RuntimeUtils.getFreeMemory;
import static com.supaham.commons.utils.RuntimeUtils.getMaximumMemory;
import static com.supaham.commons.utils.RuntimeUtils.getUsedMemory;

import com.google.common.base.Strings;

import com.supaham.commons.bukkit.Colors;
import com.supaham.commons.bukkit.utils.ChatUtils;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.event.HoverEvent.Action;
import net.kyori.text.format.TextColor;

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
        return ChatColor.GRAY + " (" + color + percent + ChatColor.GRAY + "%)";
    }

    public MemoryMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public Component getMeter() {
        final int allocated = getAllocatedMemory();
        final int allocatedAmount = allocated * Monitor.METER_LENGTH / getMaximumMemory();
        int usedAmount = getUsedMemory() * Monitor.METER_LENGTH / getMaximumMemory();
        int freeAmount = allocatedAmount - usedAmount;
        int unallocatedAmount = Monitor.METER_LENGTH - allocatedAmount;

        HoverEvent tooltip;
        {
            Component component = TextComponent.of("Used:       ").color(TextColor.YELLOW)
                .append(TextComponent.of(getUsedMemory() + "MB").color(TextColor.BLUE))
                .append(TextComponent.of(" (" + getUsedMemory() * 100 / allocated + "%\n").color(TextColor.GRAY))

                .append(TextComponent.of("Free:       ").color(TextColor.YELLOW))
                .append(TextComponent.of(getFreeMemory() + "MB").color(TextColor.BLUE))
                .append(TextComponent.of(" (" + getFreeMemory() * 100 / allocated + "%\n").color(TextColor.GRAY))

                .append(TextComponent.of("Allocated: ").color(TextColor.YELLOW))
                .append(TextComponent.of(allocated + "MB").color(TextColor.BLUE))
                .append(TextComponent.of(formatPercent(allocated * 100 / getMaximumMemory(), true)).color(TextColor.GRAY))

                .append(TextComponent.of("Max:        ").color(TextColor.YELLOW))
                .append(TextComponent.of(getMaximumMemory() + "MB").color(TextColor.BLUE));
            tooltip = new HoverEvent(Action.SHOW_TEXT, component);
        }
        Component component = TextComponent.of(Strings.repeat(Monitor.METER_CHAR, usedAmount)).color(TextColor.RED).hoverEvent(tooltip)
            .append(TextComponent.of(Strings.repeat(Monitor.METER_CHAR, freeAmount)).color(TextColor.DARK_GREEN).hoverEvent(tooltip))
            .append(TextComponent.of(Strings.repeat(Monitor.METER_CHAR, unallocatedAmount)).color(TextColor.GREEN).hoverEvent(tooltip));
        return component;
    }

    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            Component component = TextComponent.of("[").append(TextComponent.of("MEM").color(TextColor.YELLOW))
                .append(ChatUtils.forceResetStyles(TextComponent.of("]: [")))
                .append(getMeter()).append(ChatUtils.forceResetStyles(TextComponent.of("]")));
            ChatUtils.sendComponent(sender, component);
        } else {
            sender.sendMessage("[MEM]: ");
            sender.sendMessage("  Used: " + getUsedMemory() + "MB");
            sender.sendMessage("  Free: " + getFreeMemory() + "MB");
            sender.sendMessage("  Allocated: " + getAllocatedMemory() + "MB");
            sender.sendMessage("  Max: " + getMaximumMemory() + "MB");
        }
    }
}
