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
import java.util.Collection;
import java.util.List;

import javax.activation.MimeType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.URIImpl;


import com.clarkparsia.pelletserver.client.Callback;
import com.clarkparsia.pelletserver.client.Endpoint;
import com.clarkparsia.pelletserver.client.KnowledgeBase;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.pelletserver.client.PelletServerMimeTypes;
import com.clarkparsia.pelletserver.client.PelletService;
import com.clarkparsia.pelletserver.client.PelletServiceCallbackTask;
import com.clarkparsia.pelletserver.client.ServiceAnnotation;
import com.clarkparsia.pelletserver.client.utils.CallbackUtils;
import com.clarkparsia.pelletserver.client.utils.RequestUtils;
import com.clarkparsia.utils.web.Response;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The Search service executes a free text search in the {@link KnowledgeBase}
 * 
 * @author Pedro Oliveira
 * 
 */
@ServiceAnnotation("search")
public class Search extends AbstractKBPelletService {

	private static final MimeType MIMETYPE = PelletServerMimeTypes.JSON;

	/**
	 * Create a new Search service
	 * @param kb the PelletServer kb
	 * @param endpoint the Endpoint of th eKB
	 * @param mimetypes the mimetypes supported by the service
	 */
	public Search(KnowledgeBase kb, Endpoint endpoint, MimeType... mimetypes) {
		super(kb, endpoint, mimetypes);

		checkArgument(Iterables.any(this.mimetypes, PelletServerMimeTypes.getPredicate(MIMETYPE)), 
						            getName() + " service must support %s", MIMETYPE);
	}

	/**
	 * Search the {@link KnowledgeBase} for {@link Resource}s related to {@code text}
	 * 
	 * @param text
	 *            The {@link String} to search
	 * @return the results of the search
	 * @throws PelletClientException if there is an error while invoking the search servers
	 */
	public Collection<SearchResult> search(String text) throws PelletClientException {
		return new SearchTask(this, text).execute();
	}

	/**
	 * Asynchronously search the {@link KnowledgeBase} for {@link Resource}s related to {@code text}
	 * 
	 * @param text
	 *            The {@link String} to search
	 * @param callback
	 *            The {@link Callback} to execute after the search is done
	 */
	public void search(String text, Callback<Collection<SearchResult>> callback) {
		CallbackUtils.launchThread(callback, new SearchTask(this, text));
	}

	private static class SearchTask extends PelletServiceCallbackTask<Collection<SearchResult>> {

		private String text;

		protected SearchTask(PelletService service, String text) {
			super(service);
			this.text = text;
		}

		public Collection<SearchResult> execute() throws PelletClientException {
			URL url;

			try {
				String encodedQuery = URLEncoder.encode(text, "UTF-8");

				// TODO Use URI template library
				url = new URL(service.getEndpoint().getURL().toString().replaceAll("\\Q{?search}\\E", "?search=" 
								                                                   + encodedQuery)); 
			}
			catch (Exception e) {
				throw new PelletClientException("Problem creating query URL", e);
			}

			Response response = RequestUtils.execute(new Endpoint(url, service.getEndpoint().getHTTPMethods()), 
							                                      service.getServer().getPreferredMethod(), MIMETYPE);

			// Parse JSON response content into SearchResult objects
			try {
				List<SearchResult> searchResults = Lists.newArrayList();

				JSONArray results = new JSONArray(response.getContent());

				for (int i = 0; i < results.length(); i++) {
					JSONObject result = results.getJSONObject(i);

					JSONObject hit = result.getJSONObject("hit");

					Resource resource;
					if (hit.getString("type").equalsIgnoreCase("uri"))
						resource = new URIImpl(hit.getString("value"));
					else
						resource = new BNodeImpl(hit.getString("value"));

					searchResults.add(new SearchResult(resource, result.getDouble("score")));
				}

				return searchResults;
			}
			catch (JSONException e) {
				throw new PelletClientException("Problem parsing " + MIMETYPE + " content", e);
			}
		}
	}

	/**
	 * A Search Result is composed by a {@link org.openrdf.model.Resource Resource} and its score
	 * 
	 * @author Pedro Oliveira
	 * 
	 */
	public static class SearchResult {
		private Resource resource;
		private double score;

		public SearchResult(Resource resource, double score) {
			this.resource = resource;
			this.score = score;
		}

		public Resource getResource() {
			return resource;
		}

		public double getScore() {
			return score;
		}

		public String toString() {
			return resource + ": " + score;
		}
	}
}
