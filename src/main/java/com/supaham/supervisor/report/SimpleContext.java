package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.supaham.commons.utils.StringUtils;
import com.supaham.supervisor.report.formats.JSONFormat;

import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a simple Key/Value List report.
 */
public abstract class SimpleContext implements Context, ContextComparable {

    private final String name;
    private final String title;
    ReportSpecs reportSpecs;
    List<Entry> entries;

    public SimpleContext(@Nonnull String name, @Nonnull String title) {
        this.title = StringUtils.checkNotNullOrEmpty(title, "title");
        this.name = StringUtils.checkNotNullOrEmpty(name, "name");
    }

    @Nonnull @Override public Object output() throws UnsupportedFormatException {
        OutputFormat format = this.reportSpecs.getFormat();
        if (format.equals(OutputFormat.PRETTY_TXT)) {
            if (!entries.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (Entry entry : entries) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append("\n");
                    }
                    builder.append(entry.key).append(": ");
                    if (entry.value == null) {
                        builder.append("null");
                        continue;
                    }
                    String str = getStringValue(entry.value, Sets.newHashSet());
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
            for (Entry entry : entries) {
                obj.add(entry.key, gson.toJsonTree(entry.value));
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
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
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

    @Nonnull @Override public String getName() {
        return name;
    }
    
    @Nonnull @Override public String getTitle() {
        return title;
    }

    @Override public int compareTo(@Nonnull Context o) {
        return getName().compareTo(o.getName());
    }

    /**
     * Appends a {@code key}, alongside a formatted message with values.
     *
     * @param key key
     * @param message message
     * @param values values to format the message with
     */
    public void append(@Nonnull String key, @Nonnull String message, @Nullable Object... values) {
        StringUtils.checkNotNullOrEmpty(message, "message");
        Preconditions.checkNotNull(values, "values cannot be null.");
        append(key, String.format(message, values));
    }

    /**
     * Appends a {@code key} with a {@code byte} value.
     *
     * @param key key
     * @param value byte value
     */
    public void append(@Nonnull String key, byte value) {
        append(key, String.valueOf(value));
    }

    /**
     * Appends a {@code key} with a {@code short} value.
     *
     * @param key key
     * @param value short value
     */
    public void append(@Nonnull String key, short value) {
        append(key, String.valueOf(value));
    }

    /**
     * Appends a {@code key} with an {@code int} value.
     *
     * @param key key
     * @param value int value
     */
    public void append(@Nonnull String key, int value) {
        append(key, String.valueOf(value));
    }

    /**
     * Appends a {@code key} with a {@code long} value.
     *
     * @param key key
     * @param value long value
     */
    public void append(@Nonnull String key, long value) {
        append(key, String.valueOf(value));
    }

    /**
     * Appends a {@code key} with a {@code float} value.
     *
     * @param key key
     * @param value float value
     */
    public void append(@Nonnull String key, float value) {
        append(key, String.valueOf(value));
    }

    /**
     * Appends a {@code key} with a {@code double} value.
     *
     * @param key key
     * @param value double value
     */
    public void append(@Nonnull String key, double value) {
        append(key, String.valueOf(value));
    }

    /**
     * Appends a {@code key} with a {@code boolean} value.
     *
     * @param key key
     * @param value boolean value
     */
    public void append(@Nonnull String key, boolean value) {
        append(key, String.valueOf(value));
    }

    /**
     * Appends a {@code key} with a {@code char} value.
     *
     * @param key key
     * @param value char value
     */
    public void append(@Nonnull String key, char value) {
        append(key, String.valueOf(value));
    }

    /**
     * Appends a {@code key} with an {@link Object} value.
     *
     * @param key key
     * @param value value, nullable
     */
    public void append(@Nonnull String key, @Nullable Object value) {
        StringUtils.checkNotNullOrEmpty(key, "key");
        entries.add(new Entry(key, value));
    }

    @Nonnull @Override public ReportSpecs getReportSpecs() {
        return reportSpecs;
    }

    /**
     * Delegation for {@link ReportSpecs#getReportLevel(Context)}. If a specific report level has been set for this context, that is returned.
     * Otherwise, the report's report level is what's returned.
     *
     * @return report level for this context
     */
    public int getReportLevel() {
        return getReportSpecs().getReportLevel(this);
    }

    public static final class Entry {

        private final String key;
        private final Object value;

        public Entry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }
}
