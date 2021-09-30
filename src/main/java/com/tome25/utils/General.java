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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A collection of common utilities that don't fit any particular package.
 * 
 * @author ToMe25
 */
public abstract class General {

	/**
	 * The clone methods of the classes that were already cloned. Used by
	 * {@link #reflectiveClone(Cloneable)}.
	 */
	private static final Map<Class<?>, Method> CLONE_METHODS = new IdentityHashMap<>();

	/**
	 * Clones the given object by calling its clone method using reflection.<br>
	 * By implementing {@link Cloneable} the object marks this as a legal operation.
	 * 
	 * @param <T>    The type of the object to clone.
	 * @param object the object to clone.
	 * @return the new clone.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Cloneable> T reflectiveClone(T object) {
		T clone = null;
		Class<?> clazz = object.getClass();

		if (!CLONE_METHODS.containsKey(clazz)) {
			Method cln = null;
			try {
				cln = clazz.getMethod("clone");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}

			if (cln == null) {
				try {
					cln = Object.class.getDeclaredMethod("clone");
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}

			if (!Modifier.isPublic(cln.getModifiers())) {
				System.err.printf("Class %s does not have a publicly accessible clone method.%n", clazz.getName());
				System.err.println(
						"This is recommended for all classes implementing Cloneable, and necessary for this method as of Java 16.");
			}
			cln.setAccessible(true);
			CLONE_METHODS.put(clazz, cln);
		}

		try {
			clone = (T) CLONE_METHODS.get(clazz).invoke(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return clone;
	}

}
