package com.supaham.supervisor;

import com.supaham.supervisor.report.OutputFormat;
import com.supaham.supervisor.report.ReportSpecs.ReportLevel;

import java.util.UUID;

import javax.annotation.Nonnull;

import pluginbase.config.SerializationRegistrar;
import pluginbase.config.annotation.Comment;
import pluginbase.config.annotation.NoTypeKey;
import pluginbase.config.annotation.SerializableAs;
import pluginbase.plugin.PluginBase;
import pluginbase.plugin.Settings;

/**
 * Nucleus Bukkit configuration class.
 */
@SerializableAs("SupervisorConfig")
public class SupervisorConfig extends Settings {

    static {
        SerializationRegistrar.registerClass(SupervisorConfig.class);
        SerializationRegistrar.registerClass(SupervisorConfig.Defaults.class);
    }

    @Comment({"The unique uuid for this server. Please do not change this unless told to do so by a Supervisor developer."})
    private UUID uuid = UUID.randomUUID();
    private Defaults defaults = new Defaults();

    private SupervisorConfig() {} // Used for initialization.

    public SupervisorConfig(@Nonnull PluginBase plugin) {
        super(plugin);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Defaults getDefaults() {
        return defaults;
    }
    
    @NoTypeKey
    public static final class Defaults {
        private OutputFormat format = OutputFormat.JSON;
        private int reportLevel = ReportLevel.BRIEF;

        public OutputFormat getFormat() {
            return format;
        }

        public int getReportLevel() {
            return reportLevel;
        }
    }
}
