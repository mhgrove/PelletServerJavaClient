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

import java.util.concurrent.Executor;

import com.clarkparsia.pelletserver.client.Callback;
import com.clarkparsia.pelletserver.client.CallbackTask;
import com.clarkparsia.pelletserver.client.CallbackThread;


/**
 * Utilities related to {@link Callback}s
 * 
 * @author Pedro Oliveira
 * 
 */
public abstract class CallbackUtils {

	/**
	 * Default {@link Thread} executor
	 */
	public static Executor EXECUTOR = new Executor() {
		public void execute(Runnable command) {
			new Thread(command).start();
		}
	};

	/**
	 * Launches a {@link Thread} that will execute the {@link CallbackTask} and call the {@link Callback} accordingly to the
	 * results
	 * 
	 * @param <T> the type returned from the ballback
	 * @param callback
	 *            The {@link Callback} to save the results
	 * @param task
	 *            The {@link CallbackTask} to execute
	 */
	public static <T> void launchThread(Callback<T> callback, CallbackTask<T> task) {
		EXECUTOR.execute(new CallbackThread<T>(callback, task));
	}

}
