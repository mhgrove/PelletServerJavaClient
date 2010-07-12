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
import java.util.Iterator;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.MutableClassToInstanceMap;

/**
 * The base implementation of a {@link KnowledgeBase}
 * 
 * @author Pedro Oliveira
 *
 */
public class KnowledgeBaseImpl implements KnowledgeBase {

	/**
	 * A Map between {@link KBPelletService} classes and their implementations
	 */
	private ClassToInstanceMap<KBPelletService> services;

	/**
	 * The location of the {@link KnowledgeBase}
	 */
	private URL location;

	/**
	 * The name of the {@link KnowledgeBase}
	 */
	private String name;

	/**
	 * The {@link PelletServer} associated with the {@link KnowledgeBase}
	 */
	private PelletServer server;

	public KnowledgeBaseImpl(PelletServer server, URL location, String name) {
		this.server = server;
		this.location = location;
		this.name = name;
		this.services = MutableClassToInstanceMap.create();
	}

	/**
	 * Add a {@link KBPelletService} to the {@link KnowledgeBase}. If there is already another {@link KBPelletService}
	 * of the same type in {@link KnowledgeBase}, the previous {@link KBPelletService} is replaced.
	 * 
	 * @param service the service to add
	 */
	public void addService(KBPelletService service) {
		services.put(service.getClass(), service);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.java.KnowledgeBase#getService(java.lang.Class)
	 */
	public <T extends KBPelletService> T getService(Class<T> cl) {
		return services.getInstance(cl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.java.KnowledgeBase#hasService(java.lang.Class)
	 */
	public <T extends KBPelletService> boolean hasService(Class<T> cl) {
		return services.containsKey(cl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.java.KnowledgeBase#getLocation()
	 */
	public URL getLocation() {
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.java.KnowledgeBase#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.java.KnowledgeBase#getServer()
	 */
	public PelletServer getServer() {
		return server;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.java.KnowledgeBase#getServices()
	 */
	public Collection<KBPelletService> getServices() {
		return Collections.unmodifiableCollection(services.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<KBPelletService> iterator() {
		return Iterators.unmodifiableIterator(services.values().iterator());
	}
}
