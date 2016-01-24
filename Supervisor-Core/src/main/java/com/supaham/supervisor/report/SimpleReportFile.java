package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;

import com.supaham.commons.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a simple implementation of {@link ReportFile} requiring very little effort in utilization.
 */
public class SimpleReportFile implements ReportFile {

    private final ReportContextEntry contextEntry;
    private final String fileName;
    private final String fileTitle;

    protected final Map<String, Object> entries = new LinkedHashMap<>();

    public SimpleReportFile(@Nonnull ReportContextEntry contextEntry, @Nonnull String fileName, String fileTitle) {
        this.contextEntry = Preconditions.checkNotNull(contextEntry, "parentContext cannot be null.");
        this.fileName = StringUtils.checkNotNullOrEmpty(fileName, "fileName");
        this.fileTitle = fileTitle;
    }

    @Nonnull
    @Override
    public Object output() throws UnsupportedFormatException {
        return entries;
    }

    @Nonnull
    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public String getFileTitle() {
        return this.fileTitle;
    }

    @Nonnull
    @Override
    public ReportContextEntry getContextEntry() {
        return this.contextEntry;
    }

    @Override
    public void append(@Nonnull String key, @Nonnull String message, @Nullable Object... values) {
        StringUtils.checkNotNullOrEmpty(message, "message");
        Preconditions.checkNotNull(values, "values cannot be null.");
        append(key, values.length == 0 ? message : String.format(message, values));
    }

    @Override
    public void append(@Nonnull String key, byte value) {
        append(key, String.valueOf(value));
    }

    @Override
    public void append(@Nonnull String key, short value) {
        append(key, String.valueOf(value));
    }

    @Override
    public void append(@Nonnull String key, int value) {
        append(key, String.valueOf(value));
    }

    @Override
    public void append(@Nonnull String key, long value) {
        append(key, String.valueOf(value));
    }

    @Override
    public void append(@Nonnull String key, float value) {
        append(key, String.valueOf(value));
    }

    @Override
    public void append(@Nonnull String key, double value) {
        append(key, String.valueOf(value));
    }

    @Override
    public void append(@Nonnull String key, boolean value) {
        append(key, String.valueOf(value));
    }

    @Override
    public void append(@Nonnull String key, char value) {
        append(key, String.valueOf(value));
    }

    @Override
    public void append(@Nonnull String key, @Nullable Object value) {
        StringUtils.checkNotNullOrEmpty(key, "key");
        entries.put(key, value);
    }

    @Nonnull
    @Override
    public Map<String, Object> getEntries() {
        return Collections.unmodifiableMap(entries);
    }

    public static final class PlainTextReportFile extends SimpleReportFile {

        private final List<Object> list = new ArrayList<>();

        public PlainTextReportFile(@Nonnull ReportContextEntry contextEntry, @Nonnull String fileName, String fileTitle) {
            super(contextEntry, fileName, fileTitle);
            entries.put(null, list);
        }

        @Nonnull
        @Override
        public Object output() throws UnsupportedFormatException {
            StringBuilder sb = new StringBuilder();
            for (Object o : list) {
                sb.append(o);
            }
            return sb;
        }

        public void append(String message, @Nullable Object... values) {
            StringUtils.checkNotNullOrEmpty(message, "message");
            Preconditions.checkNotNull(values, "values cannot be null.");
            append(values.length == 0 ? message : String.format(message, values));
        }

        @Override
        public void append(String key, String message, @Nullable Object... values) {
            StringUtils.checkNotNullOrEmpty(message, "message");
            Preconditions.checkNotNull(values, "values cannot be null.");
            append(key, values.length == 0 ? message : String.format(message, values));
        }

        @Override
        public void append(String key, byte value) {
            append(key, String.valueOf(value));
        }

        @Override
        public void append(String key, short value) {
            append(key, String.valueOf(value));
        }

        @Override
        public void append(String key, int value) {
            append(key, String.valueOf(value));
        }

        @Override
        public void append(String key, long value) {
            append(key, String.valueOf(value));
        }

        @Override
        public void append(String key, float value) {
            append(key, String.valueOf(value));
        }

        @Override
        public void append(String key, double value) {
            append(key, String.valueOf(value));
        }

        @Override
        public void append(String key, boolean value) {
            append(key, String.valueOf(value));
        }

        @Override
        public void append(String key, char value) {
            append(key, String.valueOf(value));
        }

        @Override
        public void append(String key, @Nullable Object value) {
            list.add(value);
        }

        public PlainTextReportFile append(Object value) {
            this.list.add(value);
            return this;
        }

        public PlainTextReportFile append(int index, Object value) {
            this.list.add(index, value);
            return this;
        }

        public PlainTextReportFile appendLineBreak() {
            this.list.add(System.getProperty("line.separator"));
            return this;
        }
        
        public PlainTextReportFile appendFile(File file) throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    append(line).appendLineBreak();
                }
            }
            return this;
        }
    }
}
