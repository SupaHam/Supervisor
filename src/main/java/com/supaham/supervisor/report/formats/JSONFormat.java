package com.supaham.supervisor.report.formats;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.supaham.supervisor.report.OutputFormat;

import javax.annotation.Nonnull;

/**
 * Represents a JSON {@link OutputFormat} using {@link Gson}.
 */
public class JSONFormat extends OutputFormat {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Gson gson;

    public JSONFormat() {
        this(GSON);
    }

    public JSONFormat(@Nonnull Gson gson) {
        super("JSON");
        this.gson = Preconditions.checkNotNull(gson, "gson cannot be null.");
    }

    public Gson getGson() {
        return gson;
    }
}
