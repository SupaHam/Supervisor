package com.supaham.supervisor.report.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.supaham.supervisor.Supervisor;
import com.supaham.supervisor.report.OutputFormat;
import com.supaham.supervisor.report.Report;
import com.supaham.supervisor.report.ReportContextEntry;
import com.supaham.supervisor.report.ReportFile;
import com.supaham.supervisor.report.ReportMetadataContext.ReportMetadataContextEntry;
import com.supaham.supervisor.report.ReportOutput;
import com.supaham.supervisor.report.ReportSpecifications;
import com.supaham.supervisor.report.ReportSpecifications.ReportLevel;
import com.supaham.supervisor.report.formats.JSONFormat;

import org.json.simple.JSONArray;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class JsonReportSerializer extends AbstractReportSerializer {

    private static final JSONFormat OUTPUT_FORMAT = OutputFormat.JSON;
    private JSONArray root;
    private Map<String, String> files;

    public JsonReportSerializer(Supervisor supervisor) {
        super(supervisor);
    }

    @Override
    protected boolean pre(Report report) {
        if (super.pre(report)) {
            this.root = new JSONArray();
            this.files = new LinkedHashMap<>();
            return true;
        }
        return false;
    }

    @Override
    protected void post() {
        super.post();
        this.root = null;
        this.files = null;
    }

    @Override
    protected ReportOutput toOutput() {
        ReportOutput reportOutput = new ReportOutput(getReport().getReportSpecifications().getFormat(), OUTPUT_FORMAT.getGson().toJson(root));
        for (Entry<String, String> entry : files.entrySet()) {
            reportOutput.addFileWithExactName(entry.getKey(), entry.getValue());
        }
        return reportOutput;
    }

    @Override
    protected void each(int insertAt, ReportContextEntry contextEntry) {
        Object serialized = serializeContext(getReport(), contextEntry);
        if (insertAt < 0) {
            root.add(serialized);
        } else {
            root.add(insertAt, serialized);
        }
    }

    @Override
    public Object serializeContext(Report report, ReportContextEntry contextEntry) {
        return serialize(report, this.OUTPUT_FORMAT.getGson(), contextEntry);
    }

    @Override
    public boolean isCompatibleWith(OutputFormat outputFormat) {
        return this.OUTPUT_FORMAT.equals(outputFormat);
    }

    private JsonObject serialize(Report report, Gson gson, ReportContextEntry contextEntry) {
        ReportSpecifications specs = report.getReportSpecifications();

        String name = contextEntry.getParentContext().getName();
        String title = contextEntry.getParentContext().getTitle();
        JsonObject contextOutput = new JsonObject();
        contextOutput.addProperty("name", name);
        contextOutput.addProperty("title", title);

        // Output reportLevel if specified for the context
        if (specs.getContextReportLevels().containsKey(name)) {
            contextOutput.addProperty("report_level", specs.getContextReportLevels().get(name));
        }

        // Start timer for contextEntry
        if (!(contextEntry instanceof ReportMetadataContextEntry)) {
            report.getMetadataContextEntry().start(contextEntry);
        }
        try {
            // It is important that the ReportContext class be called instead of the contextEntry in case the ReportContext run() was overridden.
            contextEntry.getParentContext().run(contextEntry);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error occurred while running " + name, e);
        }

        try {
            // Only document files if it's not empty, or report level has been set to verbose (if empty).
            if (specs.getReportLevel() >= ReportLevel.VERBOSE || !contextEntry.getFiles().isEmpty()) {
                contextOutput.add("files", filesToJson(report, contextEntry));
            }

            contextOutput.add("data", gson.toJsonTree(contextEntry.output()));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e.getMessage(), e);
        }

        // Stop timer for contextEntry
        if (!(contextEntry instanceof ReportMetadataContextEntry)) {
            report.getMetadataContextEntry().stop(contextEntry);
        }
        return contextOutput;
    }

    private JsonElement filesToJson(Report report, ReportContextEntry contextEntry) {
        JsonArray array = new JsonArray();
        for (Entry<String, ReportFile> entry : contextEntry.getFiles().entrySet()) {
            JsonObject obj = new JsonObject();
            String path = report.sanitizeContextFile(contextEntry.getParentContext(), entry.getKey());
            obj.addProperty("path", path);
            obj.addProperty("title", entry.getValue().getFileTitle());
            array.add(obj);
            this.files.put(path, entry.getValue().output().toString());
        }
        return array;
    }
}
