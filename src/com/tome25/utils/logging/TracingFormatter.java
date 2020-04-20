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
	private boolean traceThread = true;
	private boolean traceOutputtingClass = true;
	/**
	 * Whether the traced class name should be the simple class name(without
	 * package).
	 */
	private boolean traceSimpleClassName = true;
	private boolean traceOutputtingMethod = true;
	private boolean traceLogger = true;
	private boolean traceLevel = true;
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
		if (traceLogger && traceLevel) {
			buf.append(String.format("[%s/%s] ", record.getLoggerName(), record.getLevel().getName()));
		} else if (traceLogger) {
			buf.append(String.format("[%s] ", record.getLoggerName()));
		} else if (traceLevel) {
			buf.append(String.format("[%s] ", record.getLevel().getName()));
		}
		if (traceOutputtingClass && traceSimpleClassName) {
			buf.append(String.format("[%s] ", LogTracer.classNameToSimpleClassName(record.getSourceClassName())));
		} else if (traceOutputtingClass) {
			buf.append(String.format("[%s] ", record.getSourceClassName()));
		}
		if (traceOutputtingMethod) {
			buf.append(String.format("[%s] ", record.getSourceMethodName()));
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
		cfg.addConfig(cfgFile, "traceOutputtingClass", traceOutputtingClass,
				"Whether the beginning of every line of output should contain the name of the class writing it.");
		cfg.addConfig(cfgFile, "traceSimpleClassName", traceSimpleClassName,
				"Whether the beginning of every line of output should contain simple class names(without package) "
						+ "or full class names(with package).");
		cfg.addConfig(cfgFile, "traceOutputtingMethod", traceOutputtingMethod,
				"Whether the beginning of every line of output should contain the name of the method writing it.");
		cfg.addConfig(cfgFile, "traceLogger", traceLogger,
				"Whether the beginning of every line of output should contain the name of the logger writing it.");
		cfg.addConfig(cfgFile, "traceLevel", traceLevel,
				"Whether the beginning of every line of output should contain the log level its written with.");
		// read config
		cfg.readConfig();
		traceTimestamp = (boolean) cfg.getConfig("traceTimestamp");
		traceThread = (boolean) cfg.getConfig("traceThread");
		traceOutputtingClass = (boolean) cfg.getConfig("traceOutputtingClass");
		traceSimpleClassName = (boolean) cfg.getConfig("traceSimpleClassName");
		traceOutputtingMethod = (boolean) cfg.getConfig("traceOutputtingMethod");
		traceLogger = (boolean) cfg.getConfig("traceLogger");
		traceLevel = (boolean) cfg.getConfig("traceLevel");
	}

}
