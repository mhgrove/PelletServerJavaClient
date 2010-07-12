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

package com.clarkparsia.pelletserver.client.examples;

import java.net.MalformedURLException;
import java.net.URL;

import com.clarkparsia.pelletserver.client.KBPelletService;
import com.clarkparsia.pelletserver.client.KnowledgeBase;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.pelletserver.client.PelletServer;
import com.clarkparsia.pelletserver.client.PelletService;

/**
 * Simple example on how to create a {@link PelletServer} and explore the available {@link KnowledgeBase}s and
 * {@link PelletService}s
 * 
 * @author Pedro Oliveira
 * 
 */
public class ExploreServer {

	public static void main(String[] args) throws MalformedURLException, PelletClientException {

		// Create Pellet Server instance
		PelletServer server = new PelletServer(new URL("http://ps.clarkparsia.com/"));

		// Print server information
		System.out.println(server.getEndpoint());
		System.out.println(server.getInfo());

		// Get all the available Knowledge Bases
		for (KnowledgeBase kb : server) {
			System.out.println(kb.getName());

			// Get all the services available in this Knowledge Base
			for (KBPelletService service : kb.getServices()) {
				System.out.println("\t" + service.getName());
			}
		}
	}

}
