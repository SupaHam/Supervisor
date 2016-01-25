package com.supaham.supervisor.bukkit;

import com.supaham.commons.bukkit.CommonPlugin;
import com.supaham.commons.bukkit.CommonSettings;
import com.supaham.supervisor.report.OutputFormat;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;

import java.util.UUID;

import javax.annotation.Nonnull;

import pluginbase.config.annotation.Comment;
import pluginbase.config.annotation.NoTypeKey;

/**
 * Nucleus Bukkit configuration class.
 */
@NoTypeKey
public final class SupervisorSettings extends CommonSettings {

    @Comment({"The unique uuid for this server. Please do not change this unless told to do so by a Supervisor developer."})
    private UUID uuid = UUID.randomUUID();
    private Defaults defaults = new Defaults();

    private SupervisorSettings() {} // PB initializer

    public SupervisorSettings(@Nonnull CommonPlugin plugin) {
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
