package com.supaham.supervisor.report;

import java.util.Collection;
import java.util.Map;

public interface ReportContextRegistry {

    Collection<ReportContext> getContexts();

    Collection<ReportContext> getSortedContexts();

    Map<String, ReportContext> getRegistry();
}
