package com.supaham.supervisor.report;

import com.supaham.supervisor.report.SimpleReportFile.PlainTextReportFile;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Represents an entry in a {@link Report}. Each entry is instantiated as a new Report requests a {@link ReportContext}. So this entry is easily garbage
 * collected after the report is sent to the recipient.
 */
public interface ReportContextEntry extends Runnable, Amendable {

    /**
     * This method fires when a {@link Report} is requesting this context's data.
     */
    @Override
    void run();

    /**
     * Returns the output, represented as an {@link Object}, of this Context. The output result returned should be compatible with the given {@link
     * #getReportSpecifications()}.
     *
     * @return output as object, nonnull
     */
    @Nonnull
    Object output() throws UnsupportedFormatException;

    /**
     * Creates and returns a new {@link ReportFile} that is named and titled.
     *
     * @param fileName name of the file
     * @param fileTitle title of the file, nullable
     *
     * @return report file
     */
    @Nonnull
    ReportFile createFile(@Nonnull String fileName, String fileTitle);

    /**
     * Creates and returns a new {@link ReportFile} that is named and titled.
     *
     * @param fileName name of the file
     * @param fileTitle title of the file, nullable
     *
     * @return report file
     */
    @Nonnull
    PlainTextReportFile createPlainTextFile(@Nonnull String fileName, String fileTitle);

    @Nonnull
    Map<String, ReportFile> getFiles();

    /**
     * Returns the {@link ReportContext} parent (creator) of this file.
     *
     * @return context parent
     */
    @Nonnull
    ReportContext getParentContext();

    /**
     * Returns the {@link ReportSpecifications} that defines how this ContextEntry should act. The ReportSpecifications has all the information that 
     * controls what the output should present.
     * @return report specifications
     */
    ReportSpecifications getReportSpecifications();

    /**
     * Delegation for {@link ReportSpecifications#getReportLevel(ReportContext)}. If a specific report level has been set for this context, that is 
     * returned. Otherwise, the report's report level is what's returned.
     *
     * @return report level for this context
     */
    int getReportLevel();
}
