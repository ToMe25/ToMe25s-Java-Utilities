package com.tome25.utils.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A Handler that splits records into System.out and System.err based on their
 * log level.
 * 
 * @author ToMe25
 *
 */
public class SplittingHandler extends Handler {

	private final Logger error;
	private final Logger output;

	/**
	 * creates a new SplittingHandler with the default loggers.
	 */
	public SplittingHandler() {
		this(LogTracer.getOutput(), LogTracer.getError());
	}

	/**
	 * creates a new SplittingHandler with custom loggers.
	 * 
	 * @param output the logger to redirect log level info and below to.
	 * @param error  the logger to redirect log level warning and above to.
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
	 * publishes the give LogRecord to all the handlers of the given Logger
	 * 
	 * @param logger the logger to publish the record to.
	 * @param record the log record to publish to logger.
	 */
	private void publish(Logger logger, LogRecord record) {
		for (Handler handler : logger.getHandlers()) {
			if (handler.isLoggable(record)) {
				handler.publish(record);
			}
		}
	}

}
