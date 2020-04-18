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
 * get custom Loggers logging to the System Output.
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

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and a File Output Stream to the log file.
	 * 
	 * @param log the file to log the System Error Stream to.
	 */
	public static void traceOutputs(String log) {
		traceOutputs(new File(log));
	}

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and a File Output Stream to their respective file.
	 * 
	 * @param errorLog  the file to log the System Error Stream to.
	 * @param outputLog the file to log the System Output Stream to.
	 */
	public static void traceOutputs(String errorLog, String outputLog) {
		traceOutputs(new File(errorLog), new File(outputLog));
	}

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and a File Output Stream to the log file.
	 * 
	 * @param log the file to log the System Error Stream to.
	 */
	public static void traceOutputs(File log) {
		traceOutputs(log, log);
	}

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and a File Output Stream to their respective file.
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
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and the Output Streams from additionalStreams.
	 * 
	 * @param additionalStreams the Output Streams to add to System.out
	 */
	public static void traceOutputs(OutputStream... additionalStreams) {
		traceError(additionalStreams);
		traceOutput(additionalStreams);
	}

	/**
	 * Sets System Error Stream to a TracingMultiPrintStream with the old System
	 * Error Stream and the Output Streams from additionalStreams.
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
	 * Sets System Output to a TracingMultiPrintStream with the old System Output
	 * and the Output Streams from additionalStreams.
	 * 
	 * @param additionalStreams the Output Streams to add to System.out
	 */
	public static void traceOutput(OutputStream... additionalStreams) {
		Logger error = getOutput();
		for (OutputStream out : additionalStreams) {
			error.addHandler(new OutputHandler(out, new TracingFormatter()));
		}
		System.setOut(new LoggingPrintStream(error, Level.INFO));
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
	 * gets a Logger that logs log levels info and below to System.out, and warning
	 * and above to System.err, with the give custom name.
	 * 
	 * @param name the name of the logger to get.
	 * @return the logger for the given name.
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
	 * gets a Logger that logs log levels info and below to System.out, and warning
	 * and above to System.err, with the give custom name.
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
	 * gets the System Error Logger.
	 * 
	 * @return the system error logger.
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
	 * gets the System Output Logger.
	 * 
	 * @return the system output logger.
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

}
