package com.supaham.supervisor.report;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Represents the a wrapper class for the output of a {@link Report}, providing the ability to separate the report in <em>files</em>.
 */
public class ReportOutput {

    private final OutputFormat outputFormat;
    private final Map<String, String> files = new HashMap<>();

    /**
     * Constructs a new report output object with {@link ReportFile#ROOT_FILE_NAME} and the given format for the file extension, alongside the {@code
     * contents}. The format given is stored for later use when calling {@link #addFile(String, String)} to automatically append the extension. To
     * prevent this behaviour, call {@link #addFile(String, OutputFormat, String)} instead.
     *
     * @param outputFormat format to determine the extension type to use for files
     * @param contents contents of the file
     */
    public ReportOutput(@Nonnull OutputFormat outputFormat, @Nonnull String contents) {
        this.outputFormat = Preconditions.checkNotNull(outputFormat, "format cannot be null.");
        Preconditions.checkNotNull(contents, "contents cannot be null.");
        files.put(ReportFile.ROOT_FILE_NAME + "." + outputFormat.getExtensionType(), contents);
    }

    /**
     * Adds a new file, alongside its {@code contents}, to this report output. The file is suffixed with this {@link ReportOutput}'s
     * {@link OutputFormat} extension type, which was provided during construction.
     * <p />
     * <b>To override the extension type</b>, see {@link #addFile(String, OutputFormat, String)} and {@link #addFileWithExactName(String, String)}
     *
     * @param fileName file name, without extension type
     * @param contents contents of the file
     *
     * @see #addFileWithExactName(String, String)
     */
    public void addFile(@Nonnull String fileName, @Nonnull String contents) {
        addFile(fileName, this.outputFormat, contents);
    }

    /**
     * Adds a new file, alongside its {@code contents}, to this report output. The {@code outputFormat} parameter determines what the file extension
     * type will be.
     *
     * @param fileName file name, without extension type
     * @param outputFormat output format to get extension type from
     * @param contents contents of the file
     *
     * @see #addFileWithExactName(String, String)
     */
    public void addFile(@Nonnull String fileName, @Nonnull OutputFormat outputFormat, @Nonnull String contents) {
        Preconditions.checkNotNull(fileName, "fileName cannot be null.");
        Preconditions.checkNotNull(contents, "contents cannot be null.");
        Preconditions.checkNotNull(outputFormat, "outputFormat cannot be null.");
        checkFileName(fileName);

        fileName = fileName + "." + outputFormat.getExtensionType();
        this.files.put(fileName, contents);
    }

    public Map<String, String> getFiles() {
        return Collections.unmodifiableMap(files);
    }

    /**
     * Adds a new file, alongside its {@code contents}, to this report output. The {@code fullFileName} is the exact file name to be added with as
     * opposed to other insertion methods.
     *
     * @param fullFileName full file name, preferably with extension type
     * @param contents contents of the file
     */
    public void addFileWithExactName(@Nonnull String fullFileName, @Nonnull String contents) {
        Preconditions.checkNotNull(fullFileName, "fullFileName cannot be null.");
        Preconditions.checkNotNull(contents, "contents cannot be null.");
        checkFileName(fullFileName.replaceAll("." + outputFormat.getExtensionType(), "")); // remove the format extension.

        this.files.put(fullFileName, contents);
    }
    
    private void checkFileName(String fileName) {
        Preconditions.checkArgument(!fileName.equalsIgnoreCase(ReportFile.ROOT_FILE_NAME), "cannot override root file!");
    }
}
