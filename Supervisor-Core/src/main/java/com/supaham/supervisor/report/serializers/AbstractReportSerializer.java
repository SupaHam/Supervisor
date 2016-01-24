package com.supaham.supervisor.report.serializers;

import com.google.common.base.Preconditions;

import com.supaham.supervisor.Supervisor;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.Report;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportMetadataContext;
import com.supaham.supervisor.report.ReportMetadataContext.ReportMetadataContextEntry;
import com.supaham.supervisor.report.ReportOutput;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

public abstract class AbstractReportSerializer implements ReportSerializer {

    protected final Supervisor supervisor;
    
    private Report report;

    public AbstractReportSerializer(@Nonnull Supervisor supervisor) {
        this.supervisor = Preconditions.checkNotNull(supervisor, "supervisor cannot be null.");
    }
    
    protected boolean pre(Report report) {
        this.report = report;
        return true;
    }
    
    protected void post() {
        this.report = null;
    }
    
    protected abstract ReportOutput toOutput();

    protected abstract void each(int insertAt, ReportContextEntry contextEntry);

    @Override public ReportOutput serialize(Report report) {
        pre(report);
        for (ReportContextEntry contextEntry : report) {
            try {
                each(-1, contextEntry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Handle ReportMetadataContext, make sure to add it to the beginning for readability purposes.
        each(0, report.getMetadataContextEntry());
        ReportOutput reportOutput = toOutput();
        post();
        return reportOutput;
    }

    public Logger getLogger() {
        return supervisor.getLogger();
    }
    
    protected Report getReport() {
        return this.report;
    }
}
