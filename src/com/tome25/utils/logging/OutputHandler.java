package com.tome25.utils.logging;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A StreamHandler that can have a custom log level from the start.
 * 
 * @author ToMe25
 *
 */
public class OutputHandler extends StreamHandler {

	/**
	 * creates a new OutputHandler with log level info.
	 * 
	 * @param out       the OutputStream to write the messages to.
	 * @param formatter the formatter to use for all messages.
	 */
	public OutputHandler(OutputStream out, Formatter formatter) {
		this(out, Level.INFO, formatter);
	}

	/**
	 * creates a new OutputHandler with the given log level.
	 * 
	 * @param outthe    OutputStream to write the messages to.
	 * @param level     the minimal log level this handler should handle.
	 * @param formatter the formatter to use for all messages.
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
