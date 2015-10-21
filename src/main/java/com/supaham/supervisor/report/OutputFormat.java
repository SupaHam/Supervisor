package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;

import com.supaham.commons.utils.StringUtils;
import com.supaham.supervisor.report.OutputFormat.OutputFormatSerializer;
import com.supaham.supervisor.report.formats.JSONFormat;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import pluginbase.config.annotation.SerializeWith;
import pluginbase.config.serializers.Serializer;

/**
 * Represents the format the output of a {@link Context} will be in.
 */
@SerializeWith(OutputFormatSerializer.class)
public class OutputFormat {

    private static final Map<String, OutputFormat> formats = new HashMap<>();

    public static final OutputFormat PRETTY_TXT = new OutputFormat("PRETTY_TXT");
    public static final JSONFormat JSON = new JSONFormat();

    private final String name;

    public static Collection<OutputFormat> values() {
        return Collections.unmodifiableCollection(formats.values());
    }

    public OutputFormat(@Nonnull String name) {
        this.name = StringUtils.checkNotNullOrEmpty(name);
        Preconditions.checkArgument(!formats.containsKey(name), name + " format already registered.");
        formats.put(name, this);
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof OutputFormat)) {
            return false;
        }
        OutputFormat o = (OutputFormat) obj;
        return name.equals(o.name);
    }

    public static OutputFormat getByName(String name) {
        for (OutputFormat outputFormat : formats.values()) {
            if (outputFormat.getName().equalsIgnoreCase(name)) {
                return outputFormat;
            }
        }
        return null;
    }

    @Override public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public static final class OutputFormatSerializer implements Serializer<OutputFormat> {

        @Nullable @Override public Object serialize(OutputFormat object) throws IllegalArgumentException {
            return object == null ? null : object.getName();
        }

        @Nullable @Override public OutputFormat deserialize(@Nullable Object serialized, Class wantedType) throws IllegalArgumentException {
            if (serialized == null) {
                return null;
            }
            OutputFormat outputFormat = OutputFormat.getByName(serialized.toString());
            Preconditions.checkArgument(outputFormat != null, "output format not valid.");
            return outputFormat;
        }
    }
}
