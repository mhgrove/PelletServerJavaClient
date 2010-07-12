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

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;


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
 * The Explain service explains the results of a SPARQL query to the {@link KnowledgeBase}
 * 
 * @author Pedro Oliveira
 * 
 */
@ServiceAnnotation("explain")
public class Explain extends AbstractKBPelletService {

	private static final MimeType MIMETYPE = PelletServerMimeTypes.RDFXML;

	public Explain(KnowledgeBase kb, Endpoint endpoint, MimeType... mimetypes) {
		super(kb, endpoint, mimetypes);

		checkArgument(Iterables.any(this.mimetypes, PelletServerMimeTypes.getPredicate(MIMETYPE)), getName() + " service must support %s", MIMETYPE);
	}

	/**
	 * Explains a SPARQL query
	 * 
	 * @param query
	 *            The SPARQL query to explain
	 * @return The explanation {@link org.openrdf.model.Graph Graph}
	 * @throws PelletClientException if there is an error while querying
	 */
	public Graph query(String query) throws PelletClientException {
		return new ExplainQueryTask(this, query).execute();
	}

	/**
	 * Asynchronously explains a SPARQL query
	 * 
	 * @param query
	 *            The SPARQL query to explain
	 * @param callback
	 *            The {@link Callback} to execute after the explanation is done
	 */
	public void query(String query, Callback<Graph> callback) {
		CallbackUtils.launchThread(callback, new ExplainQueryTask(this, query));
	}

	/**
	 * Explains why {@code subclass} is rdfs:subclassOf {@code superclass}
	 * 
	 * @param subclass
	 *            The sub class
	 * @param superclass
	 *            The super class
	 * @return The explanation {@link org.openrdf.model.Graph Graph}
	 * @throws PelletClientException if there is an error while querying
	 */
	public Graph subclass(Resource subclass, Resource superclass) throws PelletClientException {
		String query = selectQuery(subclass.stringValue(), RDFS.SUBCLASSOF.stringValue(), superclass.stringValue());
		return query(query);
	}

	/**
	 * Asynchronously explains why {@code subclass} is rdfs:subclassOf {@code superclass}
	 * 
	 * @param subclass
	 *            The sub class
	 * @param superclass
	 *            The super class
	 * @param callback
	 *            The {@link Callback} to execute after the explanation is done
	 */
	public void subclass(Resource subclass, Resource superclass, Callback<Graph> callback) {
		String query = selectQuery(subclass.stringValue(), RDFS.SUBCLASSOF.stringValue(), superclass.stringValue());
		query(query, callback);
	}

	/**
	 * Explains why class {@code cl} is unsatisfiable
	 * 
	 * @param cl
	 *            The class to check for unsatisfiability
	 * @return The explanation {@link org.openrdf.model.Graph Graph}
	 * @throws PelletClientException
	 *             If there is an error or {@code cl} is not unsatisfiable
	 */
	public Graph unsat(Resource cl) throws PelletClientException {
		String query = selectQuery(cl.stringValue(), RDFS.SUBCLASSOF.stringValue(), "http://www.w3.org/2002/07/owl#Nothing");
		return query(query);
	}

	/**
	 * Asynchronously explains why class {@code cl} is unsatisfiable
	 * 
	 * @param cl
	 *            The class to check for unsatisfiability
	 * @param callback
	 *            The {@link Callback} to execute after the explanation is done
	 */
	public void unsat(Resource cl, Callback<Graph> callback) {
		String query = selectQuery(cl.stringValue(), RDFS.SUBCLASSOF.stringValue(), "http://www.w3.org/2002/07/owl#Nothing");
		query(query, callback);
	}

	/**
	 * Explains why the {@link KnowledgeBase} is inconsistent
	 * 
	 * @return The explanation {@link org.openrdf.model.Graph Graph}
	 * @throws PelletClientException
	 *             If there is an error or the {@link KnowledgeBase} is not inconsistent
	 */
	public Graph inconsistent() throws PelletClientException {
		return query("");
	}

	/**
	 * Asynchronously explains why the {@link KnowledgeBase} is inconsistent
	 * 
	 * @param callback
	 *            The {@link Callback} to execute after the explanation is done
	 */
	public void inconsistent(Callback<Graph> callback) {
		query("", callback);
	}

	/**
	 * Explains the triple ({@code subject}, {@code predicate}, {@code object} )
	 * 
	 * @param subject
	 *            The subject
	 * @param predicate
	 *            The predicate
	 * @param object
	 *            The object
	 * @return The explanation {@link org.openrdf.model.Graph Graph}
	 * @throws PelletClientException if there is an error while querying
	 */
	public Graph property(Resource subject, URI predicate, Resource object) throws PelletClientException {
		String query = selectQuery(subject.stringValue(), predicate.stringValue(), object.stringValue());
		return query(query);
	}

	/**
	 * Asynchronously explains the triple ({@code subject}, {@code predicate}, {@code object} )
	 * 
	 * @param subject
	 *            The subject
	 * @param predicate
	 *            The predicate
	 * @param object
	 *            The object
	 * @param callback
	 *            The {@link Callback} to execute after the explanation is done
	 */
	public void property(Resource subject, URI predicate, Resource object, Callback<Graph> callback) {
		String query = selectQuery(subject.stringValue(), predicate.stringValue(), object.stringValue());
		query(query, callback);
	}

	/**
	 * Explains why {@code instance} is an instance of {@code cl}
	 * 
	 * @param instance
	 *            The instance
	 * @param cl
	 *            The class
	 * @return The explanation {@link org.openrdf.model.Graph Graph}
	 * @throws PelletClientException
	 *             If there is an error or the {@code instance} is not an instance of {@code cl}
	 */
	public Graph instance(Resource instance, Resource cl) throws PelletClientException {
		String query = selectQuery(instance.toString(), RDF.TYPE.stringValue(), cl.stringValue());
		return query(query);
	}

	/**
	 * Asynchronously explains why {@code instance} is an instance of {@code cl}
	 * 
	 * @param instance
	 *            The instance
	 * @param cl
	 *            The class
	 * @param callback
	 *            The {@link Callback} to execute after the explanation is done
	 */
	public void instance(Resource instance, Resource cl, Callback<Graph> callback) {
		String query = selectQuery(instance.toString(), RDF.TYPE.stringValue(), cl.stringValue());
		query(query, callback);
	}

	/**
	 * Performs a SPARQL {@code SELECT} query with the provided {@code subject}, {@code predicate}, and {@code object}
	 */
	private static String selectQuery(String subject, String predicate, String object) {
		return String.format("SELECT * WHERE { <%s> <%s> <%s> }", subject, predicate, object);
	}

	private static class ExplainQueryTask extends PelletServiceCallbackTask<Graph> {

		private String query;

		protected ExplainQueryTask(PelletService service, String query) {
			super(service);
			this.query = query;
		}

		public Graph execute() throws PelletClientException {

			URL url = null;

			try {
				String encodedQuery = URLEncoder.encode(query, "UTF-8");
				url = new URL(service.getEndpoint().getURL().toString().replaceAll("\\Q{?query}\\E", "?query=" 
								                                                   + encodedQuery));
			}
			catch (Exception e) {
				throw new PelletClientException("Problem creating query URL", e);
			}

			Response response = RequestUtils.execute(new Endpoint(url, service.getEndpoint().getHTTPMethods()), 
							                                      service.getServer().getPreferredMethod(), MIMETYPE);

			try {
				return OpenRdfUtils.createGraphFromRDFXMLBlob(response.getContent());
			}
			catch (Exception e) {
				throw new PelletClientException("Problem parsing " + MIMETYPE + " response", e);
			}
		}
	}

}
