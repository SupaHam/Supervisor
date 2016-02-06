package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.supaham.supervisor.report.SimpleReportFile.PlainTextReportFile;
import com.supaham.supervisor.report.formats.JSONFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a simple abstract Key/Value List report.
 */
public abstract class AbstractReportContextEntry implements ReportContextEntry {

    private final ReportContext parentContext;
    private final ReportSpecifications reportSpecifications;
    private final SimpleReportFile root;
    private final Map<String, ReportFile> files;

    public AbstractReportContextEntry(@Nonnull ReportContext parentContext, @Nonnull ReportSpecifications reportSpecifications) {
        this.parentContext = Preconditions.checkNotNull(parentContext, "owner cannot be null.");
        this.reportSpecifications = Preconditions.checkNotNull(reportSpecifications, "reportSpecifications cannot be null.");
        this.files = new LinkedHashMap<>();
        this.root = new SimpleReportFile(this, ReportFile.ROOT_FILE_NAME, "Report");
    }

    @Nonnull
    @Override
    public Object output() throws UnsupportedFormatException {
        OutputFormat format = this.reportSpecifications.getFormat();
        if (format.equals(OutputFormat.PRETTY_TXT)) {
            if (!files.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (Entry<String, Object> entry : this.root.getEntries().entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append("\n");
                    }
                    builder.append(entry.getKey()).append(": ");
                    if (entry.getValue() == null) {
                        builder.append("null");
                        continue;
                    }
                    String str = getStringValue(entry.getValue(), Sets.newHashSet());
                    if (str.contains("\n")) {
                        builder.append("\n");
                        builder.append(str.replaceAll("(?m)^", "\t"));
                    } else {
                        builder.append(str);
                    }
                }
                return builder.toString();
            } else {
                return "No data.";
            }
        } else if (format.equals(OutputFormat.JSON)) {
            Gson gson = ((JSONFormat) format).getGson();
            JsonObject obj = new JsonObject();
            for (Entry<String, Object> entry : this.root.getEntries().entrySet()) {
                obj.add(entry.getKey(), gson.toJsonTree(entry.getValue()));
            }
            return obj;
        }
        throw new UnsupportedFormatException(format);
    }

    private static String getStringValue(Object value, Set<Object> seen) {
        if (seen.contains(value)) {
            return "<Recursive>";
        } else {
            seen.add(value);
        }

        if (value instanceof Object[]) {
            value = Arrays.asList(value);
        }

        if (value instanceof Collection<?>) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Object entry : (Collection<?>) value) {
                if (first) {
                    first = false;
                } else {
                    builder.append("\n");
                }
                builder.append(getStringValue(entry, Sets.newHashSet(seen)));
            }
            return builder.toString();
        } else if (value instanceof Map<?, ?>) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (first) {
                    first = false;
                } else {
                    builder.append("\n");
                }

                String key = getStringValue(entry.getKey(), Sets.newHashSet(seen)).replaceAll("[\r\n]", "");
                if (key.length() > 60) {
                    key = key.substring(0, 60) + "...";
                }

                builder
                    .append(key)
                    .append(": ")
                    .append(getStringValue(entry.getValue(), Sets.newHashSet(seen)));
            }
            return builder.toString();
        } else {
            return String.valueOf(value);
        }
    }

    @Nonnull
    @Override
    public Map<String, ReportFile> getFiles() {
        return files;
    }

    @Nonnull
    @Override
    public ReportSpecifications getReportSpecifications() {
        return reportSpecifications;
    }

    @Nonnull
    @Override
    public ReportContext getParentContext() {
        return this.parentContext;
    }

    /**
     * Creates and returns a new {@link SimpleReportFile} that is named and titled with the given {@code fileName}.
     *
     * @param fileName name of the file
     *
     * @return report file
     */
    protected SimpleReportFile createFile(String fileName) {
        return createFile(fileName, fileName);
    }

    /**
     * Creates and returns a new {@link SimpleReportFile} that is named and titled.
     *
     * @param fileName name of the file
     * @param fileTitle title of the file, nullable
     *
     * @return report file
     * @throws IllegalArgumentException thrown if {@code fileName} already exists.
     */
    @Nonnull
    @Override
    public SimpleReportFile createFile(@Nonnull String fileName, String fileTitle) throws IllegalArgumentException {
        Preconditions.checkNotNull(fileName, "fileName cannot be null.");
        Preconditions.checkArgument(!this.files.containsKey(fileName), fileName + " already exists and cannot be overridden");

        SimpleReportFile reportFile = new SimpleReportFile(this, fileName, fileTitle);
        this.files.put(fileName, reportFile);
        return reportFile;
    }

    /**
     * Creates and returns a new {@link SimpleReportFile} that is named and titled.
     *
     * @param fileName name of the file
     * @param fileTitle title of the file, nullable
     *
     * @return report file
     * @throws IllegalArgumentException thrown if {@code fileName} already exists.
     */
    @Nonnull
    @Override
    public PlainTextReportFile createPlainTextFile(@Nonnull String fileName, String fileTitle) throws IllegalArgumentException {
        Preconditions.checkNotNull(fileName, "fileName cannot be null.");
        Preconditions.checkArgument(!this.files.containsKey(fileName), fileName + " already exists and cannot be overridden");

        PlainTextReportFile reportFile = new PlainTextReportFile(this, fileName, fileTitle);
        this.files.put(fileName, reportFile);
        return reportFile;
    }

    @Override
    public <T extends ReportFile> T addFile(@Nonnull T reportFile) {
        Preconditions.checkNotNull(reportFile, "reportFile cannot be null.");
        String fileName = reportFile.getFileName();
        Preconditions.checkArgument(!this.files.containsKey(fileName), fileName + " already exists and cannot be overridden");
        
        this.files.put(fileName, reportFile);
        return reportFile;
    }
    
    /* ================================
     * >> DELEGATE METHODS
     * ================================ */

    @Override
    public int getReportLevel() {
        return getReportSpecifications().getReportLevel(getParentContext());
    }

    @Override
    public void append(@Nonnull String key, @Nonnull String message, @Nullable Object... values) {
        root.append(key, message, values);
    }

    @Override
    public void append(@Nonnull String key, byte value) {
        root.append(key, value);
    }

    @Override
    public void append(@Nonnull String key, short value) {
        root.append(key, value);
    }

    @Override
    public void append(@Nonnull String key, int value) {
        root.append(key, value);
    }

    @Override
    public void append(@Nonnull String key, long value) {
        root.append(key, value);
    }

    @Override
    public void append(@Nonnull String key, float value) {
        root.append(key, value);
    }

    @Override
    public void append(@Nonnull String key, double value) {
        root.append(key, value);
    }

    @Override
    public void append(@Nonnull String key, boolean value) {
        root.append(key, value);
    }

    @Override
    public void append(@Nonnull String key, char value) {
        root.append(key, value);
    }

    @Override
    public void append(@Nonnull String key, @Nullable Object value) {
        root.append(key, value);
    }

    @Override
    @Nonnull
    public Map<String, Object> getEntries() {
        return Collections.unmodifiableMap(root.getEntries());
    }
}
