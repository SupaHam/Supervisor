package com.supaham.supervisor.bukkit;

import com.supaham.commons.bukkit.commands.flags.Flag;
import com.supaham.commons.bukkit.commands.flags.FlagParseResult;
import com.supaham.commons.bukkit.commands.flags.FlagParser;
import com.supaham.commons.exceptions.CommonException;
import com.supaham.commons.utils.StringUtils;
import com.supaham.supervisor.report.OutputFormat;
import com.supaham.supervisor.report.Report.ReportResult;
import com.supaham.supervisor.report.ReportSpecifications.ReportSpecsBuilder;
import com.supaham.supervisor.service.ReportServiceManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;

public class SupervisorCommands extends BaseCommand {

    private static final String HELP = "/<command> [flags] [help/reload]\n"
        + "Flags:\n"
        + "  -h displays help menu.\n"
        + "  -v displays version.\n"
        + "  -t sets the report's title.\n"
        + "  -f sets the output format of the report.\n"
        + "  -e <context...> excludes named contexts from the report.\n"
        + "  -i <context...> includes named contexts in the report.\n"
        + "  -l sets the report output level (verboseness).";

    private FlagParser flags = new FlagParser();

    {
        flags.add(new Flag('v', "version", true, false));
        flags.add(new Flag('h', "help", true, false));
        flags.add(new Flag('t', "title", true, true));
        flags.add(new Flag('f', "format", true, true));
        flags.add(new Flag('e', "excludes", true, true));
        flags.add(new Flag('i', "includes", true, true));
        flags.add(new Flag('l', "report-level", true, true));
    }

    @CommandAlias("svreport|sv|supervisor")
    @CommandPermission("supervisor.use")
    public void svreport(CommandSender sender, @Optional String[] args) throws CommonException {
        SupervisorPlugin plugin = SupervisorPlugin.get();
        FlagParseResult flags = this.flags.parseFor(sender, args);
        if (flags == null) {
            return; // Sender already notified
        }
        args = flags.getArgs(); // Update args after flag parsing

        if (flags.contains('h') || (args.length == 1
            && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")))) {
            sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Supervisor");
            sender.sendMessage(HELP);
            return;
        }

        if (flags.contains('v')) {
            sender.sendMessage(ChatColor.GREEN + "Supervisor v" + plugin.getDescription().getVersion());
            return;
        }

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

        String excludesString = StringUtils.stripToNull(flags.get('e'));
        String includesString = StringUtils.stripToNull(flags.get('i'));

        List<String> excludes = excludesString == null ? Collections.emptyList() : Arrays.asList(excludesString.split("\\s+|,"));
        List<String> includes = includesString == null ? Collections.emptyList() : Arrays.asList(includesString.split("\\s+|,"));

        ReportSpecsBuilder builder = plugin.createDefaultBuilder().excludes(excludes).includes(includes);
        if (flags.contains('t')) {
            builder.title(flags.get('t'));
        }
        if (flags.contains('f')) {
            String format = flags.get('f');
            OutputFormat outputFormat = OutputFormat.getByName(format);
            if (outputFormat == null) {
                throw new CommonException("'" + format + "' is not a valid format.");
            }
            builder.format(outputFormat);
        }

        if (flags.contains('l')) {
            builder.reportLevel(Integer.parseInt(flags.get('l')));
        }

        if (args.length > 0) {
            builder.arguments(Arrays.asList(args));
        }

        ReportResult result = SupervisorPlugin.createReport(builder.build()).call();
        ReportServiceManager.createService(result, "gist").publish(result, new CommandSenderMessageRecipient(sender));
    }
}
