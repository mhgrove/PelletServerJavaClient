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

import java.net.URL;
import java.net.URLEncoder;

import javax.activation.MimeType;

import org.openrdf.model.URI;


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
 * The Query service executes a SPARQL query in the {@link KnowledgeBase}.<br>
 * Since different queries can return different result types (e.g., a {@code CONSTRUCT} query returns a
 * {@link org.openrdf.model.Graph Graph}, a {@code SELECT} query returns a {@link org.openrdf.query.TupleQueryResult
 * TupleQueryResult}), this service is parameterized by its returning type (i.e., users must know the type of their
 * query result).
 * 
 * @author Pedro Oliveira
 * 
 * @param <T>
 *            The Query result type
 */
@ServiceAnnotation("query")
public class Query<T> extends AbstractKBPelletService {

	private static final MimeType[] MIMETYPES = { PelletServerMimeTypes.SPARQL_XML, PelletServerMimeTypes.RDFXML };

	public Query(KnowledgeBase kb, Endpoint endpoint, MimeType... mimetypes) {
		super(kb, endpoint, mimetypes);

		for (MimeType mimetype : MIMETYPES) {
			checkArgument(Iterables.any(this.mimetypes, PelletServerMimeTypes.getPredicate(mimetype)), 
							            getName() + " service must support %s", mimetype);
		}
	}

	/**
	 * Executes the {@code query} in the {@link KnowledgeBase}
	 * 
	 * @param query
	 *            The query
	 * @return The query result
	 * @throws PelletClientException if there is an error while querying
	 */
	public T query(String query) throws PelletClientException {
		return query(query, null, null);
	}

	/**
	 * Asynchronously executes the {@code query} in the {@link KnowledgeBase}
	 * 
	 * @param query
	 *            The query
	 * @param callback
	 *            The {@link Callback} to execute after the query is done
	 */
	public void query(String query, Callback<T> callback) {
		query(query, null, null, callback);
	}

	/**
	 * Executes the {@code query} in the {@link KnowledgeBase}
	 * 
	 * @param query
	 *            The query
	 * @param namedGraph
	 *            The named graph (can be {@code null})
	 * @param defaultGraph
	 *            The default graph (can be {@code null})
	 * @return The query result
	 * @throws PelletClientException if there is an error while querying
	 */
	public T query(String query, URI namedGraph, URI defaultGraph) throws PelletClientException {
		return new QueryTask<T>(this, 
						        query, 
						        namedGraph != null ? namedGraph.stringValue() 
						                           : null, 
						        defaultGraph != null ? defaultGraph.stringValue()
						                             : null).execute();
	}

	/**
	 * Asynchronously executes the {@code query} in the {@link KnowledgeBase}
	 * 
	 * @param query
	 *            The query
	 * @param namedGraph
	 *            The named graph (can be {@code null})
	 * @param defaultGraph
	 *            The default graph (can be {@code null})
	 * @param callback
	 *            The {@link Callback} to execute after the query is done
	 */
	public void query(String query, URI namedGraph, URI defaultGraph, Callback<T> callback) {
		CallbackUtils.launchThread(callback, new QueryTask<T>(this, 
						query, 
						namedGraph != null ? namedGraph.stringValue() 
										   : null, 
						defaultGraph != null ? defaultGraph.stringValue() 
										     : null));
	}

	private static class QueryTask<T> extends PelletServiceCallbackTask<T> {

		private String query;
		private String namedGraph;
		private String defaultGraph;

		protected QueryTask(PelletService service, String query, String defaultGraph, String namedGraph) {
			super(service);
			this.query = query;
			this.namedGraph = namedGraph;
			this.defaultGraph = defaultGraph;
		}

		public T execute() throws PelletClientException {

			URL url = null;

			try {
				String encodedQuery = URLEncoder.encode(query, "UTF-8");
				String sUrl = service.getEndpoint().getURL().toString();
				String aUrl = sUrl.substring(0, sUrl.lastIndexOf('{')) + "?query=" + encodedQuery; // TODO Use URI
																								   // template library

				if (namedGraph != null) {
					aUrl += "&named-graph-uri=" + URLEncoder.encode(namedGraph, "UTF-8");
				}

				if (defaultGraph != null) {
					aUrl += "&default-graph-uri=" + URLEncoder.encode(defaultGraph, "UTF-8");
				}

				url = new URL(aUrl);

			}
			catch (Exception e) {
				throw new PelletClientException("Problem creating query URL", e);
			}

			Response response = RequestUtils.execute(new Endpoint(url, service.getEndpoint().getHTTPMethods()), 
							                                      service.getServer().getPreferredMethod(), MIMETYPES);

			Object result = null;
			try {
				result = OpenRdfUtils.createGraphFromRDFXMLBlob(response.getContent());
			}
			catch (Exception e1) {
				try {
					result = OpenRdfUtils.createResultSetFromSparqlXMLBlob(response.getContent());
				}
				catch (Exception e2) {
					throw new PelletClientException("Problem parsing request response", e2);
				}
			}

			try {
				return (T) result;
			}
			catch (Throwable t) {
				throw new PelletClientException("Problem returning result (wrong parameterized type?)", t);
			}
		}
	}

}
