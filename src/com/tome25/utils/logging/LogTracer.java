/*
 * ToMe25s-Java-Utilities is a collection of common java utilities.
 * Copyright (C) 2020-2021  ToMe25
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tome25.utils.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * A utility class to improve logging. Used to trace the system outputs, and to
 * get custom {@link Logger}s logging to the system output.
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
	 * Sets system output and error {@link PrintStream} to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} to the log file.
	 * 
	 * @param log the file to log the system error stream to.
	 */
	public static void traceOutputs(String log) {
		traceOutputs(new File(log));
	}

	/**
	 * Sets system output and error {@link PrintStream} to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} to their respective log
	 * file.
	 * 
	 * @param errorLog  the file to log the system error stream to.
	 * @param outputLog the file to log the system output stream to.
	 */
	public static void traceOutputs(String errorLog, String outputLog) {
		traceOutputs(new File(errorLog), new File(outputLog));
	}

	/**
	 * Sets system output and error {@link PrintStream} to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} to the log file.
	 * 
	 * @param log the file to log the system error stream to.
	 */
	public static void traceOutputs(File log) {
		traceOutputs(log, log);
	}

	/**
	 * Sets system output and error {@link PrintStream} to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} to their respective log
	 * file.
	 * 
	 * @param errorLog  the file to log the system error stream to.<br/>
	 *                  Set to null to disable changing {@link System#err}.
	 * @param outputLog the file to log the system output stream to.<br/>
	 *                  Set to null to disable changing {@link System#out}.
	 */
	public static void traceOutputs(File errorLog, File outputLog) {
		traceOutputs(null, errorLog, outputLog);
	}

	/**
	 * Sets system output and error {@link PrintStream} to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} to their respective log
	 * file.<br/>
	 * With a custom location for the {@link TracingFormatter} config file.
	 * 
	 * @param config    The config file to use for the
	 *                  {@link TracingFormatter}.<br/>
	 *                  If this is null a config file with a default name will be
	 *                  created in a directory called "config" in the directory the
	 *                  file that is currently being executed is stored in.<br/>
	 *                  If this is a directory a config file with a default name
	 *                  will be created in this directory.
	 * @param errorLog  the file to log the system error stream to.<br/>
	 *                  Set to null to disable changing {@link System#err}.
	 * @param outputLog the file to log the system output stream to.<br/>
	 *                  Set to null to disable changing {@link System#out}.
	 */
	public static void traceOutputs(File config, File errorLog, File outputLog) {
		try {
			traceError(config, errorLog);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			traceOutput(config, outputLog);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets system output and error {@link PrintStream} to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and the {@link OutputStream}s from additionalStreams.
	 * 
	 * @param additionalStreams the {@link OutputStream}s to add to System.out
	 */
	public static void traceOutputs(OutputStream... additionalStreams) {
		traceOutputs(null, additionalStreams);
	}

	/**
	 * Sets system output and error {@link PrintStream} to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and the {@link OutputStream}s from
	 * additionalStreams.<br/>
	 * With a custom location for the {@link TracingFormatter} config file.
	 * 
	 * @param config            The config file to use for the
	 *                          {@link TracingFormatter}.<br/>
	 *                          If this is null a config file with a default name
	 *                          will be created in a directory called "config" in
	 *                          the directory the file that is currently being
	 *                          executed is stored in.<br/>
	 *                          If this is a directory a config file with a default
	 *                          name will be created in this directory.
	 * @param additionalStreams the {@link OutputStream}s to add to
	 *                          {@link System#out} and {@link System#err}.
	 */
	public static void traceOutputs(File config, OutputStream... additionalStreams) {
		traceError(config, additionalStreams);
		traceOutput(config, additionalStreams);
	}

	/**
	 * Sets system error {@link PrintStream}({@link System#err}) to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} writing to the given log
	 * file.
	 * 
	 * @param log the log file to write the error output to.
	 * @throws FileNotFoundException if the log file exists but is a directory
	 *                               rather than a regular file, does not exist but
	 *                               cannot be created, or cannot be opened for any
	 *                               other reason.
	 */
	public static void traceError(File log) throws FileNotFoundException {
		traceError(null, log);
	}

	/**
	 * Sets system error {@link PrintStream}({@link System#err}) to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} writing to the given log
	 * file.<br/>
	 * With a custom location for the {@link TracingFormatter} config file.
	 * 
	 * @param log    the log file to write the error output to.
	 * @param config The config file to use for the {@link TracingFormatter}.<br/>
	 *               If this is null a config file with a default name will be
	 *               created in a directory called "config" in the directory the
	 *               file that is currently being executed is stored in.<br/>
	 *               If this is a directory a config file with a default name will
	 *               be created in this directory.
	 * @throws FileNotFoundException if the log file exists but is a directory
	 *                               rather than a regular file, does not exist but
	 *                               cannot be created, or cannot be opened for any
	 *                               other reason.
	 */
	public static void traceError(File config, File log) throws FileNotFoundException {
		Objects.requireNonNull(log, "Log file can't be null! (log == null)");

		log = log.getAbsoluteFile();
		if (log.isDirectory()) {
			throw new FileNotFoundException(
					String.format("File \"%s\" is a directory. The log file can't be a direcotry!", log.toString()));
		}

		if (!log.getParentFile().exists()) {
			log.getParentFile().mkdirs();
		}

		traceError(config, new FileOutputStream(log));
	}

	/**
	 * Sets system error {@link PrintStream} to a {@link LoggingPrintStream}
	 * printing to a {@link Logger} logging to the old {@link PrintStream} and the
	 * given {@link OutputStream}s.
	 * 
	 * @param additionalStreams the {@link OutputStream}s to add to
	 *                          {@link System#err}.
	 */
	public static void traceError(OutputStream... additionalStreams) {
		traceError(null, additionalStreams);
	}

	/**
	 * Sets system error {@link PrintStream} to a {@link LoggingPrintStream}
	 * printing to a {@link Logger} logging to the old {@link PrintStream} and the
	 * given {@link OutputStream}s.<br/>
	 * With a custom location for the {@link TracingFormatter} config file.
	 * 
	 * @param config            The config file to use for the
	 *                          {@link TracingFormatter}.<br/>
	 *                          If this is null a config file with a default name
	 *                          will be created in a directory called "config" in
	 *                          the directory the file that is currently being
	 *                          executed is stored in.<br/>
	 *                          If this is a directory a config file with a default
	 *                          name will be created in this directory.
	 * @param additionalStreams the {@link OutputStream}s to add to
	 *                          {@link System#err}.
	 */
	public static void traceError(File config, OutputStream... additionalStreams) {
		Logger error = getError();
		for (OutputStream out : additionalStreams) {
			error.addHandler(new OutputHandler(out, new TracingFormatter(config)));
		}
		System.setErr(new LoggingPrintStream(error, Level.WARNING));
	}

	/**
	 * Sets system output {@link PrintStream}({@link System#out}) to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} writing to the given log
	 * file.
	 * 
	 * @param log the log file to write the standard output to.
	 * @throws FileNotFoundException if the log file exists but is a directory
	 *                               rather than a regular file, does not exist but
	 *                               cannot be created, or cannot be opened for any
	 *                               other reason.
	 */
	public static void traceOutput(File log) throws FileNotFoundException {
		traceOutput(null, log);
	}

	/**
	 * Sets system output {@link PrintStream}({@link System#out}) to a
	 * {@link LoggingPrintStream} printing to a {@link Logger} logging to the old
	 * {@link PrintStream} and a {@link FileOutputStream} writing to the given log
	 * file.<br/>
	 * With a custom location for the {@link TracingFormatter} config file.
	 * 
	 * @param log    the log file to write the standard output to.
	 * @param config The config file to use for the {@link TracingFormatter}.<br/>
	 *               If this is null a config file with a default name will be
	 *               created in a directory called "config" in the directory the
	 *               file that is currently being executed is stored in.<br/>
	 *               If this is a directory a config file with a default name will
	 *               be created in this directory.
	 * @throws FileNotFoundException if the log file exists but is a directory
	 *                               rather than a regular file, does not exist but
	 *                               cannot be created, or cannot be opened for any
	 *                               other reason.
	 */
	public static void traceOutput(File config, File log) throws FileNotFoundException {
		Objects.requireNonNull(log, "Log file can't be null! (log == null)");

		log = log.getAbsoluteFile();
		if (log.isDirectory()) {
			throw new FileNotFoundException(
					String.format("File \"%s\" is a directory. The log file can't be a direcotry!", log.toString()));
		}

		if (!log.getParentFile().exists()) {
			log.getParentFile().mkdirs();
		}

		traceOutput(config, new FileOutputStream(log));
	}

	/**
	 * Sets system output {@link PrintStream} to a {@link LoggingPrintStream}
	 * printing to a {@link Logger} logging to the old {@link PrintStream} and the
	 * given {@link OutputStream}s.
	 * 
	 * @param additionalStreams the {@link OutputStream}s to add to
	 *                          {@link System#out}.
	 */
	public static void traceOutput(OutputStream... additionalStreams) {
		traceOutput(null, additionalStreams);
	}

	/**
	 * Sets system output {@link PrintStream} to a {@link LoggingPrintStream}
	 * printing to a {@link Logger} logging to the old {@link PrintStream} and the
	 * given {@link OutputStream}s.<br/>
	 * With a custom location for the {@link TracingFormatter} config file.
	 * 
	 * @param config            The config file to use for the
	 *                          {@link TracingFormatter}.<br/>
	 *                          If this is null a config file with a default name
	 *                          will be created in a directory called "config" in
	 *                          the directory the file that is currently being
	 *                          executed is stored in.<br/>
	 *                          If this is a directory a config file with a default
	 *                          name will be created in this directory.
	 * @param additionalStreams the {@link OutputStream}s to add to
	 *                          {@link System#out}.
	 */
	public static void traceOutput(File config, OutputStream... additionalStreams) {
		Logger output = getOutput();
		for (OutputStream out : additionalStreams) {
			output.addHandler(new OutputHandler(out, new TracingFormatter(config)));
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
	 * Gets a {@link Logger} that logs {@link java.util.logging.LogRecord LogRecord}
	 * with log {@link Level} info or below to System.out, and warning and above to
	 * System.err, with the give custom name.
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
	 * Gets a {@link Logger} that logs {@link java.util.logging.LogRecord LogRecord}
	 * with log {@link Level} info or below to System.out, and warning or above to
	 * System.err, with the give custom name.
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
	 * Gets the system error {@link Logger}.
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
	 * Gets the system output {@link Logger}.
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
	 * Gets the first {@link StackTraceElement} in the {@link Thread} calling this
	 * method from the given class.
	 * 
	 * @param callerName the name of the class to look for.
	 * @return the {@link StackTraceElement} of the searched class, or null, if
	 *         there was no {@link StackTraceElement} from that class.
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
		System.setOut(DEFAULT_OUT);
	}

}
