package com.supaham.supervisor.monitoring;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.supaham.commons.bukkit.modules.CommonModule;
import com.supaham.commons.state.State;
import com.supaham.supervisor.SupervisorPlugin;

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

        SupervisorPlugin.get().getCommandsManager().getCommandGraph().registerMethods(this);
        setState(State.ACTIVE);
    }

    @Command(aliases = {"nm", "tps", "monitor", "mem", "memory"}, desc = "Print performance monitor.")
    @CommandPermissions("nucleus.monitor")
    public void nm(CommandSender sender) {
        this.memoryMonitor.send(sender);
        this.tpsMonitor.send(sender);
        this.uptimeMonitor.send(sender);
    }
}
