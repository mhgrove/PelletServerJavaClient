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

/**
 * A {@link PelletService} that is tied to a particular {@link KnowledgeBase}
 * 
 * @author Pedro Oliveira
 * 
 */
public interface KBPelletService extends PelletService {

	/**
	 * The {@link KnowledgeBase}
	 * 
	 * @return the KB for the service
	 */
	public KnowledgeBase getKnowledgeBase();
}
