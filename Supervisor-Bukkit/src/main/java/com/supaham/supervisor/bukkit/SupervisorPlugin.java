package com.supaham.supervisor.bukkit;

import com.google.common.base.Preconditions;

import com.supaham.commons.bukkit.SimpleCommonPlugin;
import com.supaham.commons.bukkit.TickerTask;
import com.supaham.commons.bukkit.utils.SerializationUtils;
import com.supaham.commons.bukkit.worldedit.CommandsManager;
import com.supaham.supervisor.Supervisor;
import com.supaham.supervisor.bukkit.SupervisorSettings.Defaults;
import com.supaham.supervisor.bukkit.contexts.BukkitServerInfoContext;
import com.supaham.supervisor.bukkit.contexts.BukkitWorldsContext;
import com.supaham.supervisor.bukkit.contexts.PluginsContext;
import com.supaham.supervisor.bukkit.contexts.LogContext;
import com.supaham.supervisor.contexts.SystemPropertiesContext;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextRegistry;
import com.supaham.supervisor.report.OutputFormat;
import com.supaham.supervisor.report.Report;
import com.supaham.supervisor.report.ReportSpecifications;
import com.supaham.supervisor.report.ReportSpecifications.ReportSpecsBuilder;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import pluginbase.config.datasource.yaml.YamlDataSource;
import pluginbase.logging.PluginLogger;

/**
 * Created by Ali on 14/10/2015.
 */
public class SupervisorPlugin extends SimpleCommonPlugin<SupervisorPlugin> {

    private static SupervisorPlugin instance;

    private CommandsManager commandsManager;

    private SupervisorSettings settings = new SupervisorSettings();
    private BukkitContextRegistry contextRegistry;
    private Supervisor supervisor;

    public static SupervisorPlugin get() {
        return instance;
    }

    public static PluginLogger log() {
        return get().getLog();
    }

    public static Report createReport(ReportSpecifications specs) {
        return Supervisor.createReport(specs);
    }

    public SupervisorPlugin() {
        super(SupervisorPlugin.class, "sv");
        Preconditions.checkState(instance == null, "SupervisorPlugin already initialized.");
        instance = this;
    }

    @Override public void onEnable() {
        super.onEnable();
        if (!loadSettings()) {
            return;
        }

        this.commandsManager = new SupervisorCommandsManager(this);
        this.contextRegistry = new BukkitContextRegistry();
        registerDefaultContexts();
        supervisor = new Supervisor(getLogger(), this.contextRegistry);

        new TickerTask(this, 1) {
            @Override public void run() {
                commandsManager.registerCommands();
            }
        }.start();
    }
    
    public boolean loadSettings() {
        YamlDataSource yaml;
        try {
            yaml = SerializationUtils.yaml(new File(getDataFolder(), "supervisor.yml")).build();
            SerializationUtils.loadOrCreateProperties(getLog(), yaml, this.settings);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void registerDefaultContexts() {
        registerContext(this, new SystemPropertiesContext());
        registerContext(this, new BukkitServerInfoContext());
        registerContext(this, new PluginsContext());
        registerContext(this, new BukkitWorldsContext());
        registerContext(this, new LogContext());
    }

    /**
     * Returns a new {@link ReportSpecsBuilder} instance with default settings. This builder sets only the following properties, requiring you to do
     * the rest:
     * <ul>
     * <li>{@link ReportSpecsBuilder#contextRegistry(ReportContextRegistry)}</li>
     * <li>{@link ReportSpecsBuilder#title(String)}</li>
     * <li>{@link ReportSpecsBuilder#format(OutputFormat)}</li>
     * <li>{@link ReportSpecsBuilder#reportLevel(int)}</li>
     * </ul>
     *
     * @return new builder
     */
    public ReportSpecsBuilder createDefaultBuilder() {
        Defaults defaults = getSettings().getDefaults();
        return ReportSpecifications.builder().owner(getName()).supervisor(this.supervisor).contextRegistry(getContextRegistry()).title("Report")
            .format(defaults.getFormat()).reportLevel(defaults.getReportLevel());
    }

    public void registerContext(@Nonnull Plugin owner, @Nonnull ReportContext context) {
        getContextRegistry().register(owner, context);
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public SupervisorSettings getSettings() {
        return settings;
    }

    public BukkitContextRegistry getContextRegistry() {
        return contextRegistry;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }
}
