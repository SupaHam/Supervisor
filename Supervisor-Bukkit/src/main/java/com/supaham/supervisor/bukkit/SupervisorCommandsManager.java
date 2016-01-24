package com.supaham.supervisor.bukkit;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.util.command.fluent.CommandGraph;
import com.sk89q.worldedit.util.command.fluent.DispatcherNode;
import com.sk89q.worldedit.util.command.parametric.ExceptionConverterHelper;
import com.sk89q.worldedit.util.command.parametric.ExceptionMatch;
import com.sk89q.worldedit.util.command.parametric.ParametricBuilder;
import com.supaham.commons.bukkit.worldedit.CommandsManager;
import com.supaham.supervisor.SupervisorException;

public class SupervisorCommandsManager extends CommandsManager {

    public SupervisorCommandsManager(SupervisorPlugin plugin) {
        super(plugin);

        DispatcherNode graph = getCommandGraph();
        graph.registerMethods(new SupervisorCommands());
    }

    @Override public CommandGraph init() {
        ParametricBuilder builder = getDefaultParametricBuilder(getPlugin());
        builder.addExceptionConverter(new SupervisorExceptionConverter());
        return new CommandGraph().builder(builder);
    }

    @Override public void registerCommands() {
        SupervisorPlugin.log().fine("Registering commands...");
        super.registerCommands();
        SupervisorPlugin.log().fine("Commands registered...");
    }

    private static final class SupervisorExceptionConverter extends ExceptionConverterHelper {

        @ExceptionMatch
        public void convert(SupervisorException e) throws CommandException {
            throw new CommandException(e.getMessage());
        }
    }
}
