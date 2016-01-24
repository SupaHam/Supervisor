package com.supaham.supervisor.report.serializers;

import com.supaham.commons.utils.StringUtils;
import com.supaham.supervisor.Supervisor;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.OutputFormat;
import com.supaham.supervisor.report.Report;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportOutput;

import org.json.simple.JSONArray;

import java.util.logging.Level;

import javax.annotation.Nonnull;

public class PrettyTxtReportSerializer extends AbstractReportSerializer {

    private StringBuilder builder;
    public PrettyTxtReportSerializer(@Nonnull Supervisor supervisor) {
        super(supervisor);
    }
    
    @Override
    protected boolean pre(Report report) {
        if (super.pre(report)) {
            builder = new StringBuilder();
            return true;
        }
        return false;
    }

    @Override
    protected void post() {
        super.post();
        builder = null;
    }

    @Override protected ReportOutput toOutput() {
        return new ReportOutput(getReport().getReportSpecifications().getFormat(), this.builder.toString());
    }

    @Override protected void each(int insertAt, ReportContextEntry contextEntry) {
        Object serialized = serializeContext(getReport(), contextEntry);
        if(insertAt < 0) {
            builder.append(serialized);
        } else {
            builder.insert(0, serialized);
        }
    }

    @Override public ReportOutput serialize(Report report) {
        OutputFormat format = OutputFormat.PRETTY_TXT;
        if (!report.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append(StringUtils.repeat("=", 48)).append("\n")
                .append(report.getReportSpecifications().getTitle()).append("\n")
                .append(StringUtils.repeat("=", 48)).append("\n\n");
            for (ReportContextEntry contextEntry : report) {
                try {
                    builder.append(serializeContext(report, contextEntry));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Handle ReportMetadataContext, make sure to add it to the beginning of the JSON for readability purposes.
            builder.insert(0, serializeContext(report, report.getMetadataContextEntry()));

            return new ReportOutput(format, builder.toString());
        } else {
            return new ReportOutput(format, "No reports.");
        }
    }

    @Override public Object serializeContext(Report report, ReportContextEntry contextEntry) {
        StringBuilder builder = new StringBuilder();
        builder.append("================================\n")
            .append(contextEntry.getParentContext().getTitle()).append(" (").append(contextEntry.getParentContext().getName()).append(")")
            .append("\n================================")
            .append("\n\n");
        try {
            builder.append(contextEntry.output());
        } catch (Exception e) {
            builder.append("ERROR OCCURRED WHEN GETTING CONTEXT OUTPUT, CHECK CONSOLE!");
            getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        builder.append("\n\n");
        return builder.toString();
    }

    @Override public boolean isCompatibleWith(OutputFormat outputFormat) {
        return OutputFormat.PRETTY_TXT.equals(outputFormat);
    }
}
