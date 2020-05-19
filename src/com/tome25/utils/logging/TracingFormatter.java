package com.tome25.utils.logging;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.tome25.utils.config.Config;

/**
 * A custom formatter for the tracing log style.
 * 
 * @author ToMe25
 *
 */
public class TracingFormatter extends Formatter {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final Map<Integer, String> THREAD_NAMES = new HashMap<Integer, String>();

	/**
	 * Whether a timestamp of the creation of the message should be added to the
	 * output.
	 */
	private boolean traceTimestamp = true;
	/**
	 * Whether the name of the thread printing the message should be added to the
	 * output.
	 */
	private boolean traceThread = true;
	private boolean traceClass = true;
	/**
	 * Whether the traced class name should be the simple class name(without
	 * package).
	 */
	private boolean traceSimpleClassName = true;
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
	private Config cfg;

	public TracingFormatter() {
		this(new File(
				new File(TracingMultiPrintStream.class.getProtectionDomain().getCodeSource().getLocation().getPath())
						.getParent(),
				LogTracer.classNameToSimpleClassName(Thread.currentThread().getStackTrace()[2].getClassName())
						+ "TracingFormatter.cfg"));
	}

	public TracingFormatter(File configFile) {
		super();
		cfg = new Config(configFile.getParentFile(), false);
		readConfig(configFile);
	}

	@Override
	public String format(LogRecord record) {
		StringBuffer buf = new StringBuffer(180);
		if (traceTimestamp) {
			buf.append(String.format("[%s] ", DATE_FORMAT.format(new Date(record.getMillis()))));
		}
		if (traceThread) {
			buf.append(String.format("[%s] ", getThreadNameForId(record.getThreadID())));
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
			buf.append(String.format("[%s] ", trace));
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
					trace += ".";
				}
				trace += record.getSourceMethodName();
			}
			buf.append(String.format("[%s] ", trace));
		}
		if (traceFile || traceLine) {
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
			buf.append(String.format("[%s] ", trace));
		}
		if (buf.length() > 0) {
			buf.replace(buf.length() - 1, buf.length(), ": ");
		}
		buf.append(formatMessage(record));
		Throwable throwable = record.getThrown();
		if (throwable != null) {
			StringWriter sink = new StringWriter();
			throwable.printStackTrace(new PrintWriter(sink, true));
			buf.append(sink.toString());
		}
		buf.append(System.lineSeparator());
		return buf.toString();
	}

	/**
	 * returns the name of the Thread for the given id.
	 * 
	 * @param threadId the id of the thread which name you want.
	 * @return the name of the Thread for the given id.
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
	 * reads the values from the given config file.
	 * 
	 * @param cfgFile the config file to read.
	 */
	private void readConfig(File cfgFile) {
		// init config
		cfg.addConfig(cfgFile, "traceTimestamp", traceTimestamp,
				"Whether the beginning of every line of output should contain a timestamp.");
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
		// read config
		cfg.readConfig();
		traceTimestamp = (boolean) cfg.getConfig("traceTimestamp");
		traceThread = (boolean) cfg.getConfig("traceThread");
		traceClass = (boolean) cfg.getConfig("traceOutputtingClass");
		traceSimpleClassName = (boolean) cfg.getConfig("traceSimpleClassName");
		traceMethod = (boolean) cfg.getConfig("traceOutputtingMethod");
		traceLogger = (boolean) cfg.getConfig("traceLogger");
		traceLevel = (boolean) cfg.getConfig("traceLevel");
		traceLine = (boolean) cfg.getConfig("traceLineNumber");
		traceFile = (boolean) cfg.getConfig("traceFileName");
	}

}
