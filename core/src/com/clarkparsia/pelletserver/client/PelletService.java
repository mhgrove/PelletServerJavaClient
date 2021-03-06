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

import java.util.Collection;

import javax.activation.MimeType;

/**
 * A service provided by {@link PelletServer}
 * 
 * @author Pedro Oliveira
 * 
 */
public interface PelletService {

	/**
	 * The name of the service
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Response {@link MimeType}s provided by the service
	 * 
	 * @return the supported mimetypes
	 */
	public Collection<MimeType> getMimeTypes();

	/**
	 * The {@link Endpoint} of the service
	 * 
	 * @return the service endpoint
	 */
	public Endpoint getEndpoint();

	/**
	 * The {@link PelletServer}
	 * 
	 * @return the parent PelletServer instance
	 */
	public PelletServer getServer();
}
