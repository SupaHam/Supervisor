package com.supaham.supervisor.contexts;

import com.google.common.base.Preconditions;

import com.supaham.commons.bukkit.players.Players;
import com.supaham.commons.bukkit.utils.ReflectionUtils;
import com.supaham.commons.utils.RuntimeUtils;
import com.supaham.supervisor.report.SimpleContext;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerInfoContext extends SimpleContext {

    public ServerInfoContext() {
        super("server-info", "Server Information");
    }

    @Override public void run() {
        append("cpu", RuntimeUtils.getCPUUsage());
        append("free_memory", RuntimeUtils.getFreeMemory());
        append("used_memory", RuntimeUtils.getUsedMemory());
        append("allocated_memory", RuntimeUtils.getAllocatedMemory());
        append("maximum_memory", RuntimeUtils.getMaximumMemory());
        append("minecraft_version", getMinecraftVersion());
        append("implementation", Bukkit.getVersion());
        append("players", Players.serverPlayers().get().size());
        append("properties", serverProperties);
    }

    private static Properties serverProperties;

    static {
        try {
            Server server = Bukkit.getServer();
            Object console = ReflectionUtils.getField(server.getClass(), "console").get(server);
            Object propertyManager = ReflectionUtils.getMethod(console.getClass(), "getPropertyManager").invoke(console);
            serverProperties = (Properties) ReflectionUtils.getField(propertyManager.getClass(), "properties").get(propertyManager);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static final Pattern PATTERN = Pattern.compile(".*MC:\\s*(.*)\\s*\\)");
    
    private static String getMinecraftVersion() {
        Matcher matcher = PATTERN.matcher(Bukkit.getVersion());
        Preconditions.checkState(matcher.find(), "Matcher didn't find minecraft version");
        return matcher.group(1);
    }
}
