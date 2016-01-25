package com.supaham.supervisor.bukkit.contexts;

import com.supaham.supervisor.bukkit.SupervisorPlugin;
import com.supaham.supervisor.report.AbstractReportContextEntry;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportSpecifications;

import java.io.File;
import java.io.IOException;

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
            try {
                createPlainTextFile("config.yml", "Supervisor Config").appendFile(new File(supervisorPlugin.getDataFolder(), "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
