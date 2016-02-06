package com.supaham.supervisor.report;

import javax.annotation.Nonnull;

/**
 * Created by Ali on 29/10/2015.
 */
public interface ReportFile extends Amendable {

    String ROOT_FILE_NAME = "report";

    /**
     * Returns the output, represented as an {@link Object}, of this file. The output result returned should be compatible with the given {@link
     * #getContextEntry()}.
     *
     * @return output as object, nonnull
     */
    @Nonnull
    Object output() throws UnsupportedFormatException;

    /**
     * Returns the name of this context. The name, as opposed to title, is a unique string that further helps identify the context.
     *
     * @return name
     */
    @Nonnull
    String getFileName();

    /**
     * Returns the title of this context.
     *
     * @return title
     */
    String getFileTitle();

    /**
     * Returns the {@link ReportContextEntry} owner (creator) of this file.
     *
     * @return context entry
     */
    @Nonnull
    ReportContextEntry getContextEntry();

    /**
     * @see {@link ReportContextEntry#getReportLevel()}
     */
    default int getReportLevel() {
        return getContextEntry().getReportLevel();
    }
}
