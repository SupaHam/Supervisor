package com.supaham.supervisor.service;

import com.supaham.supervisor.report.Report.ReportResult;

import javax.annotation.Nonnull;

/**
 * Represents an interface for creating report sharing service.
 */
public interface ReportService {

    /**
     * Called when this service is ordered to publish the given {@link ReportResult}.
     */
    void publish(@Nonnull ReportResult reportResult, @Nonnull MessageRecipient recipient);
    
    String getName();
}
