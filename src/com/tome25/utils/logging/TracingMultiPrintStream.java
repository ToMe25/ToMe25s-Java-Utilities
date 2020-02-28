package com.tome25.utils.logging;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

import com.tome25.utils.config.Config;

import java.lang.Throwable;

public class TracingMultiPrintStream extends MultiPrintStream {

	private boolean traceTimestamp = true;
	private boolean traceThread = true;
	private boolean traceOutputtingClass = true;
	/**
	 * Whether system classes should be used as the outputting class for some text.
	 */
	private boolean traceSystemClasses = true;
	/**
	 * Whether the traced class name should be the simple class name(without
	 * package)
	 */
	private boolean traceSimpleClassName = true;
	private boolean traceOutputtingMethod = true;
	private boolean traceSystemClassMethods = false;
	private boolean traceLineNumber = false;
	private int traceStartDepth = 4;
	private Config cfg;
	private boolean endLineSeperator = true;
	/**
	 * The Classes to Skip for getTrace.
	 */
	private final String[] systemClasses = { PrintStream.class.getName(), Throwable.class.getName(),
			Formatter.class.getName(), TracingMultiPrintStream.class.getName(), ThreadGroup.class.getName(),
			Thread.class.getName() };
	protected String lineSeparator = System.lineSeparator();

	public TracingMultiPrintStream(OutputStream... outs) {
		this(new File(
				new File(TracingMultiPrintStream.class.getProtectionDomain().getCodeSource().getLocation().getPath())
						.getParent(),
				classNameToSimpleClassName(Thread.currentThread().getStackTrace()[2].getClassName())
						+ "TracingMultiPrintStream.cfg"),
				outs);
	}

	public TracingMultiPrintStream(File configFile, OutputStream... outs) {
		super(outs);
		cfg = new Config(configFile.getParentFile(), false);
		readConfig(configFile);
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

	private String getTrace() {
		String ret = "";
		if (traceTimestamp) {
			LocalTime time = LocalTime.now();
			ret += "[" + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + "]";
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
							traceSimpleClassName ? classNameToSimpleClassName(element.getClassName())
									: element.getClassName());
					if (traceLineNumber) {
						ret = ret.substring(0, ret.length() - 1) + String.format(":%s]", element.getLineNumber());
					}
					firstClass = true;
				} else if (!ArrayContains(systemClasses, element.getClassName(), true)) {
					ret += String.format("[%s]",
							traceSimpleClassName ? classNameToSimpleClassName(element.getClassName())
									: element.getClassName());
					if (traceLineNumber) {
						ret = ret.substring(0, ret.length() - 1) + String.format(":%s]", element.getLineNumber());
					}
					firstClass = true;
				}
				int i = 0;
				while (ArrayContains(systemClasses, element.getClassName(), true)) {
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
							traceSimpleClassName ? classNameToSimpleClassName(element.getClassName())
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
				} else if (!ArrayContains(systemClasses, element.getClassName(), true)) {
					ret += "[" + element.getMethodName() + "]";
					firstClass = true;
				}
				int i = 0;
				while (ArrayContains(systemClasses, element.getClassName(), true)) {
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
	 * Creates a String from the given Object and the Trace.
	 * 
	 * @param x
	 * @param println whether to end the String with a lineSeperator
	 * @return
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
		s = s.replaceAll(lineSeparator, lineSeparator + trace.replaceAll("[$]", "\\\\\\$"));
		if (s.replaceAll(" ", "").endsWith(lineSeparator + trace.replaceAll(" ", ""))) {
			s = s.substring(0, s.lastIndexOf(trace));
		}
		if (s.contains(trace + lineSeparator)) {
			s = s.replaceAll(trace.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") + System.lineSeparator(),
					System.lineSeparator());
		}
		if (println) {
			s += lineSeparator;
		}
		if (s.replaceAll(" ", "").endsWith(lineSeparator)) {
			endLineSeperator = true;
		}
		return s;
	}

	/**
	 * Creates a byte[] from the given Object and the Trace.
	 * 
	 * @param x
	 * @param println whether to end the byte[] with a lineSeperator
	 * @return
	 */
	private byte[] FinishBArr(Object x, boolean println) {
		return FinishStr(x, println).getBytes();
	}

	/**
	 * Checks if the given array contains the String str, or a String starting with
	 * str.
	 * 
	 * @param array      the Array to Search in.
	 * @param str        the String to Search
	 * @param startsWith needs the String in the Array only start with str?
	 * @return
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

	private void readConfig(File cfgFile) {
		// init config
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
				"Whether the beginning of every line of output should contain the number of th line writing it.");
		cfg.addConfig(cfgFile, "traceStartDepth", traceStartDepth,
				"How deep into the Stacktrace the tracer should start looking for informations.");
		// read config
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

	/**
	 * Converts the class name with package to a simple class name without.
	 * 
	 * @param className the class name to convert
	 * @return
	 */
	private static String classNameToSimpleClassName(String className) {
		return className.contains(".") ? className.substring(className.lastIndexOf('.') + 1)
				: className.substring(className.lastIndexOf('/') + 1);
	}

}