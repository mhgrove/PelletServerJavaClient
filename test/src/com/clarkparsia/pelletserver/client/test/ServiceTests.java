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

package com.clarkparsia.pelletserver.client.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.TupleQueryResult;

import com.clarkparsia.pelletserver.client.Callback;
import com.clarkparsia.pelletserver.client.KBPelletService;
import com.clarkparsia.pelletserver.client.KnowledgeBase;
import com.clarkparsia.pelletserver.client.PelletClientException;
import com.clarkparsia.pelletserver.client.PelletServer;
import com.clarkparsia.pelletserver.client.PelletService;
import com.clarkparsia.pelletserver.client.services.Classify;
import com.clarkparsia.pelletserver.client.services.Consistency;
import com.clarkparsia.pelletserver.client.services.Explain;
import com.clarkparsia.pelletserver.client.services.KBDiscovery;
import com.clarkparsia.pelletserver.client.services.PSDiscovery;
import com.clarkparsia.pelletserver.client.services.Query;
import com.clarkparsia.pelletserver.client.services.Realize;
import com.clarkparsia.pelletserver.client.services.Search;
import com.clarkparsia.pelletserver.client.services.Search.SearchResult;


/**
 * {@link PelletService} unit {@link Test}s
 * @author Pedro Oliveira
 *
 */
public class ServiceTests {

	private static PelletServer server;
	private static KnowledgeBase wine;
	private static KnowledgeBase galen;

	@BeforeClass
	public static void launchServer() throws Exception {
		server = new PelletServer(new URL("http://ps.clarkparsia.com/"));

		wine = server.getKnowledgeBase("wine");
		galen = server.getKnowledgeBase("galen");
	}

	@Test
	public void classify() throws PelletClientException {

		Classify classify = wine.getService(Classify.class);
		assertNotNull(classify);

		// Direct call
		Graph g = classify.classify();
		assertNotNull(g);
		assertFalse(g.size() == 0);

		// Callback
		classify.classify(new TestCallback<Graph>());
	}

	@Test
	public void consistency() throws PelletClientException {

		Consistency consistency = wine.getService(Consistency.class);
		assertNotNull(consistency);

		// Direct call
		boolean g = consistency.consistency();
		assertTrue(g);

		// Callback
		consistency.consistency(new TestCallback<Boolean>());
	}

	@Test
	public void realize() throws PelletClientException {

		Realize realize = wine.getService(Realize.class);
		assertNotNull(realize);

		// Direct call
		Graph g = realize.realize();
		assertNotNull(g);
		assertFalse(g.size() == 0);

		// Callback
		realize.realize(new TestCallback<Graph>());
	}

	@Test
	public void psdiscovery() throws PelletClientException {

		PSDiscovery discovery = server.getService(PSDiscovery.class);
		assertNotNull(discovery);

		// Direct call
		Collection<KnowledgeBase> g = discovery.psdiscovery();
		assertNotNull(g);
		assertFalse(g.size() == 0);

		// Callback
		discovery.psdiscovery(new TestCallback<Collection<KnowledgeBase>>());
	}

	@Test
	public void kbdiscovery() throws PelletClientException {

		KBDiscovery discovery = wine.getService(KBDiscovery.class);
		assertNotNull(discovery);

		// Direct call
		Collection<KBPelletService> g = discovery.kbdiscovery();
		assertNotNull(g);
		assertFalse(g.size() == 0);

		// Callback
		discovery.kbdiscovery(new TestCallback<Collection<KBPelletService>>());
	}

	@Test
	public void query() throws PelletClientException {

		String selectQuery = "SELECT * where { ?s <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?o . }";
		String constructQuery = "CONSTRUCT {?s <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?o} where { ?s <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?o . }";

		// SELECT
		Query<TupleQueryResult> select = wine.getService(Query.class);
		assertNotNull(select);

		// Direct call
		TupleQueryResult t = select.query(selectQuery);
		assertNotNull(t);
		assertFalse(t.getBindingNames().size() == 0);

		t = select.query(selectQuery, null, null);
		assertNotNull(t);
		assertFalse(t.getBindingNames().size() == 0);

		// Callback
		select.query(selectQuery, new TestCallback<TupleQueryResult>());
		select.query(selectQuery, null, null, new TestCallback<TupleQueryResult>());

		// CONSTRUCT
		Query<Graph> construct = wine.getService(Query.class);
		assertNotNull(construct);

		// Direct call
		Graph g = construct.query(constructQuery);
		assertNotNull(g);
		assertFalse(g.size() == 0);

		g = construct.query(constructQuery, null, null);
		assertNotNull(g);
		assertFalse(g.size() == 0);

		// Callback
		construct.query(constructQuery, new TestCallback<Graph>());
		construct.query(constructQuery, null, null, new TestCallback<Graph>());
	}

	@Test
	public void explain() throws PelletClientException {
		Resource sub = new URIImpl("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Port");
		Resource sup = new URIImpl("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Wine");
		Resource inst = new URIImpl("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#TaylorPort");

		String query = "SELECT * where { <" + sub + "> <" + RDFS.SUBCLASSOF + "> <" + sup + "> . }";

		Explain explain = wine.getService(Explain.class);
		assertNotNull(explain);

		// Direct call
		Graph g = explain.query(query);
		assertNotNull(g);
		assertFalse(g.size() == 0);

		g = explain.property(sub, RDFS.SUBCLASSOF, sup);
		assertNotNull(g);
		assertFalse(g.size() == 0);

		g = explain.subclass(sub, sup);
		assertNotNull(g);
		assertFalse(g.size() == 0);

		g = explain.instance(inst, sup);
		assertNotNull(g);
		assertFalse(g.size() == 0);

		try {
			explain.inconsistent();
			fail("Ontology shouldn't be inconsistent!");
		}
		catch (PelletClientException e) {
			// ignore
		}

		try {
			explain.unsat(sub);
			fail(sub + " shouldn't be unsatisfiable!");
		}
		catch (PelletClientException e) {
			// ignore
		}

		// Callback
		explain.query(query, new TestCallback<Graph>());
		explain.property(sub, RDFS.SUBCLASSOF, sup, new TestCallback<Graph>());
		explain.subclass(sub, sup, new TestCallback<Graph>());
		explain.instance(inst, sup, new TestCallback<Graph>());

		explain.inconsistent(new FailTestCallback<Graph>());
		explain.unsat(sub, new FailTestCallback<Graph>());

	}

	@Test
	public void search() throws PelletClientException {

		Search search = galen.getService(Search.class);
		assertNotNull(search);

		// Direct call
		Collection<SearchResult> j = search.search("galen");
		assertNotNull(j);
		assertFalse(j.size() == 0);

		// Callback
		search.search("galen", new TestCallback<Collection<SearchResult>>());
	}

	// TODO probably implement other way to see if thread fails (it can fail silently or similar, so use Future or
	// similar)
	private static class TestCallback<T> implements Callback<T> {

		public void failure(PelletClientException exception) {
			fail();
		}

		public void success(T value) {
			assertNotNull(value);
		}
	}

	private static class FailTestCallback<T> implements Callback<T> {

		public void failure(PelletClientException exception) {

		}

		public void success(T value) {
			fail();
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ServiceTests.class);
	}

}
