package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;

import com.supaham.commons.utils.StringUtils;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Created by Ali on 15/10/2015.
 */
public class ReportSpecs {

    private final Plugin owner;
    private final ContextRegistry contextRegistry;
    private final String title;
    private final OutputFormat format;
    private final int reportLevel;
    private final Map<String, Integer> contextReportLevels;
    private final List<String> excludes;
    private final List<String> includes;

    public static ReportSpecsBuilder builder(@Nonnull Plugin owner) {
        return new ReportSpecsBuilder(Preconditions.checkNotNull(owner, "owner plugin cannot be null."));
    }

    private ReportSpecs(Plugin owner, ContextRegistry contextRegistry, String title, OutputFormat format, int reportLevel,
                       Map<String, Integer> contextReportLevels, List<String> excludes, List<String> includes) {
        this.owner = owner;
        this.contextRegistry = contextRegistry;
        this.title = StringUtils.checkNotNullOrEmpty(title, "title");
        this.format = format;
        this.reportLevel = reportLevel;
        this.contextReportLevels = contextReportLevels;
        this.excludes = excludes;
        this.includes = includes;
    }

    public ReportSpecs(@Nonnull ReportSpecs reportSpecs) {
        Preconditions.checkNotNull(reportSpecs, "report specs cannot be null.");
        this.owner = reportSpecs.owner;
        this.contextRegistry = reportSpecs.contextRegistry;
        this.title = reportSpecs.title;
        this.format = reportSpecs.format;
        this.reportLevel = reportSpecs.reportLevel;
        this.contextReportLevels = new HashMap<>(reportSpecs.contextReportLevels);
        this.excludes = reportSpecs.excludes;
        this.includes = reportSpecs.includes;
    }

    public Plugin getOwner() {
        return owner;
    }

    public ContextRegistry getContextRegistry() {
        return contextRegistry;
    }

    public String getTitle() {
        return title;
    }

    public OutputFormat getFormat() {
        return format;
    }

    /**
     * Returns the report level integer for a {@link Context}. If no specific report level has been set for the given context, the default {@link
     * #getReportLevel()} is returned.
     *
     * @param context context to get report level for
     *
     * @return context's report level
     */
    public int getReportLevel(@Nonnull Context context) {
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


    public static final class ReportLevel {

        public static int BRIEFEST = 0;
        public static int BRIEF = 200;
        public static int NORMAL = 400;
        public static int VERBOSE = 500;
        public static int MORE_VERBOSE = 700;
        public static int MOST_VERBOSE = 1000;
    }

    public static class ReportSpecsBuilder {

        private Plugin owner;
        private ContextRegistry contextRegistry;
        private String title;
        private OutputFormat format;
        private int reportLevel;
        private Map<String, Integer> contextReportLevels;
        private List<String> excludes;
        private List<String> includes;

        private ReportSpecsBuilder(Plugin owner) {
            this.owner = owner;
        }

        public ReportSpecsBuilder contextRegistry(@Nonnull ContextRegistry contextRegistry) {
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

        public ReportSpecsBuilder but() {
            return builder(owner).contextRegistry(contextRegistry).title(title).format(format).reportLevel(reportLevel)
                .contextReportLevels(contextReportLevels).excludes(excludes).includes(includes);
        }

        public ReportSpecs build() {
            Preconditions.checkNotNull(this.contextRegistry, "contextRegistry cannot be null.");
            StringUtils.checkNotNullOrEmpty(this.title, "title");
            Preconditions.checkNotNull(this.format, "format cannot be null.");
            
            if (contextReportLevels == null) {
                contextReportLevels = new HashMap<>();
            }
            if (excludes == null) {
                excludes = new ArrayList<>();
            }
            if (includes == null) {
                includes = new ArrayList<>();
            }
            return new ReportSpecs(owner, contextRegistry, title, format, reportLevel, contextReportLevels, excludes, includes);
        }
    }
}
