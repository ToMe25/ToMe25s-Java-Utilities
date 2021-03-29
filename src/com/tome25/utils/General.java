/*
 * ToMe25s-Java-Utilities is a collection of common java utilities.
 * Copyright (C) 2021  ToMe25
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
package com.tome25.utils;

import java.lang.reflect.Method;

/**
 * A collection of common utilities that don't fit any particular package.
 * 
 * @author ToMe25
 */
public abstract class General {

	/**
	 * The method {@link Object#clone} to be used by
	 * {@link #reflectiveClone(Cloneable)}.
	 */
	private static Method clone;

	static {
		try {
			clone = Object.class.getDeclaredMethod("clone");
			clone.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clones the given object by calling its clone method using reflection.<br/>
	 * By implementing {@link Cloneable} the object marks this as a legal operation.
	 * 
	 * @param <T>    The type of the object to clone.
	 * @param object the object to clone.
	 * @return the new clone.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Cloneable> T reflectiveClone(T object) {
		T clone = null;
		try {
			clone = (T) General.clone.invoke(object);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return clone;
	}

}
