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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.tome25.utils.config.Config;
import com.tome25.utils.lib.LibraryLoader;

/**
 * 
 * A Tracing {@link PrintStream} that can print to multiple
 * {@link OutputStream}s.
 * 
 * @author ToMe25
 *
 */
public class TracingMultiPrintStream extends MultiPrintStream {

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
	/**
	 * Whether the name of the class printing the message should be added to the
	 * output.
	 */
	private boolean traceOutputtingClass = true;
	/**
	 * Whether system classes should be used as the outputting class for some text.
	 */
	private boolean traceSystemClasses = true;
	/**
	 * Whether the traced class name should be the simple class name(without
	 * package).
	 */
	private boolean traceSimpleClassName = true;
	private boolean traceOutputtingMethod = true;
	private boolean traceSystemClassMethods = false;
	/**
	 * Whether the line the message got caused in should be added to the output.
	 */
	private boolean traceLineNumber = false;
	private int traceStartDepth = 4;
	private boolean endLineSeperator = true;
	private Config cfg;

	private static final String LINE_SEPARATOR = System.lineSeparator();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

	/**
	 * Creates a new TracingMultiPrintStream printing to the given
	 * {@link OutputStream}s.
	 * 
	 * @param outs the {@link OutputStream}s to print to.
	 */
	public TracingMultiPrintStream(OutputStream... outs) {
		this(null, outs);
	}

	/**
	 * Creates a new TracingMultiPrintStream printing to the given
	 * {@link OutputStream}s and a custom config file.
	 * 
	 * @param configFile the file to store the configuration in.<br>
	 *                   If this is null a config file with a default name will be
	 *                   created in a directory called "config" in the directory the
	 *                   file that is currently being executed is stored in.<br>
	 *                   If this is a directory a config file with a default name
	 *                   will be created in this directory.
	 * @param outs       the {@link OutputStream}s to print to.
	 */
	public TracingMultiPrintStream(File configFile, OutputStream... outs) {
		super(outs);
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
				if (caller != TracingMultiPrintStream.class.getName()) {
					break;
				}
			}
			configFile = new File(configFile,
					LogTracer.classNameToSimpleClassName(caller) + "TracingMultiPrintStream.cfg");
		}

		initConfig(configFile);
		new Timer(true).schedule(new TimerTask() {

			@Override
			public void run() {
				cfg.watch(cfg -> readConfig());
			}

		}, 200);
	}

	@Override
	public PrintStream append(char c) {
		if (endLineSeperator) {
			try {
				super.write(FinishBArr("", false));
			} catch (IOException e) {
			}
		}
		return super.append(c);
	}

	@Override
	public PrintStream append(CharSequence csq) {
		return super.append(FinishStr(csq, false));
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		return super.append(FinishStr(csq.subSequence(start, end), false));
	}

	@Override
	public void println(String x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println(Object x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println(char[] x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println(boolean x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println(char x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println(double x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println(float x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println(int x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println(long x) {
		super.print(FinishStr(x, true));
	}

	@Override
	public void println() {
		super.print(FinishStr("", true));
	}

	@Override
	public void print(String x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public void print(Object x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public void print(char[] x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public void print(boolean x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public void print(char x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public void print(double x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public void print(float x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public void print(int x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public void print(long x) {
		super.print(FinishStr(x, false));
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		return super.printf(FinishStr(String.format(format, args), false));
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		return super.printf(FinishStr(String.format(l, format, args), false));
	}

	@Override
	public PrintStream format(String format, Object... args) {
		return super.format(FinishStr(String.format(format, args), false));
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		return super.format(FinishStr(String.format(l, format, args), false));
	}

	@Override
	public void write(int b) {
		try {
			super.write(FinishBArr(b, false));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		super.write(FinishBArr(b, false));
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		try {
			super.write(FinishBArr(Arrays.copyOfRange(buf, off, off + len), false));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the tracing part for the start of the output.
	 * 
	 * @return the tracing part for the start of the output.
	 */
	private String getTrace() {
		String ret = "";
		if (traceTimestamp) {
			ret += "[" + DATE_FORMAT.format(new Date()) + "]";
		}
		if (traceThread) {
			if (ret.length() > 0) {
				ret += " ";
			}
			ret += "[" + Thread.currentThread().getName() + "]";
		}
		if (traceOutputtingClass) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			if (trace.length > traceStartDepth) {
				if (ret.length() > 0) {
					ret += " ";
				}
				StackTraceElement element = trace[traceStartDepth];
				boolean firstClass = false;
				if (traceSystemClasses) {
					ret += String.format("[%s]",
							traceSimpleClassName ? LogTracer.classNameToSimpleClassName(element.getClassName())
									: element.getClassName());
					if (traceLineNumber) {
						ret = ret.substring(0, ret.length() - 1) + String.format(":%s]", element.getLineNumber());
					}
					firstClass = true;
				} else if (!ArrayContains(LogTracer.SYSTEM_CLASSES, element.getClassName(), true)) {
					ret += String.format("[%s]",
							traceSimpleClassName ? LogTracer.classNameToSimpleClassName(element.getClassName())
									: element.getClassName());
					if (traceLineNumber) {
						ret = ret.substring(0, ret.length() - 1) + String.format(":%s]", element.getLineNumber());
					}
					firstClass = true;
				}
				int i = 0;
				while (ArrayContains(LogTracer.SYSTEM_CLASSES, element.getClassName(), true)) {
					i++;
					if (trace.length > traceStartDepth + i) {
						element = trace[traceStartDepth + i];
					} else {
						i--;
						break;
					}
				}
				if (i > 0) {
					if (firstClass == true) {
						ret += " ";
					}
					ret += String.format("[%s]",
							traceSimpleClassName ? LogTracer.classNameToSimpleClassName(element.getClassName())
									: element.getClassName());
					if (traceLineNumber) {
						ret = ret.substring(0, ret.length() - 1) + String.format(":%s]", element.getLineNumber());
					}
				}
			}
		}
		if (traceOutputtingMethod) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			if (trace.length > traceStartDepth) {
				if (ret.length() > 0) {
					ret += " ";
				}
				StackTraceElement element = trace[traceStartDepth];
				boolean firstClass = false;
				if (traceSystemClassMethods) {
					ret += "[" + element.getMethodName() + "]";
					firstClass = true;
				} else if (!ArrayContains(LogTracer.SYSTEM_CLASSES, element.getClassName(), true)) {
					ret += "[" + element.getMethodName() + "]";
					firstClass = true;
				}
				int i = 0;
				while (ArrayContains(LogTracer.SYSTEM_CLASSES, element.getClassName(), true)) {
					i++;
					if (trace.length > traceStartDepth + i) {
						element = trace[traceStartDepth + i];
					} else {
						i--;
						break;
					}
				}
				if (i > 0) {
					if (firstClass == true) {
						ret += " ";
					}
					ret += "[" + element.getMethodName() + "]";
				}
			}
		}
		if (ret.length() > 0) {
			ret += ": ";
		}
		return ret;
	}

	/**
	 * Creates a String from the given object and the trace.
	 * 
	 * @param x       the string to finish. can be a string, a byte array or a
	 *                character array.
	 * @param println whether to end the string with a lineSeperator.
	 * @return the finished string.
	 */
	private String FinishStr(Object x, boolean println) {
		String s = String.valueOf(x);
		if (x instanceof String) {
			s = (String) x;
		} else if (x instanceof byte[]) {
			s = new String((byte[]) x);
		} else if (x instanceof char[]) {
			s = new String((char[]) x);
		}
		String trace = getTrace();
		if (endLineSeperator) {
			s = trace + s;
			endLineSeperator = false;
		}
		s = s.replaceAll(LINE_SEPARATOR, LINE_SEPARATOR + trace.replaceAll("[$]", "\\\\\\$"));
		if (s.replaceAll(" ", "").endsWith(LINE_SEPARATOR + trace.replaceAll(" ", ""))) {
			s = s.substring(0, s.lastIndexOf(trace));
		}
		if (s.contains(trace + LINE_SEPARATOR)) {
			s = s.replaceAll(trace.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") + System.lineSeparator(),
					System.lineSeparator());
		}
		if (println) {
			s += LINE_SEPARATOR;
		}
		if (s.replaceAll(" ", "").endsWith(LINE_SEPARATOR)) {
			endLineSeperator = true;
		}
		return s;
	}

	/**
	 * Creates a byte array from the given object and the trace.
	 * 
	 * @param x       the string to finish. Can be a string, a byte array or a
	 *                character array.
	 * @param println whether to end the byte array with a lineSeperator.
	 * @return the finished byte array.
	 */
	private byte[] FinishBArr(Object x, boolean println) {
		return FinishStr(x, println).getBytes();
	}

	/**
	 * Checks whether the given array contains the given string, or a string
	 * starting with the given string.
	 * 
	 * @param array      the array to search in.
	 * @param str        the string to search
	 * @param startsWith whether the string in the array needs start with given
	 *                   string.
	 * @return whether the given array contains the given string, or a string
	 *         starting with the given string.
	 */
	private boolean ArrayContains(String[] array, String str, boolean startsWith) {
		boolean contains = false;
		for (String s : array) {
			if (startsWith) {
				if (str.startsWith(s)) {
					contains = true;
					break;
				}
			} else {
				if (str.equals(s)) {
					contains = true;
					break;
				}
			}
		}
		return contains;
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
		cfg.addConfig(cfgFile, "traceThread", traceThread,
				"Whether the beginning of every line of output should contain the name of the Thread writing it.");
		cfg.addConfig(cfgFile, "traceOutputtingClass", traceOutputtingClass,
				"Whether the beginning of every line of output should contain the name of the class writing it.");
		cfg.addConfig(cfgFile, "traceSystemClasses", traceSystemClasses,
				"Whether the beginning of every line of output should contain the name of the class writing it "
						+ "even if its a system class.");
		cfg.addConfig(cfgFile, "traceSimpleClassName", traceSimpleClassName,
				"Whether the beginning of every line of output should contain simple class names(without package) "
						+ "or full class names(with package).");
		cfg.addConfig(cfgFile, "traceOutputtingMethod", traceOutputtingMethod,
				"Whether the beginning of every line of output should contain the name of the method writing it.");
		cfg.addConfig(cfgFile, "traceSystemClassMethods", traceSystemClassMethods,
				"Whether the beginning of every line of output should contain the name of the method writing it "
						+ "even if it is from a system class.");
		cfg.addConfig(cfgFile, "traceLineNumber", traceLineNumber,
				"Whether the beginning of every line of output should contain the number of the line writing it.");
		cfg.addConfig(cfgFile, "traceStartDepth", traceStartDepth,
				"How deep into the Stacktrace the tracer should start looking for informations.");

		cfg.readConfig();
		readConfig();
	}

	/**
	 * Reads the values from the given config file.
	 */
	private void readConfig() {
		cfg.readConfig();
		traceTimestamp = (boolean) cfg.getConfig("traceTimestamp");
		traceThread = (boolean) cfg.getConfig("traceThread");
		traceOutputtingClass = (boolean) cfg.getConfig("traceOutputtingClass");
		traceSystemClasses = (boolean) cfg.getConfig("traceSystemClasses");
		traceSimpleClassName = (boolean) cfg.getConfig("traceSimpleClassName");
		traceOutputtingMethod = (boolean) cfg.getConfig("traceOutputtingMethod");
		traceSystemClassMethods = (boolean) cfg.getConfig("traceSystemClassMethods");
		traceLineNumber = (boolean) cfg.getConfig("traceLineNumber");
		traceStartDepth = (int) cfg.getConfig("traceStartDepth");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cfg == null) ? 0 : cfg.hashCode());
		result = prime * result + (endLineSeperator ? 1231 : 1237);
		result = prime * result + (traceLineNumber ? 1231 : 1237);
		result = prime * result + (traceOutputtingClass ? 1231 : 1237);
		result = prime * result + (traceOutputtingMethod ? 1231 : 1237);
		result = prime * result + (traceSimpleClassName ? 1231 : 1237);
		result = prime * result + traceStartDepth;
		result = prime * result + (traceSystemClassMethods ? 1231 : 1237);
		result = prime * result + (traceSystemClasses ? 1231 : 1237);
		result = prime * result + (traceThread ? 1231 : 1237);
		result = prime * result + (traceTimestamp ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TracingMultiPrintStream other = (TracingMultiPrintStream) obj;
		if (cfg == null) {
			if (other.cfg != null) {
				return false;
			}
		} else if (!cfg.equals(other.cfg)) {
			return false;
		}
		if (endLineSeperator != other.endLineSeperator) {
			return false;
		}
		if (traceLineNumber != other.traceLineNumber) {
			return false;
		}
		if (traceOutputtingClass != other.traceOutputtingClass) {
			return false;
		}
		if (traceOutputtingMethod != other.traceOutputtingMethod) {
			return false;
		}
		if (traceSimpleClassName != other.traceSimpleClassName) {
			return false;
		}
		if (traceStartDepth != other.traceStartDepth) {
			return false;
		}
		if (traceSystemClassMethods != other.traceSystemClassMethods) {
			return false;
		}
		if (traceSystemClasses != other.traceSystemClasses) {
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