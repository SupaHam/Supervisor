package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * Represents an {@link Exception} that is thrown when a {@link Context} does not have support for a {@link OutputFormat}.
 */
public class UnsupportedFormatException extends RuntimeException {

    private OutputFormat format;

    public UnsupportedFormatException(@Nonnull OutputFormat format) {
        super(format.getName() + " format unsupported.");
        this.format = Preconditions.checkNotNull(format, "format cannot be null.");
    }
}
