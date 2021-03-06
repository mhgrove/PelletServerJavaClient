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

import static com.google.common.base.Preconditions.checkArgument;

import javax.activation.MimeType;

import org.openrdf.model.Graph;


import com.clarkparsia.pelletserver.client.Callback;
import com.clarkparsia.pelletserver.client.Endpoint;
import com.clarkparsia.pelletserver.client.KnowledgeBase;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.pelletserver.client.PelletServerMimeTypes;
import com.clarkparsia.pelletserver.client.PelletService;
import com.clarkparsia.pelletserver.client.PelletServiceCallbackTask;
import com.clarkparsia.pelletserver.client.ServiceAnnotation;
import com.clarkparsia.pelletserver.client.utils.CallbackUtils;
import com.clarkparsia.pelletserver.client.utils.OpenRdfUtils;
import com.clarkparsia.pelletserver.client.utils.RequestUtils;
import com.clarkparsia.utils.web.Response;
import com.google.common.collect.Iterables;

/**
 * The Classify service performs classification in the {@link KnowledgeBase}
 * 
 * @author Pedro Oliveira
 * 
 */
@ServiceAnnotation("classify")
public class Classify extends AbstractKBPelletService {

	private static final MimeType MIMETYPE = PelletServerMimeTypes.RDFXML;

	public Classify(KnowledgeBase kb, Endpoint endpoint, MimeType... mimetypes) {
		super(kb, endpoint, mimetypes);

		checkArgument(Iterables.any(this.mimetypes, PelletServerMimeTypes.getPredicate(MIMETYPE)), getName() + " service must support %s", MIMETYPE);
	}

	/**
	 * Classify the {@link KnowledgeBase}
	 * 
	 * @return The classification {@link org.openrdf.model.Graph Graph}
	 * @throws PelletClientException if there was an error during invocation
	 */
	public Graph classify() throws PelletClientException {
		return new ClassifyTask(this).execute();
	}

	/**
	 * Asynchronously classify the {@link KnowledgeBase}
	 * 
	 * @param callback
	 *            The {@link Callback} to execute after the classification is done
	 */
	public void classify(Callback<Graph> callback) {
		CallbackUtils.launchThread(callback, new ClassifyTask(this));
	}

	private static class ClassifyTask extends PelletServiceCallbackTask<Graph> {

		protected ClassifyTask(PelletService service) {
			super(service);
		}

		public Graph execute() throws PelletClientException {
			Response response = RequestUtils.execute(service.getEndpoint(), service.getServer().getPreferredMethod(), MIMETYPE);

			try {
				return OpenRdfUtils.createGraphFromRDFXMLBlob(response.getContent());
			}
			catch (Exception e) {
				throw new PelletClientException("Problem parsing " + MIMETYPE + " response", e);
			}
		}
	}

}
