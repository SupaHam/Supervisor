package com.supaham.supervisor.bukkit;

import com.sk89q.intake.CommandException;
import com.sk89q.intake.parametric.handler.ExceptionConverterHelper;
import com.sk89q.intake.parametric.handler.ExceptionMatch;
import com.supaham.commons.bukkit.commands.CommonCommandsManager;
import com.supaham.supervisor.SupervisorException;

public class SupervisorCommandsManager extends CommonCommandsManager {

    public SupervisorCommandsManager(SupervisorPlugin plugin) {
        super(plugin);
        this.builder.addExceptionConverter(new SupervisorExceptionConverter());
        builder().registerMethods(new SupervisorCommands());
    }

    public static final class SupervisorExceptionConverter extends ExceptionConverterHelper {

        @ExceptionMatch
        public void convert(SupervisorException e) throws CommandException {
            throw new CommandException(e.getMessage());
        }
    }
}
