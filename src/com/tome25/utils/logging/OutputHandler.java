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

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A {@link StreamHandler} that can have a custom log {@link Level} from the
 * start. Also it automatically flushes like the
 * {@link java.util.logging.ConsoleHandler ConsoleHandler}.
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
