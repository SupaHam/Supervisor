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

    @Override public boolean add(Context context) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public boolean remove(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public boolean addAll(@Nonnull Collection<? extends Context> c) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public boolean addAll(int index, Collection<? extends Context> c) {
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

    @Override public Context set(int index, Context element) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public void add(int index, Context element) {
        throw new UnsupportedOperationException("Report is immutable.");
    }

    @Override public Context remove(int index) {
        throw new UnsupportedOperationException("Report is immutable.");
    }
}
