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
 * A {@link CallbackTask} associated with a certain {@link PelletService}
 * 
 * @author Pedro Oliveira
 * 
 * @param <T>
 */
public abstract class PelletServiceCallbackTask<T> implements CallbackTask<T> {

	protected PelletService service;

	protected PelletServiceCallbackTask(PelletService service) {
		this.service = service;
	}

}
