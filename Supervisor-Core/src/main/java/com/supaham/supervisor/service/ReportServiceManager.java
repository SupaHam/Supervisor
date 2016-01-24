package com.supaham.supervisor.service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;

import com.supaham.commons.utils.StringUtils;
import com.supaham.supervisor.report.Report.ReportResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Ali on 21/10/2015.
 */
public class ReportServiceManager {

    private static final Map<String, Function<ReportResult, ReportService>> registered = new HashMap<>();

    static {
        register("gist", new Function<ReportResult, ReportService>() {
            @Nullable @Override public ReportService apply(ReportResult input) {
                return new GistReportService(MoreExecutors.listeningDecorator(new ThreadPoolExecutor(0, 1, 3, TimeUnit.MINUTES,
                    new LinkedBlockingDeque<Runnable>())));
            }
        });
    }

    /**
     * Creates a {@link ReportService} instance from a case insensitive name registered function.
     *
     * @param name case-insensitive name
     *
     * @return report service
     *
     * @throws IllegalArgumentException thrown if {@code name} is not registered
     */
    public static ReportService createService(@Nonnull ReportResult reportResult, @Nonnull String name) throws IllegalArgumentException {
        Function<ReportResult, ReportService> function = registered.get(name.toLowerCase());
        Preconditions.checkArgument(function != null, "'" + name + "' is not a registered reporting service.");
        return function.apply(reportResult);
    }

    /**
     * Registers a {@link ReportService} through a {@link Function}. The {@code name} is lower-cased when registered and checked. The {@code
     * function} used to provide a {@link ReportService} is provided with a {@link ReportResult}, which should be utilized to output a useful report
     * access point.
     *
     * @param name name
     * @param function function used to create a {@link ReportService} when required
     *
     * @throws IllegalArgumentException thrown if {@code name} is already registered, or if it was an empty
     */
    public static void register(@Nonnull String name, @Nonnull Function<ReportResult, ReportService> function) throws IllegalArgumentException {
        StringUtils.checkNotNullOrEmpty(name, "name");
        Preconditions.checkNotNull(function, "function cannot be null.");
        String lowerCasedName = name.toLowerCase();
        Preconditions.checkArgument(!registered.containsKey(lowerCasedName), "'" + name + "' is already registered as a service");
        registered.put(lowerCasedName, function);
    }
}
