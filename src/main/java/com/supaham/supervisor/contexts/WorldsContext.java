package com.supaham.supervisor.contexts;

import com.google.common.base.CaseFormat;

import com.supaham.commons.bukkit.utils.LocationUtils;
import com.supaham.commons.utils.MapBuilder;
import com.supaham.supervisor.report.ReportSpecs.ReportLevel;
import com.supaham.supervisor.report.SimpleContext;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldsContext extends SimpleContext {

    private static final Map<EntityType, String> entityTypeToName = new HashMap<>();

    static {
        String prefix = "minecraft:";
        for (EntityType entityType : EntityType.values()) {
            if (entityType.getName() != null) {
                entityTypeToName.put(entityType, prefix + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getName()));
            }
        }
    }

    public WorldsContext() {
        super("world", "Worlds");
    }

    @Override public void run() {
        append("count", Bukkit.getWorlds().size());
        List<Object> worlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            worlds.add(worldToMap(world));
        }
        append("worlds", worlds);
    }

    private Object worldToMap(World world) {
        return MapBuilder.newLinkedHashMap()
            .put("id", Bukkit.getWorlds().indexOf(world))
            .put("name", world.getName())
            .put("uuid", world.getUID())
            .put("seed", world.getSeed())
            .put("environment", world.getEnvironment().getId())
            .put("player_count", world.getPlayers().size())
            .put("entity_count", world.getEntities().size())
            .put("chunk_count", world.getLoadedChunks().length)
                //.put("total_chunk_count", world.getChunks().length)
            .put("spawn_location", LocationUtils.serialize(world.getSpawnLocation()))
            .put("world_time", world.getFullTime())
            .put("day_time", world.getTime())
            .put("entities", getEntities(world))
            .put("loaded_chunks", getChunks(world))
            .build();
    }

    private Object getEntities(World world) {
        // Else ifs with the higher report levels on top
//      if (getReportLevel() >= ReportLevel.BRIEFEST) {
        if (false) {
        } else {
            HashMap<String, Integer> map = new HashMap<>();
            for (Entity entity : world.getEntities()) {
                String name = entityTypeToName.get(entity.getType());
                Integer integer = map.get(name);
                map.put(name, integer != null ? integer : 1);
            }
            return map;
        }
        return null;
    }

    private Object getChunks(World world) {
        // Else ifs with the higher report levels on top
        if (getReportLevel() >= ReportLevel.NORMAL) {
            List<int[]> list = new ArrayList<>();
            for (Chunk chunk : world.getLoadedChunks()) {
                list.add(new int[]{chunk.getX(), chunk.getZ()});
            }
            return list;
        }
        return null;
    }
}
