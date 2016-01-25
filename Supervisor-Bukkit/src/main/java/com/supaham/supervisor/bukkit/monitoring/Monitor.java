package com.supaham.supervisor.bukkit.monitoring;

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.supaham.commons.bukkit.modules.CommonModule;
import com.supaham.commons.state.State;
import com.supaham.supervisor.bukkit.SupervisorPlugin;

import org.bukkit.command.CommandSender;

public class Monitor extends CommonModule {

    public static final int METER_LENGTH = 50;
    public static final String METER_CHAR = "|";

    private final MemoryMonitor memoryMonitor;
    private final TPSMonitor tpsMonitor;
    private final UptimeMonitor uptimeMonitor;

    public Monitor() {
        super(SupervisorPlugin.get().getModuleContainer());
        this.memoryMonitor = new MemoryMonitor(this);
        this.tpsMonitor = new TPSMonitor(this);
        this.uptimeMonitor = new UptimeMonitor(this);
        registerTask(this.tpsMonitor);

        SupervisorPlugin.get().getCommandsManager().builder().registerMethods(this);
        setState(State.ACTIVE);
    }

    @Command(aliases = {"nm", "tps", "monitor", "mem", "memory"}, desc = "Print performance monitor.")
    @Require("nucleus.monitor")
    public void nm(CommandSender sender) {
        this.memoryMonitor.send(sender);
        this.tpsMonitor.send(sender);
        this.uptimeMonitor.send(sender);
    }
}
