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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.openrdf.model.Graph;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.clarkparsia.openrdf.ExtGraph;
import com.clarkparsia.openrdf.query.results.SparqlXmlResultSetParser;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;

/**
 * Utilities related to Sesame
 * 
 * @author Pedro Oliveira
 * 
 */
public abstract class OpenRdfUtils {

	/**
	 * Creates a {@link org.openrdf.model.Graph Graph} from RDF/XML content
	 * 
	 * @param blob
	 *            The RDF/XML content
	 * @return
	 * @throws RDFParseException
	 * @throws IOException
	 */
	public static Graph createGraphFromRDFXMLBlob(String blob) throws RDFParseException, IOException {
		InputStream is = new ByteArrayInputStream(blob.getBytes("UTF-8"));

		ExtGraph graph = new ExtGraph();
		graph.read(is, RDFFormat.RDFXML);

		return graph;
	}

	/**
	 * Creates a {@link org.openrdf.query.TupleQueryResult TupleQueryResult} from SPARQL/XML content
	 * 
	 * @param blob
	 *            The SPARQL/XML content
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static TupleQueryResult createResultSetFromSparqlXMLBlob(String blob) throws UnsupportedEncodingException, 
		SAXException, IOException {
		SparqlXmlResultSetParser ch = new SparqlXmlResultSetParser();
		SAXParser p = new SAXParser();

		p.setContentHandler(ch);
		p.parse(new InputSource(new ByteArrayInputStream(blob.getBytes("UTF-8"))));

		return ch.tupleResult();
	}
}
