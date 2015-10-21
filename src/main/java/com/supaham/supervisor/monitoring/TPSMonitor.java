package com.supaham.supervisor.monitoring;

import com.google.common.base.Strings;

import com.supaham.commons.bukkit.Colors;
import com.supaham.commons.bukkit.TickerTask;
import com.supaham.commons.bukkit.text.FancyMessage;
import com.supaham.commons.bukkit.text.MessagePart;
import com.supaham.commons.bukkit.utils.EventUtils;

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

    @Override public void run() {
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

    public FancyMessage getMeter() {
        return getMeter(false); // TODO configure
    }

    public FancyMessage getMeter(boolean averageTPS) {
        FancyMessage message = new FancyMessage();
        final float avgTPS = getAverageTps();
        float tps = averageTPS ? avgTPS : this.tps;
        int tpsAmount = (int) (tps / MAX_TPS * Monitor.METER_LENGTH);
        MessagePart tooltip = new MessagePart();
        tooltip.tooltip(Colors._yellow("Last TPS: ").blue(this.tps + " TPS").toString(),
            Colors._yellow("Avg. TPS: ").blue(avgTPS + " TPS").toString());
        message.append(Strings.repeat(Monitor.METER_CHAR, tpsAmount)).color(ChatColor.GREEN).applyHoverEvent(tooltip);
        message.append(Strings.repeat(Monitor.METER_CHAR, Monitor.METER_LENGTH - tpsAmount)).color(ChatColor.RED).applyHoverEvent(tooltip);
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
            FancyMessage message = new FancyMessage().safeAppend("[&eTPS&r]: [").append(getMeter()).safeAppend("&r]");
            message.send(sender);
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
