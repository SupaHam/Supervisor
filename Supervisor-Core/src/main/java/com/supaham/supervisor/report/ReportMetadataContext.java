package com.supaham.supervisor.report;

import com.google.common.collect.ImmutableMap;

import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;
import com.supaham.supervisor.utils.TaskTimings;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nonnull;

/**
 * Created by Ali on 04/11/2015.
 */
public final class ReportMetadataContext extends ReportContext {

    public ReportMetadataContext() {
        super("report-metadata", "Report Metadata", "1");
    }

    @Override
    public ReportContextEntry createEntry(@Nonnull ReportSpecifications specs) {
        return new ReportMetadataContextEntry(this, specs);
    }

    public final class ReportMetadataContextEntry extends AbstractReportContextEntry {
        
        private final Map<String, TaskTimings> contextMetadata = new LinkedHashMap<>();
        protected Report report;

        public ReportMetadataContextEntry(@Nonnull ReportContext parentContext, @Nonnull ReportSpecifications reportSpecifications) {
            super(parentContext, reportSpecifications);
            start(this);
        }

        @Override public void run() {
            stop(this);
            append("uuid", UUID.randomUUID());
            append("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            append("specifications", ImmutableMap.builder()
                .put("owner", getReportSpecifications().getOwner())
                .put("title", getReportSpecifications().getTitle())
                .put("format", getReportSpecifications().getFormat().getName())
                .put("report_level", getReportSpecifications().getReportLevel())
                .put("context_report_levels", getReportSpecifications().getContextReportLevels())
                .put("excludes", getReportSpecifications().getExcludes())
                .put("includes", getReportSpecifications().getIncludes())
                .put("arguments", getReportSpecifications().getArguments())
                .put("included_contexts", getContextNames())
                .build()
            );
            append("timings", getTimings());
        }

        private List<String> getContextNames() {
            List<String> result = new ArrayList<>(report.size());
            for (ReportContextEntry context : report) {
                result.add(context.getParentContext().getName());
            }
            return result;
        }

        private Object getTimings() {
            // Store timings as an array instead of object as should be.
            ArrayList<Map<String, Object>> timings = new ArrayList<>();
            for (Entry<String, TaskTimings> entry : contextMetadata.entrySet()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("context", entry.getKey());
                map.put("duration_ms", entry.getValue().getDurationInMillis());
                if (getReportLevel() >= ReportLevel.NORMAL) {
                    map.put("duration_ns", entry.getValue().getDurationInNanos());

                    map.put("start_ms", entry.getValue().getStartMillis());
                    map.put("end_ms", entry.getValue().getEndMillis());
                    map.put("start_ns", entry.getValue().getStartNanos());
                    map.put("end_ns", entry.getValue().getEndNanos());
                }
                timings.add(map);
            }
            return timings;
        }

        public void start(ReportContextEntry contextEntry) {
            if (getReportSpecifications().getReportLevel() >= ReportLevel.BRIEF || contextEntry instanceof ReportMetadataContextEntry) {
                TaskTimings timings = new TaskTimings();
                timings.start();
                this.contextMetadata.put(contextEntry.getParentContext().getName(), timings);
            }
        }

        public void stop(ReportContextEntry context) {
            TaskTimings timings = this.contextMetadata.get(context.getParentContext().getName());
            if (timings != null) {
                timings.stop();
            }
        }

        public TaskTimings getReportTimings() {
            return contextMetadata.get(getName());
        }
    }
}
