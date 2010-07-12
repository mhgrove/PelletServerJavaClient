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

package com.clarkparsia.pelletserver.client;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.clarkparsia.utils.web.Method;
import com.google.common.collect.Lists;

/**
 * Provides information about an HTTP endpoint, i.e., its {@link URL} and HTTP {@link Method}s
 * 
 * @author Pedro Oliveira
 * 
 */
public class Endpoint {

	private URL url;
	private List<Method> methods;

	public Endpoint(URL url, Method... methods) {
		this.url = url;
		this.methods = Lists.newArrayList(methods);
	}

	public Endpoint(URL url, Collection<Method> methods) {
		this.url = url;
		this.methods = Lists.newArrayList(methods);
	}

	/**
	 * Get the {@link URL} of the {@link Endpoint}
	 * 
	 * @return the endpoint URL
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Get the HTTP {@link Method}s provided by this {@link Endpoint}
	 * 
	 * @return the supported HTTP methods
	 */
	public Collection<Method> getHTTPMethods() {
		return Collections.unmodifiableCollection(methods);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return url + " " + methods;
	}
}
