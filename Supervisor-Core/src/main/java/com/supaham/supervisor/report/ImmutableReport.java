package com.supaham.supervisor.report;

import java.util.Collection;

import javax.annotation.Nonnull;

/**
 * Represents an immutable {@link Report}.
 */
public class ImmutableReport extends Report {

    public ImmutableReport(@Nonnull Report report) {
        super(report);
    }

    @Override public boolean add(ReportContextEntry context) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public boolean remove(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public boolean addAll(@Nonnull Collection<? extends ReportContextEntry> c) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public boolean addAll(int index, Collection<? extends ReportContextEntry> c) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public boolean removeAll(@Nonnull Collection<?> c) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public boolean retainAll(@Nonnull Collection<?> c) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public void clear() {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public ReportContextEntry set(int index, ReportContextEntry element) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public void add(int index, ReportContextEntry element) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public ReportContextEntry remove(int index) {
        throw new UnsupportedOperationException("Report is immutable.");
    }
}
