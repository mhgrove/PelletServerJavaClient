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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import com.google.common.base.Predicate;

/**
 * Common {@link MimeType}s used by {@link PelletServer}
 * 
 * @author Pedro Oliveira
 * 
 */
public abstract class PelletServerMimeTypes {

	public static MimeType JSON;
	public static MimeType RDFXML;
	public static MimeType TURTLE;
	public static MimeType HTML;

	public static MimeType SPARQL_XML;

	/**
	 * Get a {@link com.google.common.base.Predicate Predicate} that matches the given {@link MimeType}
	 * 
	 * @param type the mimetype to match
	 * @return a Predicate ot use for matching
	 */
	public static Predicate<MimeType> getPredicate(final MimeType type) {
		return new Predicate<MimeType>() {
			public boolean apply(MimeType arg0) {
				return arg0.match(type);
			}
		};
	}

	static {
		try {
			JSON = new MimeType("text/json");
			RDFXML = new MimeType("application/rdf+xml");
			TURTLE = new MimeType("text/turtle");
			HTML = new MimeType("text/html");
			SPARQL_XML = new MimeType("application/sparql-results+xml");
		}
		catch (MimeTypeParseException e) {
			e.printStackTrace();
		}
	}

}
