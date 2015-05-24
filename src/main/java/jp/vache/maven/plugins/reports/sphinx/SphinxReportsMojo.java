package jp.vache.maven.plugins.reports.sphinx;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;

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
        generate();
    }

    @Override
    public void generate(org.codehaus.doxia.sink.Sink sink, Locale locale) throws MavenReportException {
        generate();
    }

    private void generate() {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("python");

        final ScriptContext context = engine.getContext();
        context.setWriter(new PrintWriter(System.out));
        context.setErrorWriter(new PrintWriter(System.err));

        try {
            engine.eval("import os");
            engine.eval("import sys");
            engine.eval("print 'Python OS: ' + os.name");
            engine.eval("print 'Python version: ' + sys.version");
        } catch (final ScriptException e) {
            throw new RuntimeException("Cannot run python script", e);
        }
    }
}
