package com.supaham.supervisor.bukkit.contexts;

import com.google.common.collect.ImmutableMap;

import com.supaham.supervisor.bukkit.SupervisorPlugin;
import com.supaham.supervisor.report.AbstractReportContextEntry;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportFile;
import com.supaham.supervisor.report.ReportSpecifications;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;
import com.supaham.supervisor.report.SimpleReportFile.PlainTextReportFile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

public class SupervisorContext extends ReportContext {

    private final SupervisorPlugin supervisorPlugin;

    public SupervisorContext(SupervisorPlugin supervisorPlugin) {
        super("supervisor", "Supervisor", supervisorPlugin.getDescription().getVersion());
        this.supervisorPlugin = supervisorPlugin;
    }

    @Override
    public ReportContextEntry createEntry(@Nonnull ReportSpecifications specs) {
        return new SupervisorContextEntry(this, specs);
    }

    private final class SupervisorContextEntry extends AbstractReportContextEntry {

        public SupervisorContextEntry(@Nonnull ReportContext parentContext, @Nonnull ReportSpecifications reportSpecifications) {
            super(parentContext, reportSpecifications);
        }

        @Override
        public void run() {
            if (getReportSpecifications().hasArgument("perms")) {
                perms();
            }
            if (getReportLevel() > ReportLevel.BRIEF) {
                config();
            }
        }

        private void perms() {
            ReportFile file = createFile("permissions", "Online players' permissions");
            for (Player player : Bukkit.getOnlinePlayers()) {
                List<ImmutableMap<String, Boolean>> perms = player.getEffectivePermissions().stream()
                    .map(s -> ImmutableMap.of(s.getPermission(), s.getValue())).collect(Collectors.toList());
                file.append(player.getUniqueId().toString(), perms);
            }
        }

        private void config() {
            try {
                createPlainTextFile("config.yml", "Supervisor Config").appendFile(new File(supervisorPlugin.getDataFolder(), "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
