package jp.vache.maven.plugins.reports.sphinx;

import org.python.core.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Laurent Verdoïa <verdoialaurent@gmail.com>
 */
public class SphinxRunner {

    public static int run(final SphinxContext context) throws ScriptException {
        final PySystemState engineSys = new PySystemState();
        engineSys.path.add(Py.newString(context.sphinx));
        Py.setSystemState(engineSys);

        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("python");

        engine.put("args", context.createArguments());

        engine.eval("from sphinx import main");
        try {
            engine.eval("main(args)");
        } catch (ScriptException e) {
            final Integer result = processSystemExitException(e);
            if (result == null) {
                throw e;
            }

            return result;
        }

        return 0;
    }

    private static Integer processSystemExitException(final ScriptException e) {
        final Throwable cause = e.getCause();
        if (!(cause instanceof PyException)) {
            return null;
        }

        final PyException pyException = (PyException) cause;
        if (!(pyException.value instanceof PyBaseExceptionDerived)) {
            return null;
        }

        final PyBaseExceptionDerived pyBaseExceptionDerived = (PyBaseExceptionDerived) pyException.value;
        if (!pyBaseExceptionDerived.getType().getName().equals("SystemExit")) {
            return null;
        }

        final PyObject slot;
        try {
            slot = pyBaseExceptionDerived.getSlot(0);
        } catch (final ArrayIndexOutOfBoundsException e1) {
            return null;
        }

        if (!(slot instanceof PyInteger)) {
            return null;
        }

        final PyInteger pyInteger = (PyInteger) slot;

        return pyInteger.asInt();
    }
}
