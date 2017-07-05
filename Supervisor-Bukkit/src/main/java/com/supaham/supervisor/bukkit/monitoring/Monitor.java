package com.supaham.supervisor.bukkit.monitoring;

import com.supaham.commons.bukkit.modules.CommonModule;
import com.supaham.commons.state.State;
import com.supaham.supervisor.bukkit.SupervisorPlugin;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;

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

        SupervisorPlugin.get().getCommandsManager().registerCommand(new MonitorCommands());
        setState(State.ACTIVE);
    }
    
    public class MonitorCommands extends BaseCommand {

        @CommandAlias("nm|tps|monitor|mem|memory")
        @CommandPermission("nucleus.monitor")
        public void nm(CommandSender sender) {
            Monitor.this.memoryMonitor.send(sender);
            Monitor.this.tpsMonitor.send(sender);
            Monitor.this.uptimeMonitor.send(sender);
        }
    }
}
