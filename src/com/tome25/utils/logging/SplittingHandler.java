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

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A {@link Handler} that splits {@link LogRecord}s into {@link System#out} and
 * {@link System#err} based on their log {@link java.util.logging.Level Level}.
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
	 *               {@link java.util.logging.Level Level} info and below to.
	 * @param error  the {@link Logger} to redirect log
	 *               {@link java.util.logging.Level Level} warning and above to.
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
	 * Publishes the give {@link LogRecord} to all the handlers of the given
	 * {@link Logger}.
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
