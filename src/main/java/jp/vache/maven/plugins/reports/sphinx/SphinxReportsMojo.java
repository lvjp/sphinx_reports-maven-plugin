package jp.vache.maven.plugins.reports.sphinx;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * @author VERDOÏA Laurent <verdoialaurent@gmail.com>
 * @goal generate
 * @phase site
 */
public class SphinxReportsMojo extends AbstractMojo implements MavenReport {

    /**
     * Specifies the input encoding.
     *
     * @parameter default-value = "${project.build.sourceEncoding}"
     */
    private String inputEncoding;

    /**
     * Specifies the output encoding.
     *
     * @parameter default-value = "${project.build.outputEncoding}"
     */
    private String outputEncoding;


    /**
     * The name of the report.
     *
     * @parameter default-value="Sphinx"
     */
    private String name;

    /**
     * The description of the report.
     *
     * @parameter default-value="Documentation via sphinx"
     */
    private String description;

    /**
     * The directory containing the documentation source.
     *
     * @parameter default-value = "${basedir}/src/site/sphinx"
     */
    private File inputDirectory;

    /**
     * The directory where the generated output will be placed.
     *
     * @parameter default-value = "${project.reporting.outputDirectory}/sphinx"
     */
    private File reportOutputDirectory;

    /**
     * The name of the destination directory.
     *
     * @parameter default-value = "sphinx"
     */
    private String destinationDirectory;

    @Override
    public String getName(Locale locale) {
        return name;
    }

    @Override
    public String getOutputName() {
        return destinationDirectory + "/index";
    }

    @Override
    public String getDescription(Locale locale) {
        return description;
    }

    @Override
    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }

    @Override
    public void setReportOutputDirectory(File reportOutputDirectory) {
        this.reportOutputDirectory = reportOutputDirectory;
    }

    @Override
    public File getReportOutputDirectory() {
        return reportOutputDirectory;
    }

    public void setDestinationDirectory(final String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
    }

    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    @Override
    public boolean isExternalReport() {
        return true;
    }

    @Override
    public boolean canGenerateReport() {
        return true;
    }

    private void updateReportOutputDirectory(final File reportOutputDirectory, final String destinationDirectory) {
        if (reportOutputDirectory != null && destinationDirectory != null
                && !reportOutputDirectory.getAbsolutePath().endsWith(destinationDirectory)) {
            this.reportOutputDirectory = new File(reportOutputDirectory, destinationDirectory);
        } else {
            this.reportOutputDirectory = reportOutputDirectory;
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        generate();
    }

    @Override
    public void generate(org.codehaus.doxia.sink.Sink sink, Locale locale) throws MavenReportException {
        generate();
    }

    private void generate() {
        final Log log = getLog();

        final File outputFile = new File(reportOutputDirectory, getOutputName() + ".html");
        log.info("Output file: " + outputFile);

        Writer writer = null;
        try {
            writer = new FileWriter(outputFile);
            writer.write("The Sphinx plugin output");
            writer.flush();
        } catch (final IOException e) {
            throw new RuntimeException("IO Error", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (final IOException e) {
                    throw new RuntimeException("Cannot close file", e);
                }
            }
        }
    }
}
