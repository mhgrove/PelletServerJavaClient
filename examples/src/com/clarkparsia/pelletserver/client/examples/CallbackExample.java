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

import com.clarkparsia.pelletserver.client.Callback;
import com.clarkparsia.pelletserver.client.KnowledgeBase;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.pelletserver.client.PelletServer;
import com.clarkparsia.pelletserver.client.services.Consistency;

/**
 * Simple example on how to use {@link Callback}s
 * 
 * @author Pedro Oliveira
 * 
 */
public class CallbackExample {

	public static void main(String[] args) throws MalformedURLException, PelletClientException {

		// Create Pellet Server instance
		PelletServer server = new PelletServer(new URL("http://ps.clarkparsia.com/"));

		// Get all the available Knowledge Bases
		for (final KnowledgeBase kb : server) {

			// Get the Consistency service
			Consistency service = kb.getService(Consistency.class);

			// Print the result
			if (service != null) {
				
				//Call the consistency service with a Callback
				service.consistency(new Callback<Boolean>() {

					public void failure(PelletClientException exception) {
	                    exception.printStackTrace();
					}

					public void success(Boolean value) {
						if (value) {
							System.out.println(kb.getName() + " is consistent!");
						}
						else {
							System.out.println(kb.getName() + " is inconsistent...");
						}
					}
				});
				
				//The callback will be executed when the task is done, so we can continue to do our work
			}
		}
	}
}
