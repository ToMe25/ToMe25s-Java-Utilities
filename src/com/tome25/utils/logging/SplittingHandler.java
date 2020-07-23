package com.tome25.utils.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A {@link Handler} that splits {@link LogRecord}s into System.out and
 * System.err based on their log {@link java.util.logging.Level}.
 * 
 * @author ToMe25
 *
 */
public class SplittingHandler extends Handler {

	private final Logger error;
	private final Logger output;

	/**
	 * Creates a new SplittingHandler with the default output and error
	 * {@link Logger}s.
	 */
	public SplittingHandler() {
		this(LogTracer.getOutput(), LogTracer.getError());
	}

	/**
	 * Creates a new SplittingHandler with custom {@link Logger}s.
	 * 
	 * @param output the {@link Logger} to redirect log
	 *               {@link java.util.logging.Level} info and below to.
	 * @param error  the {@link Logger} to redirect log
	 *               {@link java.util.logging.Level} warning and above to.
	 */
	public SplittingHandler(Logger output, Logger error) {
		this.error = error;
		this.output = output;
	}

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		if (record.getLevel().intValue() <= 800) {
			publish(output, record);
		} else {
			publish(error, record);
		}
	}

	/**
	 * Publishes the give {@link LogRecord} to all the handlers of the given {@link Logger}.
	 * 
	 * @param logger the {@link Logger} to publish the {@link LogRecord} to.
	 * @param record the {@link LogRecord} to publish to {@link Logger}.
	 */
	private void publish(Logger logger, LogRecord record) {
		for (Handler handler : logger.getHandlers()) {
			if (handler.isLoggable(record)) {
				handler.publish(record);
			}
		}
	}

}
