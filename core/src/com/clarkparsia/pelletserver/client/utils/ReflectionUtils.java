/*
 * Copyright (c) 2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.clarkparsia.pelletserver.client.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.collect.Sets;

/**
 * Utilities related to {@link Reflections} API
 * 
 * @author Pedro Oliveira
 * 
 */
public abstract class ReflectionUtils {

	private static final Reflections REFLECTIONS = 
		new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.getUrlsForCurrentClasspath())
						                          .setScanners(new SubTypesScanner()));

	/**
	 * Get all non-abstract implementations of class {@code cl} annotated with {@code annotation}
	 * 
	 * @param <T> the class type
	 * @param cl
	 *            The class
	 * @param annotation
	 *            The annotation class
	 * @return the classes which implement the interface and have the given annotation
	 */
	public static <T> Set<Class<? extends T>> getImplementations(Class<T> cl, Class<? extends Annotation> annotation) {

		Set<Class<? extends T>> implementations = Sets.newHashSet();
		Set<Class<? extends T>> subTypes = REFLECTIONS.getSubTypesOf(cl);

		for (Class<? extends T> subtype : subTypes) {
			if (!Modifier.isAbstract(subtype.getModifiers()) && subtype.isAnnotationPresent(annotation)) {
				implementations.add(subtype);
			}
		}

		return implementations;
	}
}
