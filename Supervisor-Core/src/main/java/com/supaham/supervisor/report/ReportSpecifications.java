package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;

import com.supaham.commons.utils.StringUtils;
import com.supaham.supervisor.Supervisor;
import com.supaham.supervisor.report.serializers.ReportSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

/**
 * Represents the specifications for a {@link Report} to be created.
 */
public class ReportSpecifications {

    private final Supervisor supervisor;
    private final String owner;
    private final UUID uuid;
    private final ReportContextRegistry contextRegistry;
    private final String title;
    private final OutputFormat format;
    private final int reportLevel;
    private final Map<String, Integer> contextReportLevels;
    private final List<String> excludes;
    private final List<String> includes;
    private final ReportSerializer reportSerializer;

    public static ReportSpecsBuilder builder() {
        return new ReportSpecsBuilder();
    }

    private ReportSpecifications(Supervisor supervisor, String owner, UUID uuid, ReportContextRegistry contextRegistry, String title, OutputFormat format,
                                 int reportLevel, Map<String, Integer> contextReportLevels, List<String> excludes, List<String> includes,
                                 ReportSerializer reportSerializer) {
        this.supervisor = supervisor;
        this.owner = owner;
        this.uuid = uuid;
        this.contextRegistry = contextRegistry;
        this.title = StringUtils.checkNotNullOrEmpty(title, "title");
        this.format = format;
        this.reportLevel = reportLevel;
        this.contextReportLevels = contextReportLevels;
        this.excludes = excludes;
        this.includes = includes;
        this.reportSerializer = reportSerializer;
    }

    public ReportSpecifications(@Nonnull ReportSpecifications reportSpecifications) {
        Preconditions.checkNotNull(reportSpecifications, "report specs cannot be null.");
        this.supervisor = reportSpecifications.supervisor;
        this.owner = reportSpecifications.owner;
        this.uuid = reportSpecifications.uuid;
        this.contextRegistry = reportSpecifications.contextRegistry;
        this.title = reportSpecifications.title;
        this.format = reportSpecifications.format;
        this.reportLevel = reportSpecifications.reportLevel;
        this.contextReportLevels = new HashMap<>(reportSpecifications.contextReportLevels);
        this.excludes = reportSpecifications.excludes;
        this.includes = reportSpecifications.includes;
        this.reportSerializer = reportSpecifications.reportSerializer;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public String getOwner() {
        return owner;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ReportContextRegistry getContextRegistry() {
        return contextRegistry;
    }

    public String getTitle() {
        return title;
    }

    public OutputFormat getFormat() {
        return format;
    }

    /**
     * Returns the report level integer for a {@link ReportContext}. If no specific report level has been set for the given context, the default {@link
     * #getReportLevel()} is returned.
     *
     * @param context context to get report level for
     *
     * @return context's report level
     */
    public int getReportLevel(@Nonnull ReportContext context) {
        Preconditions.checkNotNull(context, "context cannot be null.");
        Integer integer = this.contextReportLevels.get(context.getName());
        return integer == null ? this.reportLevel : integer;
    }

    public int getReportLevel() {
        return reportLevel;
    }

    public Map<String, Integer> getContextReportLevels() {
        return Collections.unmodifiableMap(contextReportLevels);
    }

    public List<String> getExcludes() {
        return Collections.unmodifiableList(excludes);
    }

    public List<String> getIncludes() {
        return Collections.unmodifiableList(includes);
    }

    public ReportSerializer getReportSerializer() {
        return reportSerializer;
    }

    public static final class ReportLevel {

        public static int BRIEFEST = 0;
        public static int BRIEF = 200;
        public static int NORMAL = 400;
        public static int VERBOSE = 500;
        public static int MORE_VERBOSE = 700;
        public static int MOST_VERBOSE = 1000;
    }

    public static class ReportSpecsBuilder {

        private Supervisor supervisor;
        private String owner;
        private UUID uuid;
        private ReportContextRegistry contextRegistry;
        private String title;
        private OutputFormat format;
        private int reportLevel;
        private Map<String, Integer> contextReportLevels;
        private List<String> excludes;
        private List<String> includes;
        private ReportSerializer reportSerializer;

        private ReportSpecsBuilder() {
        }

        public ReportSpecsBuilder supervisor(@Nonnull Supervisor supervisor) {
            this.supervisor = supervisor;
            return this;
        }

        public ReportSpecsBuilder owner(@Nonnull String owner) {
            this.owner = owner;
            return this;
        }

        public ReportSpecsBuilder supervisor(@Nonnull UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public ReportSpecsBuilder contextRegistry(@Nonnull ReportContextRegistry contextRegistry) {
            this.contextRegistry = contextRegistry;
            return this;
        }

        public ReportSpecsBuilder title(@Nonnull String title) {
            this.title = title;
            return this;
        }

        public ReportSpecsBuilder format(@Nonnull OutputFormat format) {
            this.format = format;
            return this;
        }

        public ReportSpecsBuilder reportLevel(int reportLevel) {
            Preconditions.checkArgument(reportLevel >= 0, "reportLevel cannot be less than 0");
            this.reportLevel = reportLevel;
            return this;
        }

        public ReportSpecsBuilder contextReportLevels(@Nonnull Map<String, Integer> contextReportLevels) {
            this.contextReportLevels = contextReportLevels;
            return this;
        }

        public ReportSpecsBuilder contextReportLevel(@Nonnull String contextName, int reportLevel) {
            if (this.contextReportLevels == null) {
                this.contextReportLevels = new HashMap<>();
            }
            this.contextReportLevels.put(contextName, reportLevel);
            return this;
        }

        public ReportSpecsBuilder excludes(@Nonnull List<String> excludes) {
            this.excludes = excludes;
            return this;
        }

        public ReportSpecsBuilder includes(@Nonnull List<String> includes) {
            this.includes = includes;
            return this;
        }

        public ReportSpecsBuilder reportSerializer(@Nonnull ReportSerializer reportSerializer) {
            this.reportSerializer = reportSerializer;
            return this;
        }

        public ReportSpecsBuilder but() {
            return builder().owner(owner).contextRegistry(contextRegistry).title(title).format(format).reportLevel(reportLevel)
                .contextReportLevels(contextReportLevels).excludes(excludes).includes(includes).reportSerializer(reportSerializer);
        }

        public ReportSpecifications build() {
            Preconditions.checkNotNull(owner, "owner cannot be null.");
            Preconditions.checkNotNull(this.supervisor, "supervisor cannot be null.");
            Preconditions.checkNotNull(this.contextRegistry, "contextRegistry cannot be null.");
            StringUtils.checkNotNullOrEmpty(this.title, "title");
            Preconditions.checkNotNull(this.format, "format cannot be null.");

            if (uuid == null) {
                this.uuid = UUID.randomUUID();
            }
            if (contextReportLevels == null) {
                contextReportLevels = new HashMap<>();
            }
            if (excludes == null) {
                excludes = new ArrayList<>();
            }
            if (includes == null) {
                includes = new ArrayList<>();
            }
            return new ReportSpecifications(supervisor, owner, uuid, contextRegistry, title, format, reportLevel, contextReportLevels, excludes, includes,
                reportSerializer);
        }
    }
}
