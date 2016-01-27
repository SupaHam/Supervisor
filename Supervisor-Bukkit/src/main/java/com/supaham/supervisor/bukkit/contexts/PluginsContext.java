package com.supaham.supervisor.bukkit.contexts;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.supaham.commons.utils.CollectionUtils;
import com.supaham.commons.utils.MapBuilder;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportFile;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PluginsContext extends ReportContext {

    public PluginsContext() {
        super("plugins", "Plugins", "1");
    }

    @Override
    public void run(ReportContextEntry entry) {
        entry.append("count", Bukkit.getPluginManager().getPlugins().length);
        List<Object> plugins = new ArrayList<>();
        List<String> requestedConfigs = new ArrayList<>();

        for (String arg : entry.getReportSpecifications().getArguments()) {
            if (arg.toLowerCase().startsWith("config/")) {
                Collections.addAll(requestedConfigs, arg.substring(7).split(","));
            }
        }

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            final String pluginName = plugin.getName();
            plugins.add(pluginToMap(entry, plugin));

            if (CollectionUtils.containsIgnoreCase(requestedConfigs, pluginName)) {
                try {
                    entry.createPlainTextFile(pluginName + "/config.yml", pluginName + " Config")
                        .appendFile(new File(plugin.getDataFolder(), "config.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        entry.append("plugins", plugins);
        
        if (entry.getReportLevel() >= ReportLevel.NORMAL) {
            listeners(entry);
        }
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

    private void listeners(ReportContextEntry entry) {
        ReportFile file = entry.createFile("registered-listeners", "Registered Listeners");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            List<Object> result = new ArrayList<>();
            List<Listener> pluginListeners = HandlerList.getRegisteredListeners(plugin).stream()
                .map(RegisteredListener::getListener).distinct()
                .collect(Collectors.toList());
            for (Listener listener : pluginListeners) {
                List<Object> methods = new ArrayList<>();
                for (Method method : listener.getClass().getDeclaredMethods()) {
                    EventHandler annotation = method.getDeclaredAnnotation(EventHandler.class);
                    if (annotation != null && method.getParameterCount() == 1) {
                        methods.add(ImmutableMap.of(
                            "method", method.getName(),
                            "event", method.getParameters()[0].getType().getName(),
                            "priority", annotation.priority(),
                            "ignoreCancelled", annotation.ignoreCancelled()));
                    }
                }
                result.add(Collections.singletonMap(listener.getClass().getName(), methods));
            }
//            result.entrySet().stream().map(e -> new );
            file.append(plugin.getName(), result);
        }
    }
}
