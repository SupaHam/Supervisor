package com.supaham.supervisor.report.serializers;

import com.google.common.base.Preconditions;

import com.supaham.supervisor.Supervisor;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

public abstract class AbstractContextSerializer implements ContextSerializer {

    protected final Supervisor supervisor;

    public AbstractContextSerializer(@Nonnull Supervisor supervisor) {
        this.supervisor = Preconditions.checkNotNull(supervisor, "supervisor cannot be null.");
    }

    public Logger getLogger() {
        return supervisor.getLogger();
    }
}
