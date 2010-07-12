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

import javax.activation.MimeType;

import com.clarkparsia.pelletserver.client.Endpoint;
import com.clarkparsia.pelletserver.client.KBPelletService;
import com.clarkparsia.pelletserver.client.KnowledgeBase;

/**
 * An abstract {@link KBPelletService}
 * 
 * @author Pedro Oliveira
 */
public abstract class AbstractKBPelletService extends AbstractPelletService implements KBPelletService {

	/**
	 * The KB for the service
	 */
	protected KnowledgeBase kb;

	/**
	 * Create a new AbstractKBPelletService
	 * @param kb the kb
	 * @param endpoint the service endpoint
	 * @param mimetypes the supported mimetypes
	 */
	protected AbstractKBPelletService(KnowledgeBase kb, Endpoint endpoint, MimeType... mimetypes) {
		super(kb.getServer(), endpoint, mimetypes);
		this.kb = kb;
	}

	/**
	 * @inheritDoc
	 */
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}
}
