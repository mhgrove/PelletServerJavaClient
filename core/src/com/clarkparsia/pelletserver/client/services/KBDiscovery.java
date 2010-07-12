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
import com.clarkparsia.pelletserver.client.KBPelletService;
import com.clarkparsia.pelletserver.client.KnowledgeBase;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.pelletserver.client.PelletServerMimeTypes;
import com.clarkparsia.pelletserver.client.PelletService;
import com.clarkparsia.pelletserver.client.PelletServiceCallbackTask;
import com.clarkparsia.pelletserver.client.ServiceAnnotation;
import com.clarkparsia.pelletserver.client.utils.CallbackUtils;
import com.clarkparsia.pelletserver.client.utils.PelletServerUtils;
import com.clarkparsia.pelletserver.client.utils.RequestUtils;
import com.clarkparsia.utils.web.Response;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The KBDiscovery service discovers all the available services in a {@link KnowledgeBase}
 * 
 * @author Pedro Oliveira
 * 
 */
@ServiceAnnotation("kb-discovery")
public class KBDiscovery extends AbstractKBPelletService {
	private static final MimeType MIMETYPE = PelletServerMimeTypes.JSON;

	public KBDiscovery(KnowledgeBase kb, Endpoint endpoint, MimeType... mimetypes) {
		super(kb, endpoint, mimetypes);

		checkArgument(Iterables.any(this.mimetypes, PelletServerMimeTypes.getPredicate(MIMETYPE)), 
						            getName() + " service must support %s", MIMETYPE);
	}

	/**
	 * Get all the the available services in the {@link KnowledgeBase}
	 * 
	 * @return Discovered {@link KBPelletService}s
	 * @throws PelletClientException if there is an error while doing the discover
	 */
	public Collection<KBPelletService> kbdiscovery() throws PelletClientException {
		return new KBDiscoveryTask(this).execute();
	}

	/**
	 * Asynchronously gets all the the available services in the {@link KnowledgeBase}
	 * 
	 * @param callback
	 *            The {@link Callback} to execute after the discovery is done
	 */
	public void kbdiscovery(Callback<Collection<KBPelletService>> callback) {
		CallbackUtils.launchThread(callback, new KBDiscoveryTask(this));
	}

	private static class KBDiscoveryTask extends PelletServiceCallbackTask<Collection<KBPelletService>> {

		protected KBDiscoveryTask(PelletService service) {
			super(service);
		}

		public Collection<KBPelletService> execute() throws PelletClientException {
			Response response = RequestUtils.execute(service.getEndpoint(), service.getServer().getPreferredMethod(), 
							                         MIMETYPE);
			return Lists.newArrayList(PelletServerUtils.parseKnowledgeBase(service.getServer(), response.getContent()));
		}
	}

}
