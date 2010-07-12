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

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResult;


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
 * The Consistency service checks if the {@link KnowledgeBase} is consistent
 * 
 * @author Pedro Oliveira
 * 
 */
@ServiceAnnotation("consistency")
public class Consistency extends AbstractKBPelletService {

	private static final MimeType MIMETYPE = PelletServerMimeTypes.SPARQL_XML;

	public Consistency(KnowledgeBase kb, Endpoint endpoint, MimeType... mimetypes) {
		super(kb, endpoint, mimetypes);

		checkArgument(Iterables.any(this.mimetypes, PelletServerMimeTypes.getPredicate(MIMETYPE)), 
						            getName() + " service must support %s", MIMETYPE);
	}

	/**
	 * Checks if the {@link KnowledgeBase} is consistent
	 * 
	 * @return The result
	 * @throws PelletClientException if there was an error during invocation
	 */
	public boolean consistency() throws PelletClientException {
		return new ConsistencyTask(this).execute();
	}

	/**
	 * Asynchronously checks if the {@link KnowledgeBase} is consistent
	 * 
	 * @param callback
	 *            The {@link Callback} to execute after the check is done
	 */
	public void consistency(Callback<Boolean> callback) {
		CallbackUtils.launchThread(callback, new ConsistencyTask(this));
	}

	private static class ConsistencyTask extends PelletServiceCallbackTask<Boolean> {

		protected ConsistencyTask(PelletService service) {
			super(service);
		}

		public Boolean execute() throws PelletClientException {

			Response response = RequestUtils.execute(service.getEndpoint(), service.getServer().getPreferredMethod(), 
							                         MIMETYPE);

			// Parse the Query result into a boolean
			try {
				TupleQueryResult resultSet = OpenRdfUtils.createResultSetFromSparqlXMLBlob(response.getContent());
				Value v = resultSet.next().getValue("Consistent");

				if (v instanceof Literal) {
					return ((Literal) v).booleanValue();
				}

				return false;
			}
			catch (Throwable e) {
				throw new PelletClientException("Problem parsing " + MIMETYPE + " response", e);
			}
		}
	}

}
