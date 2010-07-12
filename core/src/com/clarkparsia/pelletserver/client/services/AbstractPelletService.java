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

package com.clarkparsia.pelletserver.client.services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.activation.MimeType;


import com.clarkparsia.pelletserver.client.Endpoint;
import com.clarkparsia.pelletserver.client.PelletServer;
import com.clarkparsia.pelletserver.client.PelletService;
import com.clarkparsia.pelletserver.client.ServiceAnnotation;
import com.google.common.collect.Lists;

/**
 * An abstract {@link PelletService}
 * 
 * @author Pedro Oliveira
 * 
 */
public abstract class AbstractPelletService implements PelletService {

	protected List<MimeType> mimetypes;
	protected Endpoint endpoint;
	protected PelletServer server;

	protected AbstractPelletService(PelletServer server, Endpoint endpoint, MimeType... mimetypes) {
		this.server = server;
		this.endpoint = endpoint;
		this.mimetypes = Lists.newArrayList(mimetypes);
	}

	/**
	 * @inheritDoc
	 */
	public Endpoint getEndpoint() {
		return endpoint;
	}

	/**
	 * @inheritDoc
	 */
	public Collection<MimeType> getMimeTypes() {
		return Collections.unmodifiableCollection(mimetypes);
	}

	/**
	 * @inheritDoc
	 */
	public PelletServer getServer() {
		return server;
	}

	/**
	 * @inheritDoc
	 */
	public String getName() {
		ServiceAnnotation annotation = this.getClass().getAnnotation(ServiceAnnotation.class);

		if (annotation != null) {
			return annotation.value();
		}

		return "";
	}

}
