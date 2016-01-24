package com.supaham.supervisor.contexts;

import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.SimpleReportFile.PlainTextReportFile;

import java.util.Map;
import java.util.Map.Entry;

public abstract class ServerInfoContext extends ReportContext {

    public ServerInfoContext() {
        super("server-info", "Server Information", "1");
    }

    @Override
    public void run(ReportContextEntry entry) {
        entry.append("cpu", getCPUUsage());
        entry.append("free_memory", getFreeMemory());
        entry.append("used_memory", getUsedMemory());
        entry.append("allocated_memory", getAllocatedMemory());
        entry.append("maximum_memory", getMaximumMemory());
        entry.append("minecraft_version", getMinecraftVersion());
        entry.append("implementation", getImplementation());
        entry.append("online_player_count", getOnlinePlayerCount());
        PlainTextReportFile file = entry.createPlainTextFile("server.properties", "Server Properties");
        if(getServerProperties() instanceof Map) {
            for (Entry<?, ?> mapEntry : ((Map<?, ?>) getServerProperties()).entrySet()) {
                file.append(mapEntry.toString()).appendLineBreak();
            }
        } else {
            file.append(getServerProperties());
        }
    }

    protected abstract Object getCPUUsage();

    protected abstract Object getFreeMemory();

    protected abstract Object getUsedMemory();

    protected abstract Object getAllocatedMemory();

    protected abstract Object getMaximumMemory();

    protected abstract Object getMinecraftVersion();

    protected abstract Object getImplementation();

    protected abstract Object getOnlinePlayerCount();

    protected abstract Object getServerProperties();
}
