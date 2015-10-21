package com.supaham.supervisor.report;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Ali on 21/10/2015.
 */
public class ContextRegistry {

    private final Map<String, RegistryEntry> registry = new LinkedHashMap<>(); // TEMP LINKED FOR OUTPUT ORDER

    public void register(@Nonnull Plugin owner, @Nonnull Context context) {
        String name = context.getName();
        Preconditions.checkArgument(!this.registry.containsKey(name), "'" + name + "' is already registered");
        this.registry.put(name, new RegistryEntry(owner, context));
    }

    public Collection<Context> getContexts() {
        return Collections.unmodifiableCollection(Collections2.transform(this.registry.values(), TransformFunction.INSTANCE));
    }

    public Map<String, Context> getRegistry() {
        return Maps.transformValues(registry, TransformFunction.INSTANCE);
    }

    private static final class RegistryEntry {

        private final Plugin owner;
        private final Context context;

        public RegistryEntry(Plugin owner, Context context) {
            this.owner = owner;
            this.context = context;
        }
    }

    private enum TransformFunction implements Function<RegistryEntry, Context> {
        INSTANCE;
        
        @Nullable @Override public Context apply(RegistryEntry input) {
            return input.context;
        }
    }
}
