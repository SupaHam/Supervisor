package com.supaham.supervisor.service;

import com.supaham.supervisor.report.Report.ReportResult;

import javax.annotation.Nonnull;

/**
 * Represents a message recipient for {@link ReportService#publish(ReportResult, MessageRecipient)}. This is used to abstractly interact with
 * those in charge of the report output.
 * <p />
 */
public interface MessageRecipient {

    /**
     * Prints a success message to this recipient. Per {@link ReportService} conventions, this message should strictly be nothing more than an access
     * point to the report at the service.
     *
     * @param message message to print
     */
    void printSuccess(@Nonnull String message);

    /**
     * Prints an error message to this recipient. The message should be something useful for the recipient to understand. Not necessarily
     * human-readable just verbose enough to understand the basis of the error. This is not to be confused with {@link #error(Throwable)} which is
     * used to provide a thrown error.
     *
     * @param message error message to print
     */
    void printError(@Nonnull String message);

    /**
     * Throws a {@link Throwable} error to this recipient to handle.
     *
     * @param throwable throwable to pass
     */
    void error(@Nonnull Throwable throwable);
}
