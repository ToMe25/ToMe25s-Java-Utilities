package com.tome25.utils.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 
 * A standard {@link PrintStream} except it can write to multiple
 * {@link OutputStream}s.
 * 
 * @author ToMe25
 *
 */
public class MultiPrintStream extends PrintStream {

	private PrintStream[] streams;

	/**
	 * Creates a new MultiPrintStream, printing to the given {@link OutputStream}s.
	 * 
	 * @param outputStreams the {@link OutputStream}s to print to.
	 */
	public MultiPrintStream(OutputStream... outputStreams) {
		super(outputStreams.length > 0 ? outputStreams[0] : null);
		streams = new PrintStream[outputStreams.length];
		int i = 0;
		while (i < outputStreams.length) {
			if (outputStreams[i] instanceof PrintStream) {
				streams[i] = (PrintStream) outputStreams[i];
			} else {
				streams[i] = new PrintStream(outputStreams[i]);
			}
			i++;
		}
	}

	/**
	 * Adds the given {@link OutputStream} to the List of {@link OutputStream}s that
	 * is being printed to.
	 * 
	 * @param out the {@link OutputStream} to add.
	 * @return this MultiPrintStream.
	 */
	public MultiPrintStream addOutputStream(OutputStream out) {
		streams = Arrays.copyOf(streams, streams.length + 1);
		if (out instanceof PrintStream) {
			streams[streams.length - 1] = (PrintStream) out;
		} else {
			streams[streams.length - 1] = new PrintStream(out);
		}
		return this;
	}

	/**
	 * returns a list of all used {@link OutputStream}s.
	 * 
	 * @return a new list of all used {@link OutputStream}s.
	 */
	public List<OutputStream> getOutputStreams() {
		List<OutputStream> list = new ArrayList<OutputStream>();
		for (OutputStream stream : streams) {
			list.add(stream);
		}
		return list;
	}

	@Override
	public PrintStream append(char c) {
		for (PrintStream ps : streams) {
			ps.append(c);
		}
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		for (PrintStream ps : streams) {
			ps.append(csq);
		}
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		for (PrintStream ps : streams) {
			ps.append(csq, start, end);
		}
		return this;
	}

	@Override
	public boolean checkError() {
		boolean error = false;
		for (PrintStream ps : streams) {
			error = error || ps.checkError();
		}
		return error;
	}

	@Override
	public void close() {
		for (PrintStream ps : streams) {
			ps.close();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(streams);
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
		MultiPrintStream other = (MultiPrintStream) obj;
		if (!Arrays.equals(streams, other.streams)) {
			return false;
		}
		return true;
	}

	@Override
	public void flush() {
		for (PrintStream ps : streams) {
			ps.flush();
		}
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		for (PrintStream ps : streams) {
			ps.format(l, format, args);
		}
		return this;
	}

	@Override
	public PrintStream format(String format, Object... args) {
		for (PrintStream ps : streams) {
			ps.format(format, args);
		}
		return this;
	}

	@Override
	public void print(boolean b) {
		for (PrintStream ps : streams) {
			ps.print(b);
		}
	}

	@Override
	public void print(char c) {
		for (PrintStream ps : streams) {
			ps.print(c);
		}
	}

	@Override
	public void print(char[] s) {
		for (PrintStream ps : streams) {
			ps.print(s);
		}
	}

	@Override
	public void print(double d) {
		for (PrintStream ps : streams) {
			ps.print(d);
		}
	}

	@Override
	public void print(float f) {
		for (PrintStream ps : streams) {
			ps.print(f);
		}
	}

	@Override
	public void print(int i) {
		for (PrintStream ps : streams) {
			ps.print(i);
		}
	}

	@Override
	public void print(long l) {
		for (PrintStream ps : streams) {
			ps.print(l);
		}
	}

	@Override
	public void print(Object obj) {
		for (PrintStream ps : streams) {
			ps.print(obj);
		}
	}

	@Override
	public void print(String s) {
		for (PrintStream ps : streams) {
			ps.print(s);
		}
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		for (PrintStream ps : streams) {
			ps.printf(l, format, args);
		}
		return this;
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		for (PrintStream ps : streams) {
			ps.printf(format, args);
		}
		return this;
	}

	@Override
	public void println() {
		for (PrintStream ps : streams) {
			ps.println();
		}
	}

	@Override
	public void println(boolean x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void println(char x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void println(char[] x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void println(double x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void println(float x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void println(int x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void println(long x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void println(Object x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void println(String x) {
		for (PrintStream ps : streams) {
			ps.println(x);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (PrintStream ps : streams) {
			ps.write(b);
		}
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		for (PrintStream ps : streams) {
			ps.write(buf, off, len);
		}
	}

	@Override
	public void write(int b) {
		for (PrintStream ps : streams) {
			ps.write(b);
		}
	}

}
