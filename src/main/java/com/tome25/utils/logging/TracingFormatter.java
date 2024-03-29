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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.tome25.utils.config.Config;
import com.tome25.utils.lib.LibraryLoader;

/**
 * A custom {@link Formatter} for the tracing log style.
 * 
 * @author ToMe25
 *
 */
public class TracingFormatter extends Formatter {

	private static final Map<Integer, String> THREAD_NAMES = new HashMap<Integer, String>();

	/**
	 * Whether a time stamp of the creation of the message should be added to the
	 * output.
	 */
	private boolean traceTimestamp = true;

	/**
	 * Whether seconds should be a part of the time stamp added to the output.
	 */
	private boolean traceSeconds = true;

	/**
	 * Whether milliseconds should be a part of the time stamp added to the output.
	 */
	private boolean traceMilliSeconds = false;

	/**
	 * Whether the name of the thread printing the message should be added to the
	 * output.
	 */
	private boolean traceThread = true;

	/**
	 * Whether the name of the class printing the message should be added to the
	 * output.
	 */
	private boolean traceClass = true;

	/**
	 * Whether the traced class name should be the simple class name(without
	 * package).
	 */
	private boolean traceSimpleClassName = true;

	/**
	 * Whether the name of the method printing the message should be added to the
	 * output.
	 */
	private boolean traceMethod = true;
	private boolean traceLogger = true;
	private boolean traceLevel = true;

	/**
	 * Whether the line the message got caused in should be added to the output.
	 */
	private boolean traceLine = false;

	/**
	 * Whether the name of the file that caused the message should be added to the
	 * output.
	 */
	private boolean traceFile = false;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private Config cfg;

	/**
	 * Creates a new TracingFormatter with the default config file.
	 */
	public TracingFormatter() {
		this(null);
	}

	/**
	 * Creates a new TracingFormatter with a custom config file.
	 * 
	 * @param configFile the file to store the configuration in.<br>
	 *                   If this is null a config file with a default name will be
	 *                   created in a directory called "config" in the directory the
	 *                   file that is currently being executed is stored in.<br>
	 *                   If this is a directory a config file with a default name
	 *                   will be created in this directory.
	 */
	public TracingFormatter(File configFile) {
		super();
		if (configFile == null) {
			configFile = new File(LibraryLoader.getMainDir(), "config");
			configFile.mkdirs();
		}

		if (configFile.isDirectory()) {
			String caller = "";
			for (StackTraceElement trace : Thread.currentThread().getStackTrace()) {
				if (caller == "") {
					caller = trace.getClassName();
					continue;
				}
				caller = trace.getClassName();
				if (!caller.startsWith("com.tome25.utils.logging")) {
					break;
				}
			}
			configFile = new File(configFile, LogTracer.classNameToSimpleClassName(caller) + "TracingFormatter.cfg");
		}

		initConfig(configFile);
		new Timer(true).schedule(new TimerTask() {

			@Override
			public void run() {
				cfg.watch(cfg -> readConfig());
			}

		}, 100);
	}

	@Override
	public String format(LogRecord record) {
		String trace = getTrace(record);
		StringBuffer buffer = new StringBuffer(200);
		String message = formatMessage(record);
		buffer.append(traceMessage(message, trace));

		Throwable throwable = record.getThrown();
		if (throwable != null) {
			StringWriter sink = new StringWriter();
			try (PrintWriter printer = new PrintWriter(sink, true)) {
				throwable.printStackTrace(printer);
			}
			buffer.append(traceMessage(sink.toString(), trace));
		}
		return buffer.toString();
	}

	/**
	 * Adds the given trace block to the begining of every line in the given
	 * message.
	 * 
	 * @param message the message to add the trace block to.
	 * @param trace   the trace block.
	 * @return the traced message.
	 */
	private String traceMessage(String message, String trace) {
		if (message == null || message.isEmpty()) {
			return message;
		}
		StringBuffer buffer = new StringBuffer(200);
		for (String line : message.split(System.lineSeparator())) {
			if (line.isEmpty()) {
				buffer.append(System.lineSeparator());
			} else if (line.trim().isEmpty()) {
				buffer.append(line);
				buffer.append(System.lineSeparator());
			} else {
				buffer.append(trace);
				buffer.append(line);
				buffer.append(System.lineSeparator());
			}
		}
		return buffer.toString();
	}

	/**
	 * Gets the trace block for the begining of a new line.
	 * 
	 * @param record the {@link LogRecord} for which to get the trace.
	 * @return the trace block.
	 */
	private String getTrace(LogRecord record) {
		StringBuffer buffer = new StringBuffer(75);
		if (traceTimestamp) {
			buffer.append(String.format("[%s] ", dateFormat.format(new Date(record.getMillis()))));
		}
		if (traceThread) {
			buffer.append(String.format("[%s] ", getThreadNameForId(record.getThreadID())));
		}
		if (traceLogger || traceLevel) {
			String trace = "";
			if (traceLogger) {
				trace += record.getLoggerName();
			}
			if (traceLevel) {
				if (traceLogger) {
					trace += "/";
				}
				trace += record.getLevel().getName();
			}
			buffer.append(String.format("[%s] ", trace));
		}
		if (traceClass || traceMethod) {
			String trace = "";
			if (traceClass && traceSimpleClassName) {
				trace += LogTracer.classNameToSimpleClassName(record.getSourceClassName());
			} else if (traceClass) {
				trace += record.getSourceClassName();
			}
			if (traceMethod) {
				if (traceClass) {
					trace += '.';
				}
				trace += record.getSourceMethodName();
			}
			if (traceLine && !traceFile) {
				trace += ':';
				trace += LogTracer.getCallerStackTraceElement(record.getSourceClassName()).getLineNumber();
			}
			buffer.append(String.format("[%s] ", trace));
		}
		if (traceFile || (traceLine && !(traceClass || traceMethod))) {
			String trace = "";
			StackTraceElement traceElement = LogTracer.getCallerStackTraceElement(record.getSourceClassName());
			if (traceFile) {
				trace += traceElement.getFileName();
			}
			if (traceLine) {
				if (traceFile) {
					trace += ":";
				}
				trace += traceElement.getLineNumber();
			}
			buffer.append(String.format("[%s] ", trace));
		}
		if (buffer.length() > 0) {
			buffer.replace(buffer.length() - 1, buffer.length(), ": ");
		}
		return buffer.toString();
	}

	/**
	 * Returns the name of the {@link Thread} for the given id.
	 * 
	 * @param threadId the id of the {@link Thread} which name you want.
	 * @return the name of the {@link Thread} for the given id.
	 */
	private String getThreadNameForId(int threadId) {
		if (!THREAD_NAMES.containsKey(threadId)) {
			for (Thread thread : Thread.getAllStackTraces().keySet()) {
				if (thread.getId() == threadId) {
					THREAD_NAMES.put(threadId, thread.getName());
				}
			}
		}
		return THREAD_NAMES.get(threadId);
	}

	/**
	 * Initializes the config object and reads its values from the config file.
	 * 
	 * @param cfgFile the config file to read.
	 */
	private void initConfig(File cfgFile) {
		cfg = new Config(false, cfgFile.getParentFile(), false);

		cfg.addConfig(cfgFile, "traceTimestamp", traceTimestamp,
				"Whether the beginning of every line of output should contain a timestamp.");
		cfg.addConfig(cfgFile, "traceSeconds", traceSeconds,
				"Wehther the beginning of every line of output should contain the second it was written in"
						+ " as part of the timestamp.");
		cfg.addConfig(cfgFile, "traceMilliSeconds", traceMilliSeconds,
				"Wehther the beginning of every line of output should contain the millisecond it was written in"
						+ " as part of the timestamp.");
		cfg.addConfig(cfgFile, "traceThread", traceThread,
				"Whether the beginning of every line of output should contain the name of the Thread writing it.");
		cfg.addConfig(cfgFile, "traceOutputtingClass", traceClass,
				"Whether the beginning of every line of output should contain the name of the class writing it.");
		cfg.addConfig(cfgFile, "traceSimpleClassName", traceSimpleClassName,
				"Whether the beginning of every line of output should contain simple class names(without package) "
						+ "or full class names(with package).");
		cfg.addConfig(cfgFile, "traceOutputtingMethod", traceMethod,
				"Whether the beginning of every line of output should contain the name of the method writing it.");
		cfg.addConfig(cfgFile, "traceLogger", traceLogger,
				"Whether the beginning of every line of output should contain the name of the logger writing it.");
		cfg.addConfig(cfgFile, "traceLevel", traceLevel,
				"Whether the beginning of every line of output should contain the log level its written with.");
		cfg.addConfig(cfgFile, "traceLineNumber", traceLine,
				"Whether the beginning of every line of output should contain the number of the line writing it.");
		cfg.addConfig(cfgFile, "traceFileName", traceFile,
				"Whether the beginning of every line of output should contain the name of the file writing it.");

		cfg.readConfig();
		readConfig();
	}

	/**
	 * Reads the values from the given config file.
	 */
	private void readConfig() {
		traceTimestamp = (boolean) cfg.getConfig("traceTimestamp");
		traceSeconds = (boolean) cfg.getConfig("traceSeconds");
		traceMilliSeconds = (boolean) cfg.getConfig("traceMilliSeconds");
		traceThread = (boolean) cfg.getConfig("traceThread");
		traceClass = (boolean) cfg.getConfig("traceOutputtingClass");
		traceSimpleClassName = (boolean) cfg.getConfig("traceSimpleClassName");
		traceMethod = (boolean) cfg.getConfig("traceOutputtingMethod");
		traceLogger = (boolean) cfg.getConfig("traceLogger");
		traceLevel = (boolean) cfg.getConfig("traceLevel");
		traceLine = (boolean) cfg.getConfig("traceLineNumber");
		traceFile = (boolean) cfg.getConfig("traceFileName");

		String timestampPattern = "HH:mm";
		if (traceSeconds) {
			timestampPattern += ":ss";
		}
		if (traceMilliSeconds) {
			timestampPattern += ".SS";
		}
		dateFormat = new SimpleDateFormat(timestampPattern);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cfg == null) ? 0 : cfg.hashCode());
		result = prime * result + (traceClass ? 1231 : 1237);
		result = prime * result + (traceFile ? 1231 : 1237);
		result = prime * result + (traceLevel ? 1231 : 1237);
		result = prime * result + (traceLine ? 1231 : 1237);
		result = prime * result + (traceLogger ? 1231 : 1237);
		result = prime * result + (traceMethod ? 1231 : 1237);
		result = prime * result + (traceSimpleClassName ? 1231 : 1237);
		result = prime * result + (traceThread ? 1231 : 1237);
		result = prime * result + (traceTimestamp ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TracingFormatter other = (TracingFormatter) obj;
		if (cfg == null) {
			if (other.cfg != null) {
				return false;
			}
		} else if (!cfg.equals(other.cfg)) {
			return false;
		}
		if (traceClass != other.traceClass) {
			return false;
		}
		if (traceFile != other.traceFile) {
			return false;
		}
		if (traceLevel != other.traceLevel) {
			return false;
		}
		if (traceLine != other.traceLine) {
			return false;
		}
		if (traceLogger != other.traceLogger) {
			return false;
		}
		if (traceMethod != other.traceMethod) {
			return false;
		}
		if (traceSimpleClassName != other.traceSimpleClassName) {
			return false;
		}
		if (traceThread != other.traceThread) {
			return false;
		}
		if (traceTimestamp != other.traceTimestamp) {
			return false;
		}
		return true;
	}

}
