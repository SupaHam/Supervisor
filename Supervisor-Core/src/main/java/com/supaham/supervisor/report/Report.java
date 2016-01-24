package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.supaham.supervisor.Supervisor;
import com.supaham.supervisor.report.Report.ReportResult;
import com.supaham.supervisor.report.ReportMetadataContext.ReportMetadataContextEntry;
import com.supaham.supervisor.report.serializers.ReportSerializer;
import com.supaham.supervisor.utils.TaskTimings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

/**
 * Represents a report which consists of a {@link List} of {@link ReportContext}s, a title, and name.
 */
public class Report implements List<ReportContextEntry>, Callable<ReportResult> {

    private static final List<OutputFormat> SUPPORTED_FORMATS = ImmutableList.of(OutputFormat.JSON, OutputFormat.PRETTY_TXT);

    private final ReportSpecifications reportSpecifications;
    private final List<ReportContextEntry> list; // This list is what controls this Report instance.
    private ReportMetadataContextEntry metadataContextEntry;
    private final Map<String, ReportContext> fileNamesAndOwners = new LinkedHashMap<>();

    public Report(@Nonnull ReportSpecifications reportSpecifications) {
        this(reportSpecifications, new ArrayList<ReportContext>());
    }

    public Report(@Nonnull ReportSpecifications reportSpecifications, @Nonnull List<ReportContext> list) {
        this.reportSpecifications = Preconditions.checkNotNull(reportSpecifications, "report specs cannot be null.");
        Preconditions.checkNotNull(list, "context cannot be null.");
        this.list = new ArrayList<>(list.size());
    }

    /**
     * Clones a {@link Report}. This only supports cloning the existing report list into an {@link ArrayList}. So if the previous report was created
     * with {@link #Report(ReportSpecifications, List)} and {@link LinkedList()} as the list parameter, the list would be come an {@link ArrayList} in this
     * clone.
     *
     * @param report report to clone
     */
    public Report(@Nonnull Report report) {
        Preconditions.checkNotNull(report, "report cannot be null.");
        this.reportSpecifications = new ReportSpecifications(report.reportSpecifications);
        this.list = new ArrayList<>(report.list);
    }

    @Nonnull public ReportResult call() {
        // metadata is never added to the contexts to prevent the output
        ReportMetadataContext metadataCtx = new ReportMetadataContext();
        this.metadataContextEntry = (ReportMetadataContextEntry) metadataCtx.createEntry(this.reportSpecifications);
        this.metadataContextEntry.report = this;

        ReportOutput output;
        ReportSerializer serializer = this.reportSpecifications.getReportSerializer();
        if (serializer == null) {
            serializer = getSupervisor().getReportSerializer(this.reportSpecifications.getFormat());
        }
        
        try {
            Preconditions.checkState(serializer.isCompatibleWith(this.reportSpecifications.getFormat()),
                serializer.getClass().getCanonicalName() + " is not compatible with " + this.reportSpecifications.getFormat() + ".");
            output = serializer.serialize(this);
        } catch (Exception e) {
            this.metadataContextEntry = null;
            throw e;
        }
        return new ReportResult(this.reportSpecifications, output, this.metadataContextEntry.getReportTimings());
    }

    public String sanitizeContextFile(ReportContext context, String file) {
        return context.getName() + "/" + file;
    }

    @Override public String toString() {
        return getClass().getSimpleName() + "{" +
            "specs=" + reportSpecifications +
            ", listCount=" + list.size() +
            '}';
    }

    public ReportSpecifications getReportSpecifications() {
        return reportSpecifications;
    }

    public ReportMetadataContextEntry getMetadataContextEntry() {
        return metadataContextEntry;
    }
    /* ================================
     * >> Delegate List methods
     * ================================ */

    public Supervisor getSupervisor() {
        return reportSpecifications.getSupervisor();
    }

    public Logger getLogger() {
        return getSupervisor().getLogger();
    }

    @Override public int size() {
        return list.size();
    }

    @Override public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override public boolean contains(Object o) {
        return list.contains(o);
    }

    @Nonnull @Override public Iterator<ReportContextEntry> iterator() {
        return list.iterator();
    }

    @Nonnull @Override public Object[] toArray() {
        return list.toArray();
    }

    @Nonnull @Override public <T> T[] toArray(@Nonnull T[] a) {
        return list.toArray(a);
    }

    @Override public boolean add(ReportContextEntry context) {
        return this.list.add(context);
    }

    @Override public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override public boolean containsAll(@Nonnull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override public boolean addAll(@Nonnull Collection<? extends ReportContextEntry> c) {
        return list.addAll(c);
    }

    @Override public boolean addAll(int index, Collection<? extends ReportContextEntry> c) {
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

    @Override public ReportContextEntry get(int index) {
        return list.get(index);
    }

    @Override public ReportContextEntry set(int index, ReportContextEntry element) {
        return list.set(index, element);
    }

    @Override public void add(int index, ReportContextEntry element) {
        list.add(index, element);
    }

    @Override public ReportContextEntry remove(int index) {
        return list.remove(index);
    }

    @Override public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Nonnull @Override public ListIterator<ReportContextEntry> listIterator() {
        return list.listIterator();
    }

    @Nonnull @Override public ListIterator<ReportContextEntry> listIterator(int index) {
        return list.listIterator(index);
    }

    @Nonnull @Override public List<ReportContextEntry> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public static final class ReportResult {

        private final ReportSpecifications reportSpecifications;
        private final ReportOutput output;
        private final TaskTimings taskTimings;

        private ReportResult(ReportSpecifications reportSpecifications, ReportOutput output, TaskTimings taskTimings) {
            this.reportSpecifications = reportSpecifications;
            this.output = output;
            this.taskTimings = taskTimings;
        }

        public ReportSpecifications getReportSpecifications() {
            return reportSpecifications;
        }

        public ReportOutput getOutput() {
            return output;
        }

        public TaskTimings getTaskTimings() {
            return taskTimings;
        }
    }
}
