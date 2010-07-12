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
 * A {@link Thread} that executes a {@link CallbackTask} and calls a {@link Callback} accordingly to the results of the
 * {@link CallbackTask}
 * 
 * @author Pedro Oliveira
 * 
 * @param <T> the type returned from the callback
 */
public class CallbackThread<T> implements Runnable {

	/**
	 * The callback to notify of task success or failure
	 */
	private Callback<T> callback;

	/**
	 * The task to execute
	 */
	private CallbackTask<T> task;

	/**
	 * Create a new CallbackThread
	 * @param callback the callback to notify
	 * @param task the task to execute
	 */
	public CallbackThread(Callback<T> callback, CallbackTask<T> task) {
		this.callback = callback;
		this.task = task;
	}

	/**
	 * @inheritDoc
	 */
	public void run() {

		try {
			callback.success(task.execute());
		}
		catch (PelletClientException e) {
			callback.failure(e);
		}
	}

}
