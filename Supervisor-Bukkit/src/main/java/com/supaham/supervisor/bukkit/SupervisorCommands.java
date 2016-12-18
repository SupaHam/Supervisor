package com.supaham.supervisor.bukkit;

import com.sk89q.intake.Command;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Optional;
import com.sk89q.intake.parametric.annotation.Switch;
import com.sk89q.intake.parametric.annotation.Text;
import com.supaham.commons.exceptions.CommonException;
import com.supaham.commons.utils.StringUtils;
import com.supaham.supervisor.SupervisorException;
import com.supaham.supervisor.report.OutputFormat;
import com.supaham.supervisor.report.Report.ReportResult;
import com.supaham.supervisor.report.ReportSpecifications.ReportSpecsBuilder;
import com.supaham.supervisor.service.ReportServiceManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SupervisorCommands {

    private static final String HELP = "/<command> [flags] [help/reload]\n" 
        + "Flags:\n" 
        + "  -h displays help menu.\n"
        + "  -v displays version.\n"
        + "  -t sets the report's title.\n"
        + "  -f sets the output format of the report.\n"
        + "  -e <context...> excludes named contexts from the report.\n"
        + "  -i <context...> includes named contexts in the report.\n"
        + "  -l sets the report output level (verboseness).";

    @Command(aliases = {"svreport", "sv", "supervisor"}, desc = "Generates a Supervisor report.",
        help = HELP)
    @Require("supervisor.use")
    public void svreport(CommandSender sender, @Optional @Text String argsString,
                         @Switch('v') boolean version,
                         @Switch('h') boolean help,
                         @Switch('t') String title,
                         @Switch('f') String format,
                         @Switch('e') String excludesString,
                         @Switch('i') String includesString,
                         @Switch('l') Integer reportLevel) throws CommonException {
        SupervisorPlugin plugin = SupervisorPlugin.get();

        argsString = StringUtils.trimToNull(argsString);
        if (help || "help".equalsIgnoreCase(argsString) || "?".equalsIgnoreCase(argsString)) {
            sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Supervisor");
            sender.sendMessage(HELP);
            return;
        }

        if (version) {
            sender.sendMessage(ChatColor.GREEN + "Supervisor v" + plugin.getDescription().getVersion());
            return;
        }

        List<String> args = argsString == null ? Collections.<String>emptyList() : Arrays.asList(argsString.split("\\s"));
        for (String arg : args) {
            if ("reload".equals(arg.toLowerCase())) {
                if (!sender.hasPermission("supervisor.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission.");
                    return;
                }

                if (SupervisorPlugin.get().reloadSettings()) {
                    sender.sendMessage(ChatColor.GREEN + "You've successfully reloaded the Supervisor configuration file.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to load configuration. Please check the console for errors.");
                }
                return;
            }
        }

        excludesString = StringUtils.stripToNull(excludesString);
        includesString = StringUtils.stripToNull(includesString);

        List<String> excludes = excludesString == null ? Collections.<String>emptyList() : Arrays.asList(excludesString.split("\\s+|,"));
        List<String> includes = includesString == null ? Collections.<String>emptyList() : Arrays.asList(includesString.split("\\s+|,"));

        ReportSpecsBuilder builder = plugin.createDefaultBuilder().excludes(excludes).includes(includes);
        if (title != null) {
            builder.title(title);
        }
        if (format != null) {
            OutputFormat outputFormat = OutputFormat.getByName(format);
            if (outputFormat == null) {
                throw new CommonException("'" + format + "' is not a valid format.");
            }
            builder.format(outputFormat);
        }

        if (reportLevel != null) {
            builder.reportLevel(reportLevel);
        }

        if (!args.isEmpty()) {
            builder.arguments(args);
        }

        ReportResult result = SupervisorPlugin.createReport(builder.build()).call();
        ReportServiceManager.createService(result, "gist").publish(result, new CommandSenderMessageRecipient(sender));
    }
}
