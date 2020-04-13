package com.tome25.utils.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A PrintStream that writes everything to a Logger, everything written into it
 * after the last newline will be buffered until the next newline or flush.
 * 
 * @author ToMe25
 *
 */
public class LoggingPrintStream extends PrintStream {

	private final Logger log;
	private final Level lvl;
	private String buffer = "";
	private static String lineSeparator = System.lineSeparator();

	/**
	 * Creates a new LoggingPrintStream with the global logger and log level info.
	 */
	public LoggingPrintStream() {
		this(Logger.getGlobal());
	}

	/**
	 * Creates a new LoggingPrintStream with a custom logger and log level info.
	 * 
	 * @param logger
	 */
	public LoggingPrintStream(Logger logger) {
		this(logger, Level.INFO);
	}

	/**
	 * Creates a new LoggingPrintStream with a custom logger and log level.
	 * 
	 * @param logger
	 * @param logLevel
	 */
	public LoggingPrintStream(Logger logger, Level logLevel) {
		super((OutputStream) System.err);
		log = logger;
		lvl = logLevel;
	}

	/**
	 * returns the Logger this LoggingPrintStream writes to.
	 * 
	 * @return the Logger this LoggingPrintStream writes to.
	 */
	public Logger getLogger() {
		return log;
	}

	/**
	 * returns the log level this LoggingPrintStream writes to.
	 * 
	 * @return the log level this LoggingPrintStream writes to.
	 */
	public Level getLogLevel() {
		return lvl;
	}

	@Override
	public PrintStream append(char c) {
		print("" + c);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		print(csq.toString());
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		print(csq.subSequence(start, end).toString());
		return this;
	}

	@Override
	public boolean checkError() {
		return false;
	}

	@Override
	public void close() {
		flush();
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof LoggingPrintStream)) {
			LoggingPrintStream obj1 = (LoggingPrintStream) obj;
			return log.equals(obj1.log) && lvl.equals(obj1.lvl) && buffer.equals(obj1.buffer);
		}
		return false;
	}

	@Override
	public void flush() {
		if (!buffer.isEmpty()) {
			println();
		}
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		print(String.format(l, format, args));
		return this;
	}

	@Override
	public PrintStream format(String format, Object... args) {
		print(String.format(format, args));
		return this;
	}

	@Override
	public void print(boolean b) {
		print("" + b);
	}

	@Override
	public void print(char c) {
		print("" + c);
	}

	@Override
	public void print(char[] s) {
		print(new String(s));
	}

	@Override
	public void print(double d) {
		print("" + d);
	}

	@Override
	public void print(float f) {
		print("" + f);
	}

	@Override
	public void print(int i) {
		print("" + i);
	}

	@Override
	public void print(long l) {
		print("" + l);
	}

	@Override
	public void print(Object obj) {
		print(obj.toString());
	}

	@Override
	public void print(String s) {
		buffer += s;
		StackTraceElement writer = getWriter();
		while (buffer.contains(lineSeparator)) {
			log.logp(lvl, writer.getClassName(), writer.getMethodName(),
					buffer.substring(0, buffer.indexOf(lineSeparator)));
			buffer = buffer.substring(buffer.indexOf(lineSeparator) + 1);
		}
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		print(String.format(l, format, args));
		return this;
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		print(String.format(format, args));
		return this;
	}

	@Override
	public void println() {
		print(lineSeparator);
	}

	@Override
	public void println(boolean x) {
		print(x + lineSeparator);
	}

	@Override
	public void println(char x) {
		print(x + lineSeparator);
	}

	@Override
	public void println(char[] x) {
		print(new String(x) + lineSeparator);
	}

	@Override
	public void println(double x) {
		print(x + lineSeparator);
	}

	@Override
	public void println(float x) {
		print(x + lineSeparator);
	}

	@Override
	public void println(int x) {
		print(x + lineSeparator);
	}

	@Override
	public void println(long x) {
		print(x + lineSeparator);
	}

	@Override
	public void println(Object x) {
		print(x.toString() + lineSeparator);
	}

	@Override
	public void println(String x) {
		print(x + lineSeparator);
	}

	@Override
	public void write(byte[] b) throws IOException {
		print(new String(b));
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		print(new String(buf, off, len));
	}

	@Override
	public void write(int b) {
		print(b);
	}

	/**
	 * returns the StackTraceElement that contains the method writing the current
	 * message.
	 * 
	 * @return
	 */
	private StackTraceElement getWriter() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		int i = 3;
		StackTraceElement element = trace[i];
		while (arrayContains(LogTracer.SYSTEM_CLASSES, element.getClassName())) {
			i++;
			if (trace.length > i) {
				element = trace[i];
			} else {
				i--;
				break;
			}
		}
		return element;
	}

	/**
	 * Checks if the given array contains the give string.
	 * 
	 * @param array the array to search in.
	 * @param str   the string to search.
	 * @return
	 */
	private boolean arrayContains(String[] array, String str) {
		boolean contains = false;
		for (String s : array) {
			if (str.equals(s)) {
				contains = true;
				break;
			}
		}
		return contains;
	}

}
