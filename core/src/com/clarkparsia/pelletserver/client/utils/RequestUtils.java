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

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import javax.activation.MimeType;


import com.clarkparsia.pelletserver.client.Endpoint;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.utils.web.HttpHeaders;
import com.clarkparsia.utils.web.Method;
import com.clarkparsia.utils.web.Request;
import com.clarkparsia.utils.web.Response;
import com.google.common.base.Joiner;

/**
 * Utilities related to HTTP {@link Request}s
 * 
 * @author Pedro Oliveira
 * 
 */
public abstract class RequestUtils {

	private static final Joiner COMMA_JOINER = Joiner.on(',').skipNulls();

	/**
	 * Executes a HTTP Request with the provided parameters, and returns the server {@link Response}
	 * 
	 * @param endpoint
	 *            The server {@link Endpoint}
	 * @param defaultMethod
	 *            Default HTTP Request {@link com.clarkparsia.utils.web.Method Method} to use
	 * @param mimeType
	 *            {@link MimeType}s to use in HTTP Accept header
	 * @return The response of the HTTP call
	 * @throws PelletClientException
	 *             If the Request fails or its response code is different from 200
	 */
	public static Response execute(Endpoint endpoint, Method defaultMethod, MimeType... mimeType) 
		throws PelletClientException {

		Collection<Method> methods = endpoint.getHTTPMethods();

		if (methods.size() > 0) {
			Method method = (defaultMethod != null && methods.contains(defaultMethod)) ? defaultMethod 
							                                                           : methods.iterator().next();

			try {
				Response response = execute(endpoint.getURL(), method, mimeType);

				if (response.getResponseCode() != 200) {
					throw new PelletClientException("Wrong response code (" + response.getResponseCode() + ") :\n" 
									               + response.getContent());
				}

				return response;
			}
			catch (IOException e) {
				throw new PelletClientException(e);
			}
		}

		throw new PelletClientException("No HTTP methods in " + endpoint.getURL());
	}

	private static Response execute(URL url, Method method, MimeType... mimeType) throws IOException {
		Request request = new Request(method, url);
		request.addHeader(HttpHeaders.Accept.toString(), COMMA_JOINER.join(mimeType));
		return request.execute();
	}
}
