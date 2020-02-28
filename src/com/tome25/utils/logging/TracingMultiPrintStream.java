package com.tome25.utils.logging;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
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
	private int traceStartDepth = 4;
	private Config cfg;
	private boolean endLineSeperator = true;
	private List<OutputStream> outs = new ArrayList<OutputStream>();
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
		for (OutputStream out : outs) {
			this.outs.add(out);
		}
		readConfig(configFile);
	}

	/**
	 * Adds the given OutputStream to the List of Output Streams that is being used
	 * as Output.
	 * 
	 * @param out the OutputStream to add.
	 */
	public void addOutputStream(OutputStream out) {
		super.addOutputStream(out);
		outs.add(out);
	}

	@Override
	public void println(String x) {
		for (OutputStream out : outs) {
			if (x != null && out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println(Object x) {
		for (OutputStream out : outs) {
			if (x != null && out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println(char[] x) {
		for (OutputStream out : outs) {
			if (x != null && out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println(boolean x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println(char x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println(double x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println(float x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println(int x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println(long x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, true));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void println() {
		for (OutputStream out : outs) {
			try {
				out.write(Finish(lineSeparator, false));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void print(String x) {
		for (OutputStream out : outs) {
			if (x != null && out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void print(Object x) {
		for (OutputStream out : outs) {
			if (x != null && out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void print(char[] x) {
		for (OutputStream out : outs) {
			if (x != null && out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void print(boolean x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void print(char x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void print(double x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void print(float x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void print(int x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void print(long x) {
		for (OutputStream out : outs) {
			if (out != null) {
				try {
					out.write(Finish(x, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		return format(format, args);
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		return format(l, format, args);
	}

	@Override
	public PrintStream format(String format, Object... args) {
		return format(Locale.getDefault(), format, args);
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		Formatter form = new Formatter(l);
		form.format(format, args);
		print(form.toString());
		form.close();
		return this;
	}

	@Override
	public void write(int b) {
		print(b);
	}

	@Override
	public void write(byte[] b) {
		print(b);
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		print(new String(buf).substring(off, len));
	}

	@Override
	public PrintStream append(char c) {
		print(c);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		print(csq.toString());
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		print(csq.toString().substring(start, end));
		return this;
	}

	@Override
	public void close() {
		super.close();
		for (OutputStream out : outs) {
			try {
				out.close();
			} catch (Exception e) {
			}
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
					firstClass = true;
				} else if (!ArrayContains(systemClasses, element.getClassName(), true)) {
					ret += String.format("[%s]",
							traceSimpleClassName ? classNameToSimpleClassName(element.getClassName())
									: element.getClassName());
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
	 * Creates a byte[] from the given Object and the Trace.
	 * 
	 * @param x
	 * @param println
	 * @return
	 */
	private byte[] Finish(Object x, boolean println) {
		byte[] ret;
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
		ret = s.getBytes();
		return ret;
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
				"Whether the begin of every line of output should contain a timestamp.");
		cfg.addConfig(cfgFile, "traceThread", traceThread,
				"Whether the begin of every line of output should contain the name of the Thread writing it.");
		cfg.addConfig(cfgFile, "traceOutputtingClass", traceOutputtingClass,
				"Whether the begin of every line of output should contain the name of the class writing it.");
		cfg.addConfig(cfgFile, "traceSystemClasses", traceSystemClasses,
				"Whether the begin of every line of output should contain the name of the class writing it "
						+ "even if its a system class.");
		cfg.addConfig(cfgFile, "traceSimpleClassName", traceSimpleClassName,
				"Whether the begin of every line of output should contain simple class names(without package) "
						+ "or full class names(with package).");
		cfg.addConfig(cfgFile, "traceOutputtingMethod", traceOutputtingMethod,
				"Whether the begin of every line of output should contain the name of the method writing it.");
		cfg.addConfig(cfgFile, "traceSystemClassMethods", traceSystemClassMethods,
				"Whether the begin of every line of output should contain the name of the method writing it "
						+ "even if it is from a system class.");
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