package com.supaham.supervisor.contexts;

import com.google.common.collect.ImmutableMap;

import com.supaham.commons.utils.MapBuilder;
import com.supaham.supervisor.report.SimpleContext;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.List;

public class PluginsContext extends SimpleContext {

    public PluginsContext() {
        super("plugins", "Plugins");
    }

    @Override public void run() {
        append("count", Bukkit.getPluginManager().getPlugins().length);
        List<Object> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.add(pluginToMap(plugin));
        }
        append("plugins", plugins);
    }
    
    private Object pluginToMap(Plugin plugin) {
        PluginDescriptionFile desc = plugin.getDescription();
        return MapBuilder.newLinkedHashMap()
            .put("name", desc.getName())
            .put("version", desc.getVersion())
            .put("main", desc.getMain())
            .put("author", desc.getAuthors())
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
            .put("commands", desc.getCommands())
            .put("permissions", desc.getPermissions())
            .build();
    }
}
