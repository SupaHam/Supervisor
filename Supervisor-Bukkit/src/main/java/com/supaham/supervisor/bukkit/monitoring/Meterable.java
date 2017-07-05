package com.supaham.supervisor.bukkit.monitoring;

import net.kyori.text.Component;

/**
 * Represents an object that can be represented as a meter in the form of a {@link Component}.
 */
public interface Meterable {

    Component getMeter();
}
