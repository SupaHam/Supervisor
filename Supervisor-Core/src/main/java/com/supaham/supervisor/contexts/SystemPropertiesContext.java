package com.supaham.supervisor.contexts;

import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportSpecifications;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;
import com.supaham.supervisor.report.ReportContext;
import com.supaham.supervisor.report.AbstractReportContextEntry;

import javax.annotation.Nonnull;

public class SystemPropertiesContext extends ReportContext {

    public SystemPropertiesContext() {
        super("system-properties", "System Properties", "1");
    }

    @Override
    public ReportContextEntry createEntry(@Nonnull ReportSpecifications specs) {
        return new SystemPropertiesEntry(this, specs);
    }

    private static final class SystemPropertiesEntry extends AbstractReportContextEntry {

        public SystemPropertiesEntry(@Nonnull ReportContext parentContext, @Nonnull ReportSpecifications specs) {
            super(parentContext, specs);
        }

        @Override
        public void run() {
            sensitive("java.class.path");
            sensitive("java.home");
            normal("java.vendor");
            normal("java.vendor.url");
            normal("java.vendor.url.bug");
            briefest("java.version");
            normal("java.vm.name");
            normal("java.vm.specification.name");
            normal("java.vm.specification.vendor");
            briefest("java.vm.specification.version");
            normal("java.vm.vendor");
            briefest("java.vm.version");
            normal("java.runtime.name");
            normal("java.runtime.version");
            normal("os.arch");
            briefest("os.name");
            normal("os.version");
            sensitive("user.dir");
            sensitive("user.home");
            sensitive("user.name");
            normal("user.country");
            normal("user.language");
            normal("user.timezone");
        }

        private void briefest(String property) {
            append(property, System.getProperty(property));
        }

        private void normal(String property) {
            if (getReportLevel() >= ReportLevel.NORMAL) {
                append(property, System.getProperty(property));
            }
        }

        private void sensitive(String property) {
            if (getReportLevel() >= ReportLevel.MORE_VERBOSE) {
                append(property, System.getProperty(property));
            }
        }
    }
}
