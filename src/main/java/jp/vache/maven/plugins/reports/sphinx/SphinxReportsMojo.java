package jp.vache.maven.plugins.reports.sphinx;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

import javax.script.ScriptException;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author VERDOÏA Laurent <verdoialaurent@gmail.com>
 */
@Mojo(name = "generate")
@Execute(goal = "generate")
public class SphinxReportsMojo extends AbstractMojo implements MavenReport {

    /**
     * Specifies the input encoding.
     */
    @Parameter(name = "inputEncoding", defaultValue = "${project.build.sourceEncoding}")
    private String inputEncoding;

    /**
     * Specifies the output encoding.
     */
    @Parameter(name = "outputEncoding", defaultValue = "${project.build.outputEncoding}")
    private String outputEncoding;


    /**
     * The name of the report.
     */
    @Parameter(name = "name", defaultValue = "Sphinx")
    private String name;

    /**
     * The description of the report.
     */
    @Parameter(name = "description", defaultValue = "Documentation via sphinx")
    private String description;

    /**
     * The directory containing the documentation source.
     */
    @Parameter(name = "inputDirectory", defaultValue = "${basedir}/src/site/sphinx")
    private File inputDirectory;

    /**
     * The directory where the generated output will be placed.
     */
    @Parameter(name = "reportOutputDirectory", defaultValue = "${project.reporting.outputDirectory}/sphinx")
    private File reportOutputDirectory;

    /**
     * The name of the destination directory.
     */
    @Parameter(name = "destinationDirectory", defaultValue = "sphinx")
    private String destinationDirectory;

    /**
     * Sphinx source code directory.
     */
    @Parameter(name = "sphinxSources", required = true, property = "sphinxSources")
    private File sphinxSources;

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
        updateReportOutputDirectory(reportOutputDirectory, destinationDirectory);
    }

    @Override
    public File getReportOutputDirectory() {
        return reportOutputDirectory;
    }

    public void setDestinationDirectory(final String destinationDirectory) {
        updateReportOutputDirectory(reportOutputDirectory, destinationDirectory);
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
        try {
            generate();
        } catch (final MavenReportException e) {
            throw new MojoFailureException("Cannot generate report", e);
        }
    }

    @Override
    public void generate(org.codehaus.doxia.sink.Sink sink, Locale locale) throws MavenReportException {
        generate();
    }

    private void generate() throws MavenReportException {
        printParameters();

        final SphinxContext context = createContext();

        try {
            SphinxRunner.run(context);
        } catch (final ScriptException e) {
            throw new MavenReportException("Sphinx error", e);
        }
    }

    private SphinxContext createContext() {
        final SphinxContext context = new SphinxContext();

        context.builder = "html";

        context.isVerbose = true;
        context.showWarning = true;

        context.input = inputDirectory.getAbsolutePath().toString();
        context.output = reportOutputDirectory.getAbsolutePath().toString();
        context.sphinx = sphinxSources.toString();
        context.argv0 = new File(sphinxSources, "sphinx-builder").getAbsolutePath().toString();

        return context;
    }

    private void printParameters() {
        final Log log = getLog();

        if (!log.isDebugEnabled()) {
            return;
        }

        final Map<String, String> data = new HashMap<String, String>();
        data.put("inputEncoding", inputEncoding);
        data.put("outputEncoding", outputEncoding);
        data.put("name", name);
        data.put("description", description);
        data.put("inputDirectory", inputDirectory.toString());
        data.put("reportOutputDirectory", reportOutputDirectory.toString());
        data.put("destinationDirectory", destinationDirectory);
        data.put("getOutputName()", getOutputName());
        data.put("sphinxSources", sphinxSources.toString());

        for (final Map.Entry<String, String> pair : data.entrySet()) {
            log.info(pair.getKey() + " => " + pair.getValue());
        }
    }
}
