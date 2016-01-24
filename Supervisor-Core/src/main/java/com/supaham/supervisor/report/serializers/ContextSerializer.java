package com.supaham.supervisor.report.serializers;

import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.OutputFormat;

/**
 * Created by Ali on 04/11/2015.
 */
public interface ContextSerializer {

    Object serialize(ReportContext context);
    
    boolean isCompatibleWith(OutputFormat outputFormat);
}
