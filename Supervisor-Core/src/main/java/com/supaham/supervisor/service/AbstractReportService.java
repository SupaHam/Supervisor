package com.supaham.supervisor.service;

import com.supaham.commons.utils.StringUtils;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

/**
 * Represents a simple abstract extension of {@link ReportService} which provides a {@link Logger} as well as a constructor for the name of the
 * service. It is highly recommended that the logger, provided through {@link #getLogger()}, be used when report service tasks are trying to print to
 * console.
 */
public abstract class AbstractReportService implements ReportService {

    private final String name;
    private final Logger logger;

    /**
     * Constructs a new report service with a name, the name mustn't be null or empty.
     *
     * @param name name, nonnull
     */
    public AbstractReportService(@Nonnull String name) {
        this.name = StringUtils.checkNotNullOrEmpty(name, "name");
        this.logger = Logger.getLogger(name);
    }

    @Override public String getName() {
        return name;
    }

    /**
     * Returns a {@link Logger} with the name of {@link #getName()}. It is highly recommended that this be used when report service tasks are
     * trying to print to console.
     *
     * @return service logger
     */
    public Logger getLogger() {
        return logger;
    }
}
