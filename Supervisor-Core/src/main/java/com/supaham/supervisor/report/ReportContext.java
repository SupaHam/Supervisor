package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;

import com.supaham.commons.utils.StringUtils;

import javax.annotation.Nonnull;

/**
 * Represents a context that belongs in a {@link ReportContextRegistry} where it may later be present in a {@link Report}.
 */
public class ReportContext implements ReportContextComparable {

    private final String name;
    private final String title;
    private final String version;

    public ReportContext(@Nonnull String name, @Nonnull String title, @Nonnull String version) {
        this.name = StringUtils.checkNotNullOrEmpty(name, "name");
        this.title = StringUtils.checkNotNullOrEmpty(title, "title");
        this.version = StringUtils.checkNotNullOrEmpty(version, "version");
    }
    /**
     * This method fires when a {@link Report} is requesting this context's data.
     * <p />
     * <b>NOTE: If overridden, the override method should NOT call the super method.</b>
     */
    public void run(ReportContextEntry contextEntry) {
        try {
            // #run() will throw UnsupportedOperationException if createEntry isn't overridden. Otherwise, run the custom entry.
            contextEntry.run();
        } catch (UnsupportedOperationException e) {
            // Print default message to confirm functionality.
            contextEntry.append("not_implemented_yet", "This is the default implementation from Supervisor.");
        }
    }
    
    public ReportContextEntry createEntry(@Nonnull ReportSpecifications specs) {
        Preconditions.checkNotNull(specs, "specs cannot be null.");
        return new SimpleReportContextEntry(specs);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getTitle() {
        return title;
    }

    @Nonnull
    public String getVersion() {
        return version;
    }

    @Override
    public int compareTo(@Nonnull ReportContext o) {
        return getName().compareTo(o.getName());
    }

    private final class SimpleReportContextEntry extends AbstractReportContextEntry {

        public SimpleReportContextEntry(ReportSpecifications specs) {
            super(ReportContext.this, specs);
        }

        @Override
        public void run() {
            // We don't do anything since 
            throw new UnsupportedOperationException();
        }
    }
}
