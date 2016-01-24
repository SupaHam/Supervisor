package com.supaham.supervisor.bukkit.contexts;

import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextEntry;

import java.io.File;
import java.io.IOException;

public class LogContext extends ReportContext {

    public LogContext() {
        super("log", "Server Log File", "1");
    }

    @Override
    public void run(ReportContextEntry contextEntry) {
        try {
            contextEntry.createPlainTextFile("latest.log", "Latest log file").appendFile(new File("logs/latest.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
