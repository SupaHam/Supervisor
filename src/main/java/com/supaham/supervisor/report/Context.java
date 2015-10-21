package com.supaham.supervisor.report;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Represents context of a report that has a title, name, and output function to provide the user with appropriate information.
 */
public interface Context extends Runnable {

    /**
     * This method fires when a {@link Report} is requesting this context's data.
     */
    @Override void run();

    /**
     * Returns the output, represented as an {@link Object}, of this Context. The output result returned should be compatible with the given {@link
     * #getReportSpecs()}.
     *
     * @return output as object, nonnull
     */
    @Nonnull Object output() throws UnsupportedFormatException;

    /**
     * Returns the title of this context.
     *
     * @return title
     */
    @NotNull @Nonnull String getTitle();

    /**
     * Returns the name of this context. The name, as opposed to title, is a unique string that further helps identify the context.
     *
     * @return name
     */
    @Nonnull String getName();

    /**
     * Returns the {@link ReportSpecs} instance that controls what this context should output, and how it should output.
     *
     * @return report specs
     */
    @Nonnull ReportSpecs getReportSpecs();
}
