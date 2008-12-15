package org.mozilla.javascript.tools.shell;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.SystemExitTool;
import de.unisb.cs.st.ds.util.SystemExitTool.SystemExitException;
import de.unisb.cs.st.javalanche.rhino.ClassLoaderUtil;
import de.unisb.cs.st.javalanche.rhino.RhinoTestRunnable;

/**
 * Wrappes the Main class of the shell package.
 *
 * @author David Schuler
 *
 */
public class WrappedMain {

	public static Logger logger = Logger.getLogger(WrappedMain.class);

	private static boolean useMain = false;
	private static boolean useClassLoader = false;

	/**
	 * We need this wrapper method because the original exec Method depends on
	 * static variables. Therefor this class (and some ohters) is loaded in an
	 * special class loader for each invocation. Otherwise a false exit code can
	 * be returned or the wrong files can be executed.
	 *
	 * @param args
	 *            the args for Main.exec
	 * @param out
	 * @param err
	 * @return the exitCode for this test.
	 */
	public static int wrappedExec(String[] args, PrintStream out,
			PrintStream err) {
		logger.info("Execute test for arguments: " + Arrays.toString(args));
		logger.info("Thread: " + Thread.currentThread());
		//displayJavaCommand(args);
		PrintStream errBack = System.err;
		PrintStream outBack = System.out;
//		System.setOut(out);
//		System.setErr(err);
		int exec = 0;
		if (useMain) {
			SystemExitTool.forbidSystemExitCall();
			if (useClassLoader) {
				try {
					ClassLoader classLoader = ClassLoaderUtil
							.getClassLoader(WrappedMain.class.getClassLoader());
					try {
						Class<?> loadClass = classLoader
								.loadClass("org.mozilla.javascript.tools.shell.MainLoader");
						Method[] methods = loadClass.getMethods();
						Method mainMethod = loadClass.getMethod("main",
								String[].class);
						logger.debug("Main method: " + mainMethod);
						Method exitCodeMethod = loadClass.getMethod(
								"getExitCode", null);
						logger.debug("Exit Code method " + exitCodeMethod);
						Object invoke = mainMethod.invoke(null,
								new Object[] { args });
						exec = (Integer) exitCodeMethod.invoke(null, null);

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				} catch (SystemExitException e) {
					logger.info("Test tried to call System.exit");
					exec = 3;
				} finally {

				}
			} else {
				Main.exitCode = 0;
				Main.main(args);
//				exec = Main.exec(args);
				exec = Main.exitCode;
			}

		} else {
			exec = Main.exec(args);
		}
		System.setOut(outBack);
		System.setErr(errBack);
		SystemExitTool.enableSystemExitCall();
		return exec;
	}

	public static void main(String[] args) {
		displayJavaCommand(args);
	}

	private static void displayJavaCommand(String[] args) {
		String message = RhinoTestRunnable.getCommand(args);
		logger.info(message);
	}

}
