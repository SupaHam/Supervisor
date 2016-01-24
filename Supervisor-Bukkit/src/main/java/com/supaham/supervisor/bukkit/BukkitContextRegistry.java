package com.supaham.supervisor.bukkit;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.ReportContextComparable;
import com.supaham.supervisor.report.ReportContextRegistry;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Ali on 21/10/2015.
 */
public class BukkitContextRegistry implements ReportContextRegistry {

    private final Map<String, RegistryEntry> registry = new LinkedHashMap<>(); // TEMP LINKED FOR OUTPUT ORDER
    private final Comparator<RegistryEntry> comparator;

    /**
     * <b>Note: this comparator imposes orderings that are inconsistent with equals.</b>
     */
    public BukkitContextRegistry() {
        this(RegistryEntryComparator.INSTANCE);
    }

    public BukkitContextRegistry(@Nonnull Comparator<RegistryEntry> comparator) {
        this.comparator = Preconditions.checkNotNull(comparator, "comparator cannot be null.");
    }

    public void register(@Nonnull Plugin owner, @Nonnull ReportContext context) {
        String name = context.getName();
        Preconditions.checkArgument(!this.registry.containsKey(name), "'" + name + "' is already registered");
        this.registry.put(name, new RegistryEntry(owner, context));
    }

    @Override public Collection<ReportContext> getContexts() {
        return _transform(this.registry.values());
    }

    @Override public Collection<ReportContext> getSortedContexts() {
        List<RegistryEntry> entries = new ArrayList<>(this.registry.values());
        Collections.sort(entries, this.comparator);
        return _transform(entries);
    }
    
    private Collection<ReportContext> _transform(Collection<RegistryEntry> c) {
        return Collections.unmodifiableCollection(Collections2.transform(c, TransformFunction.INSTANCE));
    }

    @Override public Map<String, ReportContext> getRegistry() {
        return Maps.transformValues(registry, TransformFunction.INSTANCE);
    }

    public static final class RegistryEntry {

        private final Plugin owner;
        private final ReportContext context;

        public RegistryEntry(Plugin owner, ReportContext context) {
            this.owner = owner;
            this.context = context;
        }

        public Plugin getOwner() {
            return owner;
        }

        public ReportContext getContext() {
            return context;
        }
    }

    private enum TransformFunction implements Function<RegistryEntry, ReportContext> {
        INSTANCE;

        @Nullable @Override public ReportContext apply(RegistryEntry input) {
            return input.context;
        }
    }

    private enum RegistryEntryComparator implements Comparator<RegistryEntry> {
        INSTANCE;

        @Override public int compare(RegistryEntry o1, RegistryEntry o2) {
            // Prioritize Supervisor contexts
            if (o1.owner.getName().equals(SupervisorPlugin.get().getName())) {
                // Supervisor reports always go on top.
                if (!o2.owner.getName().equals(SupervisorPlugin.get().getName())) {
                    return 1;
                } else {
                    int compare = ContextComparator.INSTANCE.compare(o1.context, o2.context);
                    return compare;
                }
            }
            int i = o1.owner.getName().compareTo(o2.owner.getName());
            return i != 0 ? i : ContextComparator.INSTANCE.compare(o1.context, o2.context);
        }
    }

    private enum ContextComparator implements Comparator<ReportContext> {
        INSTANCE;

        @Override public int compare(ReportContext o1, ReportContext o2) {
            if (o1 instanceof ReportContextComparable) {
                return ((ReportContextComparable) o1).compareTo(o2);
            } else {
                return o1.getName().compareTo(o2.getName());
            }
        }
    }
}
