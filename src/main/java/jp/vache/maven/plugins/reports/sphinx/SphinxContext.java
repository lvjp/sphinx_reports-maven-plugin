package jp.vache.maven.plugins.reports.sphinx;

import org.python.core.PyList;
import org.python.core.PyString;

/**
 * @author Laurent Verdoïa <verdoialaurent@gmail.com>
 */
public class SphinxContext {
    boolean isVerbose;
    boolean showWarning;
    String builder;
    String input;
    String output;
    String sphinx;
    String argv0;

    PyList createArguments() {
        final PyList args = new PyList();

        args.add(new PyString(isVerbose ? "-v" : "-Q"));

        if (showWarning) {
            args.add("-W");
        }

        if (builder != null) {
            args.add(new PyString("-b"));
            args.add(new PyString(builder));
        }

        args.add(new PyString("-n"));
        args.add(new PyString(input));
        args.add(new PyString(output));

        return args;
    }
}
