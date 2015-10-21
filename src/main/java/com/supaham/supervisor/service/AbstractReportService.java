package com.supaham.supervisor.service;

import com.google.common.base.Preconditions;

import com.supaham.supervisor.report.Report.ReportResult;

import javax.annotation.Nonnull;

/**
 * Created by Ali on 21/10/2015.
 */
public abstract class AbstractReportService implements ReportService {

    protected final ReportResult reportResult;

    public AbstractReportService(@Nonnull ReportResult reportResult) {
        this.reportResult = Preconditions.checkNotNull(reportResult, "report result cannot be null.");
    }

    @Nonnull @Override public ReportResult getReportResult() {
        return reportResult;
    }
}
