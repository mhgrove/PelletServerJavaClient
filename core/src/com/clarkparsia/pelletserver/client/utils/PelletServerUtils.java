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

package com.clarkparsia.pelletserver.client.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarkparsia.pelletserver.client.Endpoint;
import com.clarkparsia.pelletserver.client.KBPelletService;
import com.clarkparsia.pelletserver.client.KnowledgeBase;
import com.clarkparsia.pelletserver.client.KnowledgeBaseImpl;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.pelletserver.client.PelletServer;
import com.clarkparsia.pelletserver.client.PelletService;
import com.clarkparsia.pelletserver.client.ServiceAnnotation;
import com.clarkparsia.utils.web.Method;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * <p>
 * Collection of utility methods for working with the client API
 * </p>
 */
public abstract class PelletServerUtils {

	private static final Logger log = Logger.getLogger(PelletServerUtils.class.getName());

	// TODO Add multimap with services
	private static Map<String, Class<? extends PelletService>> SERVER_SERVICES = Maps.newHashMap();
	private static Map<String, Class<? extends KBPelletService>> KB_SERVICES = Maps.newHashMap();

	static {
		// Scan classpath for Service implementations
		Set<Class<? extends PelletService>> services = ReflectionUtils.getImplementations(PelletService.class, 
						                                                                  ServiceAnnotation.class);
		for (Class<? extends PelletService> service : services) {
			if (org.reflections.ReflectionUtils.getAllSuperTypes(service).contains(KBPelletService.class)) {
				KB_SERVICES.put(service.getAnnotation(ServiceAnnotation.class).value(), 
								                      (Class<? extends KBPelletService>) service);
			}
			else {
				SERVER_SERVICES.put(service.getAnnotation(ServiceAnnotation.class).value(), service);
			}
		}
	}

	/**
	 * Get the {@link PelletService} registered with the given {@code name}
	 * 
	 * @param name
	 *            the name of the service
	 * @return The {@link PelletService} or {@code null} if there is no service registered to that {@code name}
	 */
	public static Class<? extends PelletService> getService(String name) {
		return SERVER_SERVICES.get(name);
	}

	/**
	 * Get the {@link KBPelletService} registered with the given {@code name}
	 * 
	 * @param name
	 *            the name of the service
	 * @return The {@link KBPelletService} or {@code null} if there is no service registered to that {@code name}
	 */
	public static Class<? extends KBPelletService> getKBService(String name) {
		return KB_SERVICES.get(name);
	}

	/**
	 * Parse {@link KnowledgeBase} from a HTTP request content
	 * 
	 * @param server
	 *            The {@link PelletServer}
	 * @param content
	 *            The content of the HTTP request
	 * @return the KB represented by the content
	 * @throws PelletClientException
	 *             if there was an error during invocation
	 */
	public static KnowledgeBase parseKnowledgeBase(PelletServer server, String content) throws PelletClientException {
		try {
			JSONObject kb = new JSONObject(content);
			return parseKnowledgeBase(server, kb);
		}
		catch (Exception e) {
			throw new PelletClientException("Problem parsing knowledge base from server", e);
		}
	}

	/**
	 * Parse all the {@link KnowledgeBase}s in the {@link PelletServer} root
	 * 
	 * @param server
	 *            The {@link PelletServer}
	 * @param content
	 *            The content of the HTTP request
	 * @return the kbs for the server
	 * @throws PelletClientException
	 *             if there was an error during invocation
	 */
	public static Collection<KnowledgeBase> parseServerRootForKBs(PelletServer server, String content) 
		throws PelletClientException {
		try {
			JSONObject root = new JSONObject(content);
			return parseKnowledgeBases(server, root.getJSONArray("knowledge-bases"));
		}
		catch (Exception e) {
			throw new PelletClientException("Problem parsing knowledge bases from server", e);
		}
	}

	/**
	 * Parse all the {@link PelletService}s in the {@link PelletServer} root
	 * 
	 * @param server
	 *            The {@link PelletServer}
	 * @param content
	 *            The content of the HTTP request
	 * @return the list of services
	 * @throws PelletClientException
	 *             if there was an error during invocation
	 */
	public static Collection<PelletService> parseServerRootForServices(PelletServer server, String content) 
		throws PelletClientException {
		List<PelletService> services = Lists.newArrayList();

		try {
			JSONObject root = new JSONObject(content);
			JSONArray rnames = root.names();

			for (int i = 0; i < rnames.length(); i++) {

				String serviceName = rnames.getString(i);

				if (!serviceName.equals("knowledge-bases") && !serviceName.equals("server-information")) {
					PelletService service = parsePelletService(server, root.getJSONObject(serviceName), serviceName);
					if (service != null)
						services.add(service);
				}
			}

		}
		catch (Exception e) {
			throw new PelletClientException("Problem parsing services from server", e);
		}

		return services;
	}

	/**
	 * Parse {@link PelletServer} server-information
	 * 
	 * @param server
	 *            the PelletServer instance
	 * @param content
	 *            the info from the server to be parsed
	 * @return the parsed information
	 * @throws PelletClientException
	 *             if there was an error during invocation
	 */
	public static Map<String, String> parseServerInfo(PelletServer server, String content) throws PelletClientException {
		try {
			JSONObject root = new JSONObject(content);

			Map<String, String> info = Maps.newHashMap();

			if (root.has("server-information")) {
				JSONObject obj = root.getJSONObject("server-information");
				JSONArray names = obj.names();

				for (int i = 0; i < names.length(); i++) {
					String key = names.getString(i);
					String value = obj.getString(key);
					info.put(key, value);
				}
			}

			return info;
		}
		catch (Exception e) {
			throw new PelletClientException("Problem parsing server information", e);
		}
	}

	/**
	 * Parse all the {@link KnowledgeBase}s in the given {@link JSONArray}
	 */
	private static Collection<KnowledgeBase> parseKnowledgeBases(PelletServer server, JSONArray kbs) 
		throws SecurityException, IllegalArgumentException, MalformedURLException, JSONException, 
		NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, 
		MimeTypeParseException, PelletClientException {

		List<KnowledgeBase> knowledgeBases = Lists.newArrayList();

		log.fine(kbs.length() + " kbs to parse :");

		for (int i = 0; i < kbs.length(); i++) {
			knowledgeBases.add(parseKnowledgeBase(server, kbs.getJSONObject(i)));
		}

		return knowledgeBases;
	}

	/**
	 * Parse a given {@link JSONObject} referent to a {@link KnowledgeBase}
	 */
	private static KnowledgeBase parseKnowledgeBase(PelletServer server, JSONObject kb) throws JSONException, 
		SecurityException, IllegalArgumentException, MalformedURLException, NoSuchMethodException,
	    InstantiationException, IllegalAccessException, InvocationTargetException, MimeTypeParseException, 
	    PelletClientException {
		String name = kb.getString("name");

		log.fine("Parsing kb " + name);

		KnowledgeBaseImpl knowledgeBase = new KnowledgeBaseImpl(server, null, name);

		JSONObject services = kb.getJSONObject("kb-services");

		JSONArray snames = services.names();
		for (int i = 0; i < snames.length(); i++) {

			String serviceName = snames.getString(i);
			JSONObject service = services.getJSONObject(serviceName);

			KBPelletService kbservice = parseKBPelletService(knowledgeBase, service, serviceName);

			if (kbservice != null) {
				knowledgeBase.addService(kbservice);
			}
		}

		return knowledgeBase;
	}

	/**
	 * Parse a given {@link JSONObject} referent to a {@link KBPelletService}
	 */
	private static KBPelletService parseKBPelletService(KnowledgeBase kb, JSONObject service, String name) 
		throws SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException,
		InstantiationException, IllegalAccessException, InvocationTargetException, JSONException, 
		MimeTypeParseException, PelletClientException {

		Class<? extends KBPelletService> ps = getKBService(name);

		if (ps != null) {
			Constructor<? extends KBPelletService> constructor = ps.getConstructor(KnowledgeBase.class, Endpoint.class, MimeType[].class);
			return constructor.newInstance(kb, parseEndpoint(service), parseMimeTypes(service));
		}

		log.info("Unable to create KB service " + name);

		return null;
	}

	/**
	 * Parse a given {@link JSONObject} referent to a {@link PelletService}
	 */
	private static PelletService parsePelletService(PelletServer server, JSONObject service, String name) 
		throws SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException,
	    InstantiationException, IllegalAccessException, InvocationTargetException, JSONException, 
	    MimeTypeParseException, PelletClientException {

		Class<? extends PelletService> ps = getService(name);

		if (ps != null) {
			Constructor<? extends PelletService> constructor = ps.getConstructor(PelletServer.class, Endpoint.class, MimeType[].class);
			return constructor.newInstance(server, parseEndpoint(service), parseMimeTypes(service));
		}

		log.info("Unable to create service " + name);

		return null;
	}

	/**
	 * Parse the {@link MimeType}s from a services' {@link JSONObject}
	 */
	private static MimeType[] parseMimeTypes(JSONObject service) throws JSONException, MimeTypeParseException {
		JSONArray mimetypes = service.getJSONArray("response-mimetype");

		MimeType[] res = new MimeType[mimetypes.length()];

		for (int i = 0; i < mimetypes.length(); i++) {
			res[i] = new MimeType(mimetypes.getString(i));
		}

		return res;
	}

	/**
	 * Parse {@link Endpoint} info from a services' {@link JSONObject}
	 */
	private static Endpoint parseEndpoint(JSONObject service) throws JSONException, MalformedURLException {
		JSONObject endpoint = service.getJSONObject("endpoint");

		URL url = new URL(endpoint.getString("url"));
		List<Method> methods = Lists.newArrayList();

		JSONArray mts = endpoint.getJSONArray("http-methods");
		for (int i = 0; i < mts.length(); i++) {
			methods.add(Method.valueOf(mts.getString(i)));
		}

		return new Endpoint(url, methods);
	}

}
