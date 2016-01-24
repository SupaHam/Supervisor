package com.supaham.supervisor.contexts;

import com.supaham.commons.utils.MapBuilder;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class WorldsContext extends ReportContext {

    public WorldsContext() {
        super("world", "Worlds", "1");
    }

    protected abstract List<World> getWorlds(ReportContextEntry entry);

    @Override public void run(ReportContextEntry entry) {
        List<World> worlds = getWorlds(entry);
        entry.append("count", worlds.size());
        List<Object> output = new ArrayList<>();
        for (World world : worlds) {
            output.add(worldToMap(world));
        }
        entry.append("worlds", output);
    }

    private Object worldToMap(World world) {
        return MapBuilder.newLinkedHashMap()
            .put("id", world.getId())
            .put("name", world.getName())
            .put("uuid", world.getUuid())
            .put("seed", world.getSeed())
            .put("environment", world.getEnvironment())
            .put("online_player_count", world.getOnlinePlayerCount())
            .put("entity_count", world.getEntityCount())
            .put("chunk_count", world.getChunkCount())
                //.put("total_chunk_count", world.getChunks().length)
            .put("spawn_location", world.getSpawnLocation())
            .put("world_time", world.getWorldTime())
            .put("day_time", world.getDayTime())
            .put("entities", world.getEntities())
            .put("loaded_chunks", world.getLoadedChunks())
            .build();
    }

    public interface World {

        Object getId();

        Object getName();

        Object getUuid();

        Object getSeed();

        Object getEnvironment();

        Object getOnlinePlayerCount();

        Object getEntityCount();

        Object getChunkCount();

        Object getSpawnLocation();

        Object getWorldTime();

        Object getDayTime();

        Object getEntities();

        Object getLoadedChunks();
    }
}
