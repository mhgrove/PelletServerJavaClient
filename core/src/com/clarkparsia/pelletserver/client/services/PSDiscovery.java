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

import java.util.Collection;

import javax.activation.MimeType;


import com.clarkparsia.pelletserver.client.Callback;
import com.clarkparsia.pelletserver.client.Endpoint;
import com.clarkparsia.pelletserver.client.KnowledgeBase;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.pelletserver.client.PelletServer;
import com.clarkparsia.pelletserver.client.PelletServerMimeTypes;
import com.clarkparsia.pelletserver.client.PelletService;
import com.clarkparsia.pelletserver.client.PelletServiceCallbackTask;
import com.clarkparsia.pelletserver.client.ServiceAnnotation;
import com.clarkparsia.pelletserver.client.utils.CallbackUtils;
import com.clarkparsia.pelletserver.client.utils.PelletServerUtils;
import com.clarkparsia.pelletserver.client.utils.RequestUtils;
import com.clarkparsia.utils.web.Response;
import com.google.common.collect.Iterables;

/**
 * The PSDiscovery service is used to discover all the {@link KnowledgeBase}s available in a server
 * 
 * @author Pedro Oliveira
 * 
 */
@ServiceAnnotation("ps-discovery")
public class PSDiscovery extends AbstractPelletService {

	private static final MimeType MIMETYPE = PelletServerMimeTypes.JSON;

	public PSDiscovery(PelletServer server, Endpoint endpoint, MimeType... mimetypes) {
		super(server, endpoint, mimetypes);

		checkArgument(Iterables.any(this.mimetypes, PelletServerMimeTypes.getPredicate(MIMETYPE)), 
						            getName() + " service must support %s", MIMETYPE);
	}

	/**
	 * Get all the {@link KnowledgeBase}s in the server
	 * 
	 * @return Discovered {@link KnowledgeBase}s
	 * @throws PelletClientException if there is an error while doing the discovery
	 */
	public Collection<KnowledgeBase> psdiscovery() throws PelletClientException {
		return new PSDiscoveryTask(this).execute();
	}

	/**
	 * Asynchronously gets all the {@link KnowledgeBase}s in the server
	 * 
	 * @param callback
	 *            The {@link Callback} to execute after the discovery is done
	 */
	public void psdiscovery(Callback<Collection<KnowledgeBase>> callback) {
		CallbackUtils.launchThread(callback, new PSDiscoveryTask(this));
	}

	private static class PSDiscoveryTask extends PelletServiceCallbackTask<Collection<KnowledgeBase>> {

		protected PSDiscoveryTask(PelletService service) {
			super(service);
		}

		public Collection<KnowledgeBase> execute() throws PelletClientException {
			Response response = RequestUtils.execute(service.getEndpoint(), service.getServer().getPreferredMethod(), 
							                         MIMETYPE);
			return PelletServerUtils.parseServerRootForKBs(service.getServer(), response.getContent());
		}
	}

}
