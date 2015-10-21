package com.supaham.supervisor.contexts;

import com.supaham.supervisor.report.ReportSpecs.ReportLevel;
import com.supaham.supervisor.report.SimpleContext;

/**
 * Hello World dummy {@link SimpleContext} for testing purposes.
 */
public class HelloWorldContext extends SimpleContext {

    public HelloWorldContext() {
        super("hello-world", "Hello World");
    }

    @Override public void run() {
        // Always append "message": "Hello, World" to our report.
        append("message", "Hello, World!");

        // Only append the following data if the report level for this context is higher than normal.
        if (getReportLevel() > ReportLevel.NORMAL) {
            append("more_info", "Hello world is dope af!");
        }
    }
}
