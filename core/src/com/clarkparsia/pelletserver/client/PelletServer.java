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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import com.clarkparsia.pelletserver.client.utils.PelletServerUtils;
import com.clarkparsia.pelletserver.client.utils.RequestUtils;
import com.clarkparsia.utils.web.Method;
import com.clarkparsia.utils.web.Response;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.MutableClassToInstanceMap;

/**
 * A Pellet Server instance
 * 
 * @author Pedro Oliveira
 * 
 */
public class PelletServer implements Iterable<KnowledgeBase> {

	/**
	 * A Map from name to {@link KnowledgeBase}s
	 */
	private Map<String, KnowledgeBase> kbs;

	/**
	 * The services offered by the root of this server
	 */
	private ClassToInstanceMap<PelletService> services;

	/**
	 * Info about the server (if available)
	 */
	private Map<String, String> info;

	/**
	 * The server {@link Endpoint}
	 */
	private Endpoint endpoint;

	/**
	 * The preferred HTTP {@link Method}
	 */
	private Method preferredMethod;

	public PelletServer(URL location) throws PelletClientException {
		this(location, Method.GET);
	}

	public PelletServer(URL location, Method preferredMethod) throws PelletClientException {
		this.endpoint = new Endpoint(location, preferredMethod);
		this.preferredMethod = preferredMethod;
		this.kbs = new HashMap<String, KnowledgeBase>();
		this.services = MutableClassToInstanceMap.create();
		this.info = Maps.newHashMap();
		discover();
	}

	/**
	 * Get {@link KnowledgeBase} by {@code name}
	 * 
	 * @param name the name of the KB to retrieve
	 * @return the KB with the given name, or null if it does not exist
	 */
	public KnowledgeBase getKnowledgeBase(String name) {
		return kbs.get(name);
	}

	/**
	 * Get all the {@link KnowledgeBase}s in the server
	 * 
	 * @return the list of kbs
	 */
	public Collection<KnowledgeBase> getKnowledgeBases() {
		return Collections.unmodifiableCollection(kbs.values());
	}

	/**
	 * Get the name of all the {@link KnowledgeBase}s in the server
	 * 
	 * @return the list of KB names
	 */
	public Set<String> getKnowledgeBaseNames() {
		return Collections.unmodifiableSet(kbs.keySet());
	}

	/**
	 * Get a {@link PelletService} from the server
	 * 
	 * @param <T> the type of the service
	 * @param cl
	 *            The Class of the {@link PelletService}
	 * @return The {@link PelletService} or {@code null} if the service is not available
	 */
	public <T extends PelletService> T getService(Class<T> cl) {
		return services.getInstance(cl);
	}

	/**
	 * Checks if the server provides the a certain {@link PelletService}
	 * 
	 * @param <T> the service type
	 * @param cl
	 *            The Class of the {@link PelletService}
	 * @return true if it has the service, false otherwise
	 */
	public <T extends PelletService> boolean hasService(Class<T> cl) {
		return services.containsKey(cl);
	}

	/**
	 * Get the server {@link Endpoint}
	 * 
	 * @return the endpoint
	 */
	public Endpoint getEndpoint() {
		return endpoint;
	}

	/**
	 * Get the prefered HTTP {@link Method} to use with this server
	 * 
	 * @return the preferred HTTP method
	 */
	public Method getPreferredMethod() {
		return preferredMethod;
	}

	/**
	 * Get information about the server
	 * 
	 * @return the server info
	 */
	public Map<String, String> getInfo() {
		return info;
	}

	/**
	 * Discover all the {@link PelletService}s and {@link KnowledgeBase}s in the server
	 * 
	 * @throws PelletClientException if there was an error during invocation
	 */
	private void discover() throws PelletClientException {

		Response response = RequestUtils.execute(endpoint, preferredMethod, PelletServerMimeTypes.JSON);

		Collection<KnowledgeBase> kbs = PelletServerUtils.parseServerRootForKBs(this, response.getContent());

		for (KnowledgeBase kb : kbs) {
			this.kbs.put(kb.getName(), kb);
		}

		Collection<PelletService> services = PelletServerUtils.parseServerRootForServices(this, response.getContent());

		for (PelletService service : services) {
			this.services.put(service.getClass(), service);
		}

		this.info.putAll(PelletServerUtils.parseServerInfo(this, response.getContent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<KnowledgeBase> iterator() {
		return Iterators.unmodifiableIterator(kbs.values().iterator());
	}
}
