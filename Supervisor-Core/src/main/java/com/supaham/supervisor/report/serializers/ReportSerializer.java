package com.supaham.supervisor.report.serializers;

import com.supaham.supervisor.report.Amendable;
import com.supaham.supervisor.report.OutputFormat;
import com.supaham.supervisor.report.Report;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportOutput;

/**
 * Created by Ali on 04/11/2015.
 */
public interface ReportSerializer {

    ReportOutput serialize(Report report);

    Object serializeAmendable(Report report, Amendable amendable);

    boolean isCompatibleWith(OutputFormat outputFormat);
}
