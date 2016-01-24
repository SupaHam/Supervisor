package com.supaham.supervisor.bukkit.monitoring;

import com.supaham.commons.bukkit.text.FancyMessage;

/**
 * Represents an object that can be represented as a meter in the form of a {@link FancyMessage}.
 */
public interface Meterable {

    FancyMessage getMeter();
}
