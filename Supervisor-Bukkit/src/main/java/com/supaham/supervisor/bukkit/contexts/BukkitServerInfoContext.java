package com.supaham.supervisor.bukkit.contexts;

import com.google.common.base.Preconditions;

import com.supaham.commons.bukkit.players.Players;
import com.supaham.commons.bukkit.utils.ReflectionUtils;
import com.supaham.commons.utils.RuntimeUtils;
import com.supaham.supervisor.contexts.ServerInfoContext;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BukkitServerInfoContext extends ServerInfoContext {

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

    private static String _getMinecraftVersion() {
        Matcher matcher = PATTERN.matcher(Bukkit.getVersion());
        Preconditions.checkState(matcher.find(), "Matcher didn't find minecraft version");
        return matcher.group(1);
    }

    @Override protected Object getCPUUsage() {
        return new double[]{RuntimeUtils.getCPUUsage()}; // temporary single-element array, TODO add more
    }

    @Override protected Object getFreeMemory() {
        return RuntimeUtils.getFreeMemory();
    }

    @Override protected Object getUsedMemory() {
        return RuntimeUtils.getUsedMemory();
    }

    @Override protected Object getAllocatedMemory() {
        return RuntimeUtils.getAllocatedMemory();
    }

    @Override protected Object getMaximumMemory() {
        return RuntimeUtils.getMaximumMemory();
    }

    @Override protected Object getMinecraftVersion() {
        return _getMinecraftVersion();
    }

    @Override protected Object getImplementation() {
        return Bukkit.getVersion();
    }

    @Override protected Object getOnlinePlayerCount() {
        return Players.serverPlayers().get().size();
    }

    @Override protected Object getServerProperties() {
        return serverProperties;
    }
}
