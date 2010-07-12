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

/**
 * A Knowledge Base available in a certain {@link PelletServer}
 * 
 * @author Pedro Oliveira
 * 
 */
public interface KnowledgeBase extends Iterable<KBPelletService> {

	/**
	 * Get a {@link KBPelletService} from the {@link KnowledgeBase}
	 * 
	 * @param <T> the service type
	 * @param cl
	 *            The Class of the {@link KBPelletService}
	 * @return The {@link KBPelletService} or {@code null} if the service is not available
	 */
	public <T extends KBPelletService> T getService(Class<T> cl);

	/**
	 * Checks if the {@link KnowledgeBase} provides the a certain {@link KBPelletService}
	 * 
	 * @param <T> the service type
	 * @param cl
	 *            The Class of the {@link KBPelletService}
	 * @return true if it has the service, false otherwise
	 */
	public <T extends KBPelletService> boolean hasService(Class<T> cl);

	/**
	 * Get the {@link URL} location of the {@link KnowledgeBase}
	 * 
	 * @return the location of the KB
	 */
	public URL getLocation();

	/**
	 * Get the name of the {@link KnowledgeBase}
	 * 
	 * @return the name of th eKB
	 */
	public String getName();

	/**
	 * Get the {@link PelletServer} associated with the {@link KnowledgeBase}
	 * 
	 * @return the parent PelletServer instance.
	 */
	public PelletServer getServer();

	/**
	 * Get all the {@link KBPelletService}s associated with the {@link KnowledgeBase}
	 * 
	 * @return the list of services provided
	 */
	public Collection<KBPelletService> getServices();
}
