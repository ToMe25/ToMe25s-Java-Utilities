package com.tome25.utils.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * a Handler that splits records into System.out and System.err based on their
 * log level.
 * 
 * @author ToMe25
 *
 */
public class SplittingHandler extends Handler {

	@Override
	public void close() {

	}

	@Override
	public void flush() {

	}

	@Override
	public void publish(LogRecord record) {
		if (record.getLevel().intValue() <= 800) {
			publish(LogTracer.getOutput(), record);
		} else {
			publish(LogTracer.getError(), record);
		}
	}

	/**
	 * publishes the give LogRecord to all the handlers of the given Logger
	 * 
	 * @param logger
	 * @param record
	 */
	private void publish(Logger logger, LogRecord record) {
		for (Handler handler : logger.getHandlers()) {
			if (handler.isLoggable(record)) {
				handler.publish(record);
			}
		}
	}

}
