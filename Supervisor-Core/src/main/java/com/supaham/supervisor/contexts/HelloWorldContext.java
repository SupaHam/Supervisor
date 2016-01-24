package com.supaham.supervisor.contexts;

import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;
import com.supaham.supervisor.report.ReportContext;

import javax.annotation.Nonnull;

/**
 * Hello World dummy {@link ReportContext} for testing purposes.
 */
public class HelloWorldContext extends ReportContext {

    public HelloWorldContext() {
        super("hello-world", "Hello World");
    }

    @Override public void run(ReportContextEntry entry) {
        // Always append "message": "Hello, World" to our report.
        entry.append("message", "Hello, World!");

        // Only append the following data if the report level for this context is higher than normal.
        if (entry.getReportLevel() > ReportLevel.NORMAL) {
            entry.append("more_info", "Hello world is dope af!");
        }
    }
}
