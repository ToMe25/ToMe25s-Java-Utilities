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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link PrintStream} that writes everything to a {@link Logger}. Everything
 * written into it after the last newline will be buffered until the next
 * newline or flush.
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
	 * Creates a new LoggingPrintStream with the global {@link Logger} and log {@link Level} info.
	 */
	public LoggingPrintStream() {
		this(Logger.getGlobal());
	}

	/**
	 * Creates a new LoggingPrintStream with a custom {@link Logger} and log
	 * {@link Level} info.
	 * 
	 * @param logger the {@link Logger} to print the input to.
	 */
	public LoggingPrintStream(Logger logger) {
		this(logger, Level.INFO);
	}

	/**
	 * Creates a new LoggingPrintStream with a custom {@link Logger} and log
	 * {@link Level}.
	 * 
	 * @param logger   the {@link Logger} to print the input to.
	 * @param logLevel the log {@link Level} to use when printing the input to
	 *                 {@link Logger}.
	 */
	public LoggingPrintStream(Logger logger, Level logLevel) {
		super((OutputStream) System.err);
		log = logger;
		lvl = logLevel;
	}

	/**
	 * Returns the {@link Logger} this LoggingPrintStream writes to.
	 * 
	 * @return the {@link Logger} this LoggingPrintStream writes to.
	 */
	public Logger getLogger() {
		return log;
	}

	/**
	 * Returns the log {@link Level} this LoggingPrintStream writes to.
	 * 
	 * @return the log {@link Level} this LoggingPrintStream writes to.
	 */
	public Level getLogLevel() {
		return lvl;
	}

	@Override
	public PrintStream append(char c) {
		print(String.valueOf(c));
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		print(String.valueOf(csq));
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		print(csq == null ? "null" : csq.subSequence(start, end).toString());
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buffer == null) ? 0 : buffer.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		result = prime * result + ((lvl == null) ? 0 : lvl.hashCode());
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
		LoggingPrintStream other = (LoggingPrintStream) obj;
		if (buffer == null) {
			if (other.buffer != null) {
				return false;
			}
		} else if (!buffer.equals(other.buffer)) {
			return false;
		}
		if (log == null) {
			if (other.log != null) {
				return false;
			}
		} else if (!log.equals(other.log)) {
			return false;
		}
		if (lvl == null) {
			if (other.lvl != null) {
				return false;
			}
		} else if (!lvl.equals(other.lvl)) {
			return false;
		}
		return true;
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
		print(String.valueOf(b));
	}

	@Override
	public void print(char c) {
		print(String.valueOf(c));
	}

	@Override
	public void print(char[] s) {
		print(new String(s));
	}

	@Override
	public void print(double d) {
		print(String.valueOf(d));
	}

	@Override
	public void print(float f) {
		print(String.valueOf(f));
	}

	@Override
	public void print(int i) {
		print(String.valueOf(i));
	}

	@Override
	public void print(long l) {
		print(String.valueOf(l));
	}

	@Override
	public void print(Object obj) {
		print(String.valueOf(obj));
	}

	@Override
	public void print(String s) {
		buffer += s;
		StackTraceElement writer = getWriter();
		while (buffer.contains(lineSeparator)) {
			log.logp(lvl, writer.getClassName(), writer.getMethodName(),
					buffer.substring(0, buffer.indexOf(lineSeparator)));
			buffer = buffer.substring(buffer.indexOf(lineSeparator) + lineSeparator.length());
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
		print(x + lineSeparator);
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
	 * Returns the {@link StackTraceElement} that contains the method writing the
	 * current message.
	 * 
	 * @return the {@link StackTraceElement} that contains the method writing the
	 *         current message.
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
	 * Checks whether the given array contains the given string.
	 * 
	 * @param array the array to search in.
	 * @param str   the string to search.
	 * @return whether the give array contains the given string.
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
