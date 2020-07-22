package com.tome25.utils.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * A utility class to improve logging. Used to trace the System Outputs, and to
 * get custom {@link Logger}s logging to the System Output.
 * 
 * @author ToMe25
 *
 */
public class LogTracer {

	protected static final String[] SYSTEM_CLASSES = { PrintStream.class.getName(), Throwable.class.getName(),
			Formatter.class.getName(), TracingMultiPrintStream.class.getName(), ThreadGroup.class.getName(),
			Thread.class.getName(), LoggingPrintStream.class.getName() };

	private static Logger global;
	private static Logger error;
	private static Logger output;
	private static final PrintStream DEFAULT_ERR = System.err;
	private static final PrintStream DEFAULT_OUT = System.out;

	/**
	 * Sets System Output and Error Stream to a {@link LoggingPrintStream} printing
	 * to a {@link Logger} logging to the old Stream and a {@link FileOutputStream}
	 * to the log file.
	 * 
	 * @param log the file to log the System Error Stream to.
	 */
	public static void traceOutputs(String log) {
		traceOutputs(new File(log));
	}

	/**
	 * Sets System Output and Error Stream to a {@link LoggingPrintStream} printing
	 * to a {@link Logger} logging to the old Stream and a {@link FileOutputStream}
	 * to the log file.
	 * 
	 * @param errorLog  the file to log the System Error Stream to.
	 * @param outputLog the file to log the System Output Stream to.
	 */
	public static void traceOutputs(String errorLog, String outputLog) {
		traceOutputs(new File(errorLog), new File(outputLog));
	}

	/**
	 * Sets System Output and Error Stream to a {@link LoggingPrintStream} printing
	 * to a {@link Logger} logging to the old Stream and a {@link FileOutputStream}
	 * to the log file.
	 * 
	 * @param log the file to log the System Error Stream to.
	 */
	public static void traceOutputs(File log) {
		traceOutputs(log, log);
	}

	/**
	 * Sets System Output and Error Stream to a {@link LoggingPrintStream} printing
	 * to a {@link Logger} logging to the old Stream and a {@link FileOutputStream}
	 * to their respective file.
	 * 
	 * @param errorLog  the file to log the System Error Stream to. set to null to
	 *                  disable changing System.err.
	 * @param outputLog the file to log the System Output Stream to. set to null to
	 *                  disable changing System.out.
	 */
	public static void traceOutputs(File errorLog, File outputLog) {
		if (errorLog != null) {
			errorLog = errorLog.getAbsoluteFile();
			outputLog = outputLog.getAbsoluteFile();
			if (!errorLog.getParentFile().exists()) {
				errorLog.getParentFile().mkdirs();
			}
			try {
				traceError(new FileOutputStream(errorLog));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (outputLog != null) {
			if (!outputLog.getParentFile().exists()) {
				outputLog.getParentFile().mkdirs();
			}
			try {
				traceOutput(new FileOutputStream(outputLog));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets System Output and Error Stream to a {@link LoggingPrintStream} printing
	 * to a {@link Logger} logging to the old Stream and the {@link OutputStream}s
	 * from additionalStreams.
	 * 
	 * @param additionalStreams the {@link OutputStream}s to add to System.out
	 */
	public static void traceOutputs(OutputStream... additionalStreams) {
		traceError(additionalStreams);
		traceOutput(additionalStreams);
	}

	/**
	 * Sets System Error Stream to a {@link LoggingPrintStream} printing to a
	 * {@link Logger} logging to the old Stream and the {@link OutputStream}s from
	 * additionalStreams.
	 * 
	 * @param additionalStreams the Output Streams to add to System.err
	 */
	public static void traceError(OutputStream... additionalStreams) {
		Logger error = getError();
		for (OutputStream out : additionalStreams) {
			error.addHandler(new OutputHandler(out, new TracingFormatter()));
		}
		System.setErr(new LoggingPrintStream(error, Level.WARNING));
	}

	/**
	 * Sets System Output to a {@link LoggingPrintStream} printing to a
	 * {@link Logger} logging to the old Stream and the {@link OutputStream}s from
	 * additionalStreams.
	 * 
	 * @param additionalStreams the Output Streams to add to System.out
	 */
	public static void traceOutput(OutputStream... additionalStreams) {
		Logger output = getOutput();
		for (OutputStream out : additionalStreams) {
			output.addHandler(new OutputHandler(out, new TracingFormatter()));
		}
		System.setOut(new LoggingPrintStream(output, Level.INFO));
	}

	/**
	 * Converts the class name with package to a simple class name without.
	 * 
	 * @param className the class name to convert
	 * @return the simple class name for the given class name.
	 */
	public static String classNameToSimpleClassName(String className) {
		return className.contains(".") ? className.substring(className.lastIndexOf('.') + 1)
				: className.substring(className.lastIndexOf('/') + 1);
	}

	/**
	 * Gets a {@link Logger} that logs records with log {@link Level} info or below to System.out, and warning
	 * and above to System.err, with the give custom name.
	 * 
	 * @param name the name of the {@link Logger} to get.
	 * @return the {@link Logger} for the given name.
	 */
	public static Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);
		boolean addHandler = true;
		for (Handler handler : logger.getHandlers()) {
			if (handler.getFormatter() instanceof TracingFormatter) {
				addHandler = false;
				break;
			}
		}
		if (addHandler) {
			logger.addHandler(new SplittingHandler());
		}
		return logger;
	}

	/**
	 * Gets a {@link Logger} that logs records with log {@link Level} info or below to System.out, and warning
	 * or above to System.err, with the give custom name.
	 * 
	 * @return the global logger.
	 */
	public static Logger getGlobal() {
		if (global != null) {
			return global;
		} else {
			Logger logger = Logger.getGlobal();
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.ALL);
			boolean addHandler = true;
			for (Handler handler : logger.getHandlers()) {
				if (handler.getFormatter() instanceof TracingFormatter) {
					addHandler = false;
					break;
				}
			}
			if (addHandler) {
				logger.addHandler(new SplittingHandler());
			}
			global = logger;
			return logger;
		}
	}

	/**
	 * Gets the System Error {@link Logger}.
	 * 
	 * @return the system error {@link Logger}.
	 */
	public static Logger getError() {
		if (error != null) {
			return error;
		} else {
			Logger logger = Logger.getLogger("SYSERR");
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.ALL);
			boolean addHandler = true;
			for (Handler handler : logger.getHandlers()) {
				if (handler.getFormatter() instanceof TracingFormatter) {
					addHandler = false;
					break;
				}
			}
			if (addHandler) {
				logger.addHandler(new OutputHandler(System.err, Level.ALL, new TracingFormatter()));
			}
			error = logger;
			return logger;
		}
	}

	/**
	 * Gets the System Output {@link Logger}.
	 * 
	 * @return the system output {@link Logger}.
	 */
	public static Logger getOutput() {
		if (output != null) {
			return output;
		} else {
			Logger logger = Logger.getLogger("SYSOUT");
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.ALL);
			boolean addHandler = true;
			for (Handler handler : logger.getHandlers()) {
				if (handler.getFormatter() instanceof TracingFormatter) {
					addHandler = false;
					break;
				}
			}
			if (addHandler) {
				logger.addHandler(new OutputHandler(System.out, Level.ALL, new TracingFormatter()));
			}
			output = logger;
			return logger;
		}
	}

	/**
	 * Gets the first {@link StackTraceElement} in the {@link Thread} calling this method from the
	 * given class.
	 * 
	 * @param callerName the name of the class to look for.
	 * @return the {@link StackTraceElement} of the searched class, or null, if there was no
	 *         {@link StackTraceElement} from that class.
	 */
	public static StackTraceElement getCallerStackTraceElement(final String callerName) {
		StackTraceElement callerFrame = null;
		final StackTraceElement stack[] = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			final StackTraceElement frame = stack[i];
			if (callerName.equals(frame.getClassName())) {
				callerFrame = frame;
				break;
			}
		}
		return callerFrame;
	}

	/**
	 * Resets the system error output to its value from before this class changed
	 * it. And maybe before other classes changed it.
	 */
	public static void resetErr() {
		System.setErr(DEFAULT_ERR);
	}

	/**
	 * Resets the system output to its value from before this class changed it. And
	 * maybe before other classes changed it.
	 */
	public static void resetOut() {
		System.setErr(DEFAULT_OUT);
	}

}
