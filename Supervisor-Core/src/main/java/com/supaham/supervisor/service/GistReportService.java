package com.supaham.supervisor.service;

import com.supaham.supervisor.Supervisor;
import com.supaham.supervisor.report.Report.ReportResult;
import com.supaham.supervisor.report.ReportOutput;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nonnull;

/**
 * Represents a GitHub Gists report service which prints the gist html url on success.
 */
public class GistReportService extends AbstractReportService {

    private ExecutorService executorService;

    public GistReportService(@Nonnull ExecutorService executorService) {
        super("Gist");
        this.executorService = executorService;
    }

    @Override public void publish(@Nonnull final ReportResult reportResult, @Nonnull final MessageRecipient recipient) {
        this.executorService.submit(new Runnable() {
            @Override public void run() {
                try {
                    Gist gist = new Gist().setDescription(reportResult.getReportSpecifications().getTitle() + " - " + Supervisor.TITLE_SUFFIX);
                    ReportOutput output = reportResult.getOutput();
                    Map<String, GistFile> gists = new HashMap<>();
                    for (Entry<String, String> entry : output.getFiles().entrySet()) {
                        if(entry.getValue().isEmpty()) {
                            getLogger().warning("File " + entry.getKey() + " is empty.");
                        } else {
                            gists.put(entry.getKey().replaceAll("/", "\\\\"), new GistFile().setContent(entry.getValue()));
                        }
                    }
                    gist.setFiles(gists);
                    try{
                        gist = new GistService().createGist(gist);
                        recipient.printSuccess(gist.getHtmlUrl());
                    } catch (IOException e) {
                        getLogger().warning("Error occurred while publishing report id " + reportResult.getReportSpecifications().getUuid());
                        recipient.error(e);
                    }
                } catch (Exception e) {
                    getLogger().warning("Error occurred while publishing report id " + reportResult.getReportSpecifications().getUuid());
                    recipient.error(e);
                }
            }
        });
    }
}
