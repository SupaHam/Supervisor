package com.supaham.supervisor.bukkit.monitoring;

import com.google.common.base.Strings;

import com.supaham.commons.bukkit.Colors;
import com.supaham.commons.bukkit.TickerTask;
import com.supaham.commons.bukkit.utils.ChatUtils;
import com.supaham.commons.bukkit.utils.EventUtils;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.event.HoverEvent.Action;
import net.kyori.text.format.TextColor;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.LinkedList;

public class TPSMonitor extends TickerTask implements Meterable {

    public static final float MAX_TPS = 20f;

    private final Monitor monitor;
    private final LinkedList<Float> loggedTps = new LinkedList<>();
    private final int interval;

    private long lastCall = System.currentTimeMillis() - 3000L;
    private float tps = 20.0F;

    public TPSMonitor(Monitor monitor) {
        this(monitor, 40);
    }

    public TPSMonitor(Monitor monitor, int interval) {
        super(monitor.getContainer().getPlugin(), 0, interval);
        this.monitor = monitor;
        this.interval = interval;
    }

    @Override
    public void run() {
        long currTime = System.currentTimeMillis();

        long spentTime = (currTime - this.lastCall) / 1000L;
        if (spentTime == 0L) {
            spentTime = 1L;
        }

        float calculatedTps = (float) (this.interval / spentTime);
        if (calculatedTps > MAX_TPS) {
            calculatedTps = MAX_TPS;
        }
        EventUtils.callEvent(new TPSChangeEvent(this.tps, calculatedTps));
        this.tps = calculatedTps;
        addTps(this.tps);
        this.lastCall = System.currentTimeMillis();
    }

    public Component getMeter() {
        return getMeter(false); // TODO configure
    }

    public Component getMeter(boolean averageTPS) {
        final float avgTPS = getAverageTps();
        float tps = averageTPS ? avgTPS : this.tps;
        int tpsAmount = (int) (tps / MAX_TPS * Monitor.METER_LENGTH);
        HoverEvent hoverEvent;
        {
            TextComponent text =
                TextComponent.of("Last TPS: ").color(TextColor.YELLOW).append(TextComponent.of(this.tps + " TPS \n").color(TextColor.BLUE))
                    .append(TextComponent.of("Avg. TPS: ").color(TextColor.YELLOW).append(TextComponent.of(avgTPS + " TPS").color(TextColor.BLUE)));
            hoverEvent = new HoverEvent(Action.SHOW_TEXT, text);
        }
        Component message = TextComponent.of(Strings.repeat(Monitor.METER_CHAR, tpsAmount)).color(TextColor.GREEN).hoverEvent(hoverEvent)
            .append(TextComponent.of(Strings.repeat(Monitor.METER_CHAR, Monitor.METER_LENGTH - tpsAmount)).color(TextColor.RED)
                .hoverEvent(hoverEvent));
        return message;
    }

    private void addTps(final float tps) {
        if (tps <= MAX_TPS) {
            this.loggedTps.add(tps);
        }
        if (this.loggedTps.size() > 10) {
            this.loggedTps.poll();
        }
    }

    public final float getAverageTps() {
        float amount = 0.0F;
        for (Float f : this.loggedTps) {
            amount += f;
        }
        return amount / this.loggedTps.size();
    }

    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            TextComponent component = TextComponent.of("[")
                .append(TextComponent.of("TPS").color(TextColor.YELLOW).content("TPS"))
                .append(ChatUtils.forceResetStyles(TextComponent.of("]: [")))
                .append(getMeter())
                .append(ChatUtils.forceResetStyles(TextComponent.of("]")));
            ChatUtils.sendComponent(sender, component);
        } else {
            sender.sendMessage("[TPS]:");
            sender.sendMessage("  Last TPS: " + this.tps);
            sender.sendMessage("  Avg. TPS: " + getAverageTps());
        }
    }

    public static final class TPSChangeEvent extends Event {

        private final float oldTPS;
        private final float newTPS;

        private static final HandlerList handlerList = new HandlerList();

        public TPSChangeEvent(float oldTPS, float newTPS) {
            this.oldTPS = oldTPS;
            this.newTPS = newTPS;
        }

        public float getOldTPS() {
            return oldTPS;
        }

        public float getNewTPS() {
            return newTPS;
        }

        @Override
        public HandlerList getHandlers() { return handlerList; }

        public static HandlerList getHandlerList() { return handlerList; }
    }
}
