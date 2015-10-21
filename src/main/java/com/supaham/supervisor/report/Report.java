package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.supaham.supervisor.SupervisorPlugin;
import com.supaham.supervisor.report.Report.ReportResult;
import com.supaham.supervisor.report.formats.JSONFormat;

import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.annotation.Nonnull;

/**
 * Represents a report which consists of a {@link List} of {@link Context}s, a title, and name.
 */
public class Report implements List<Context>, Callable<ReportResult> {

    private final ReportSpecs reportSpecs;
    private final List<Context> list;

    public Report(@Nonnull ReportSpecs reportSpecs) {
        this(reportSpecs, new ArrayList<Context>());
    }

    public Report(@Nonnull ReportSpecs reportSpecs, @Nonnull List<Context> list) {
        this.reportSpecs = Preconditions.checkNotNull(reportSpecs, "report specs cannot be null.");
        this.list = Preconditions.checkNotNull(list, "context cannot be null.");
    }

    /**
     * Clones a {@link Report}. This only supports cloning the existing report list into an {@link ArrayList}. So if the previous report was created
     * with {@link #Report(ReportSpecs, List)} and {@link LinkedList()} as the list parameter, the list would be come an {@link ArrayList} in this
     * clone.
     *
     * @param report report to clone
     */
    public Report(@Nonnull Report report) {
        Preconditions.checkNotNull(report, "report cannot be null.");
        this.reportSpecs = new ReportSpecs(report.reportSpecs);
        this.list = new ArrayList<>(report.list);
    }

    @Nonnull public ReportResult call() {

        Object output;
        OutputFormat format = this.reportSpecs.getFormat();

        ReportMetadataContext metadata = new ReportMetadataContext();
        List<Context> contexts = new ArrayList<>(this.list);
        contexts.add(metadata); // Silently register ReportMetadataContext here for tracking data

        for (Context context : contexts) {
            _setup(context);
        }
        if (format.equals(OutputFormat.PRETTY_TXT)) {
            if (!contexts.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (Context context : contexts) {
                    context.run();
                    builder.append("================================\n")
                        .append(context.getTitle()).append(" (").append(context.getName()).append(")")
                        .append("\n================================")
                        .append("\n\n");
                    try {
                        builder.append(context.output());
                    } catch (Exception e) {
                        builder.append("ERROR OCCURRED WHEN GETTING CONTEXT OUTPUT, CHECK CONSOLE!");
                        SupervisorPlugin.log().log(Level.SEVERE, e.getMessage(), e);
                    }
                    builder.append("\n\n");
                }
                output = builder.toString();
            } else {
                output = "No reports.";
            }
        } else if (format.equals(OutputFormat.JSON)) {
            Gson gson = ((JSONFormat) format).getGson();
            JSONArray root = new JSONArray();
            for (Context context : contexts) {
                context.run();
                JsonObject obj = new JsonObject();
                obj.addProperty("name", context.getName());
                obj.addProperty("title", context.getTitle());
                // Output reportLevel if specified for the context
                if (reportSpecs.getContextReportLevels().containsKey(context.getName())) {
                    obj.addProperty("reportLevel", reportSpecs.getContextReportLevels().get(context.getName()));
                }
                try {
                    obj.add("data", gson.toJsonTree(context.output()));
                } catch (Exception e) {
                    SupervisorPlugin.log().log(Level.SEVERE, e.getMessage(), e);
                }
                root.add(obj);
            }
            output = gson.toJson(root);
        } else {
            for (Context context : contexts) {
                _destroy(context);
            }
            throw new UnsupportedFormatException(format);
        }
        for (Context context : contexts) {
            _destroy(context);
        }

        return new ReportResult(this.reportSpecs, output, metadata.startMillis, metadata.endMillis, metadata.durationNanos);
    }

    private void _setup(Context context) {
        if (context instanceof SimpleContext) {
            SimpleContext simple = (SimpleContext) context;
            simple.reportSpecs = this.reportSpecs;
            simple.entries = new ArrayList<>();
        }

    }

    private void _destroy(Context context) {
        if (context instanceof SimpleContext) {
            SimpleContext simple = (SimpleContext) context;
            simple.reportSpecs = null;
            simple.entries = null;
        }
    }

    @Override public String toString() {
        return getClass().getSimpleName() + "{" +
            "specs=" + reportSpecs +
            ", listCount=" + list.size() +
            '}';
    }
    
    /* ================================
     * >> Delegate List methods
     * ================================ */

    @Override public int size() {
        return list.size();
    }

    @Override public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override public boolean contains(Object o) {
        return list.contains(o);
    }

    @Nonnull @Override public Iterator<Context> iterator() {
        return list.iterator();
    }

    @Nonnull @Override public Object[] toArray() {
        return list.toArray();
    }

    @Nonnull @Override public <T> T[] toArray(@Nonnull T[] a) {
        return list.toArray(a);
    }

    @Override public boolean add(Context context) {
        return this.list.add(context);
    }

    @Override public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override public boolean containsAll(@Nonnull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override public boolean addAll(@Nonnull Collection<? extends Context> c) {
        return list.addAll(c);
    }

    @Override public boolean addAll(int index, Collection<? extends Context> c) {
        return list.addAll(index, c);
    }

    @Override public boolean removeAll(@Nonnull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override public boolean retainAll(@Nonnull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override public void clear() {
        list.clear();
    }

    @Override public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override public int hashCode() {
        return list.hashCode();
    }

    @Override public Context get(int index) {
        return list.get(index);
    }

    @Override public Context set(int index, Context element) {
        return list.set(index, element);
    }

    @Override public void add(int index, Context element) {
        list.add(index, element);
    }

    @Override public Context remove(int index) {
        return list.remove(index);
    }

    @Override public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Nonnull @Override public ListIterator<Context> listIterator() {
        return list.listIterator();
    }

    @Nonnull @Override public ListIterator<Context> listIterator(int index) {
        return list.listIterator(index);
    }

    @Nonnull @Override public List<Context> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public static final class ReportResult {

        private final ReportSpecs reportSpecs;
        private final Object output;
        private final long startMillis;
        private final long endMillis;
        private final long durationNano;

        private ReportResult(@Nonnull ReportSpecs reportSpecs, @Nonnull Object output, long startMillis, long endMillis,
                             long durationNano) {
            this.reportSpecs = reportSpecs;
            this.output = output;
            this.startMillis = startMillis;
            this.endMillis = endMillis;
            this.durationNano = durationNano;
        }

        public ReportSpecs getReportSpecs() {
            return reportSpecs;
        }

        public Object getOutput() {
            return output;
        }

        public long getStartMillis() {
            return startMillis;
        }

        public long getEndMillis() {
            return endMillis;
        }

        public long getDurationNano() {
            return durationNano;
        }
    }

    public static final class ReportMetadataContext extends SimpleContext {

        private final long startMillis;
        private final long startNanos;
        private long endMillis;
        private long durationNanos;

        public ReportMetadataContext() {
            super("report-metadata", "Report Metadata");
            this.startMillis = System.currentTimeMillis();
            this.startNanos = System.nanoTime();
        }

        @Override public void run() {
            append("start_ms", this.startMillis);
            append("end_ms", this.endMillis = System.currentTimeMillis());
            append("duration_ns", this.durationNanos = System.nanoTime() - this.startNanos);
            append("specifications", ImmutableMap.builder()
                    .put("owner", getReportSpecs().getOwner().getName())
                    .put("title", getReportSpecs().getTitle())
                    .put("format", getReportSpecs().getFormat().getName())
                    .put("reportLevel", getReportSpecs().getReportLevel())
                    .put("contextReportLevels", getReportSpecs().getContextReportLevels())
                    .put("excludes", getReportSpecs().getExcludes())
                    .put("includes", getReportSpecs().getIncludes())
                    .build()
            );
        }
    }
}
