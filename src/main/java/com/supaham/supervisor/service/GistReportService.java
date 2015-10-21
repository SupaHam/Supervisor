package com.supaham.supervisor.service;

import com.supaham.commons.bukkit.TickerTask;
import com.supaham.supervisor.SupervisorPlugin;
import com.supaham.supervisor.report.Report.ReportResult;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Nonnull;

/**
 * Created by Ali on 21/10/2015.
 */
public class GistReportService extends AbstractReportService {

    public GistReportService(@Nonnull ReportResult reportResult) {
        super(reportResult);
    }

    @Override public boolean publish(@Nonnull final CommandSender sender) {
        new TickerTask(reportResult.getReportSpecs().getOwner(), 0) {
            @Override public void run() {
                Gist gist = new Gist().setDescription(
                    reportResult.getReportSpecs().getTitle() + " - " + SupervisorPlugin.TITLE_SUFFIX);
                GistFile gistFile = new GistFile().setContent(reportResult.getOutput().toString()).setFilename("report.json");
                gist.setFiles(Collections.singletonMap("report.json", gistFile));
                try {
                    gist = new GistService().createGist(gist);
                    sender.sendMessage("Gisted! " + gist.getHtmlUrl());
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.startAsync();
        return true;
    }
}
