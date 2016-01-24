package com.supaham.supervisor.bukkit.contexts;

import com.supaham.commons.utils.MapBuilder;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PluginsContext extends ReportContext {

    public PluginsContext() {
        super("plugins", "Plugins", "1");
    }

    @Override public void run(ReportContextEntry entry) {
        entry.append("count", Bukkit.getPluginManager().getPlugins().length);
        List<Object> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.add(pluginToMap(entry, plugin));
        }
        entry.append("plugins", plugins);
    }

    private Object pluginToMap(ReportContextEntry entry, Plugin plugin) {
        PluginDescriptionFile desc = plugin.getDescription();
        LinkedHashMap<Object, Object> map = MapBuilder.newLinkedHashMap()
            .put("name", desc.getName())
            .put("version", desc.getVersion())
            .put("main", desc.getMain())
            .put("authors", desc.getAuthors())
            .put("description", desc.getDescription())
            .put("website", desc.getWebsite())
            .put("prefix", desc.getPrefix())
            .put("database", desc.isDatabaseEnabled())
            .put("load", desc.getLoad())
            .put("default-permission", desc.getPermissionDefault())
            .put("awareness", desc.getAwareness())
            .put("depend", desc.getDepend())
            .put("softdepend", desc.getSoftDepend())
            .put("loadbefore", desc.getLoadBefore())
            .build();
        if (entry.getReportLevel() > ReportLevel.NORMAL) {
            map.put("commands", desc.getCommands());
            map.put("permissions", desc.getPermissions());
        }
        return map;
    }
}
