package com.tome25.utils.logging;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A {@link StreamHandler} that can have a custom log {@link Level} from the
 * start. Also it automatically flushes like the
 * {@link java.util.logging.ConsoleHandler}.
 * 
 * @author ToMe25
 *
 */
public class OutputHandler extends StreamHandler {

	/**
	 * Creates a new OutputHandler with log {@link Level} info.
	 * 
	 * @param out       the {@link OutputStream} to write the {@link LogRecord}s to.
	 * @param formatter the {@link Formatter} to use for all {@link LogRecord}s.
	 */
	public OutputHandler(OutputStream out, Formatter formatter) {
		this(out, Level.INFO, formatter);
	}

	/**
	 * Creates a new OutputHandler with the given log {@link Level}.
	 * 
	 * @param out       the {@link OutputStream} to write the {@link LogRecord}s to.
	 * @param level     the minimal log {@link Level} this handler should handle.
	 * @param formatter the {@link Formatter} to use for all {@link LogRecord}s.
	 */
	public OutputHandler(OutputStream out, Level level, Formatter formatter) {
		super(out, formatter);
		setLevel(level);
	}

	@Override
	public void close() {
		flush();
	}

	@Override
	public void publish(LogRecord record) {
		super.publish(record);
		flush();
	}

}
