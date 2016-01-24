package com.supaham.supervisor.report;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an amendable class, with access to the amends through {@link #getEntries()}.
 */
public interface Amendable {

    /**
     * Appends a {@code key}, alongside a formatted message with values.
     *
     * @param key key
     * @param message message
     * @param values values to format the message with
     */
    void append(@Nonnull String key, @Nonnull String message, @Nullable Object... values);

    /**
     * Appends a {@code key} with a {@code byte} value.
     *
     * @param key key
     * @param value byte value
     */
    void append(@Nonnull String key, byte value);

    /**
     * Appends a {@code key} with a {@code short} value.
     *
     * @param key key
     * @param value short value
     */
    void append(@Nonnull String key, short value);

    /**
     * Appends a {@code key} with an {@code int} value.
     *
     * @param key key
     * @param value int value
     */
    void append(@Nonnull String key, int value);

    /**
     * Appends a {@code key} with a {@code long} value.
     *
     * @param key key
     * @param value long value
     */
    void append(@Nonnull String key, long value);

    /**
     * Appends a {@code key} with a {@code float} value.
     *
     * @param key key
     * @param value float value
     */
    void append(@Nonnull String key, float value);

    /**
     * Appends a {@code key} with a {@code double} value.
     *
     * @param key key
     * @param value double value
     */
    void append(@Nonnull String key, double value);

    /**
     * Appends a {@code key} with a {@code boolean} value.
     *
     * @param key key
     * @param value boolean value
     */
    void append(@Nonnull String key, boolean value);

    /**
     * Appends a {@code key} with a {@code char} value.
     *
     * @param key key
     * @param value char value
     */
    void append(@Nonnull String key, char value);

    /**
     * Appends a {@code key} with an {@link Object} value.
     *
     * @param key key
     * @param value value, nullable
     */
    void append(@Nonnull String key, @Nullable Object value);

    @Nonnull Map<String, Object> getEntries();
}
