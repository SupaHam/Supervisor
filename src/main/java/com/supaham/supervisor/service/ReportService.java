package com.supaham.supervisor.service;

import com.supaham.supervisor.report.Report.ReportResult;

import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

/**
 * Created by Ali on 21/10/2015.
 */
public interface ReportService {

    boolean publish(@Nonnull CommandSender sender);
    @Nonnull ReportResult getReportResult();
}
