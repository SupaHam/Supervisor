package com.supaham.supervisor.bukkit.contexts;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;

import com.supaham.commons.bukkit.utils.LocationUtils;
import com.supaham.supervisor.contexts.WorldsContext;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class BukkitWorldsContext extends WorldsContext {

    private static final Map<EntityType, String> entityTypeToName = new HashMap<>();

    static {
        String prefix = "minecraft:";
        for (EntityType entityType : EntityType.values()) {
            if (entityType.getName() != null) {
                entityTypeToName.put(entityType, prefix + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getName()));
            }
        }
    }

    @Override
    protected List<World> getWorlds(ReportContextEntry entry) {
        List<World> list = new ArrayList<>();
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            list.add(new BukkitWorldWrapper(entry, world));
        }
        return list;
    }

    public class BukkitWorldWrapper implements World {

        private final ReportContextEntry entry;
        private org.bukkit.World bukkitWorld;

        public BukkitWorldWrapper(ReportContextEntry entry, @Nonnull org.bukkit.World bukkitWorld) {
            this.entry = entry;
            this.bukkitWorld = Preconditions.checkNotNull(bukkitWorld, "bukkitWorld cannot be null.");
        }

        @Override
        public Object getId() {
            return Bukkit.getWorlds().indexOf(bukkitWorld);
        }

        @Override
        public Object getName() {
            return this.bukkitWorld.getName();
        }

        @Override
        public Object getUuid() {
            return this.bukkitWorld.getUID();
        }

        @Override
        public Object getSeed() {
            return this.bukkitWorld.getSeed();
        }

        @Override
        public Object getEnvironment() {
            return this.bukkitWorld.getEnvironment().getId();
        }

        @Override
        public Object getOnlinePlayerCount() {
            return this.bukkitWorld.getPlayers().size();
        }

        @Override
        public Object getEntityCount() {
            return this.bukkitWorld.getEntities().size();
        }

        @Override
        public Object getChunkCount() {
            return this.bukkitWorld.getLoadedChunks().length;
        }

        @Override
        public Object getSpawnLocation() {
            return LocationUtils.serialize(this.bukkitWorld.getSpawnLocation());
        }

        @Override
        public Object getWorldTime() {
            return this.bukkitWorld.getFullTime();
        }

        @Override
        public Object getDayTime() {
            return this.bukkitWorld.getTime();
        }

        @Override
        public Object getEntities() {
            // Else ifs with the higher report levels on top
            //      if (getReportLevel() >= ReportLevel.BRIEFEST) {
            if (false) {
            } else {
                HashMap<String, Integer> map = new HashMap<>();
                for (Entity entity : this.bukkitWorld.getEntities()) {
                    String name = entityTypeToName.get(entity.getType());
                    Integer integer = map.get(name);
                    map.put(name, integer != null ? integer : 1);
                }
                return map;
            }
            return null;
        }

        @Override
        public Object getLoadedChunks() {
            // Else ifs with the higher report levels on top
            if (this.entry.getReportLevel() >= ReportLevel.NORMAL) {
                List<int[]> list = new ArrayList<>();
                for (Chunk chunk : this.bukkitWorld.getLoadedChunks()) {
                    list.add(new int[]{chunk.getX(), chunk.getZ()});
                }
                return list;
            }
            return null;
        }
    }
}
