package org.jahia.loganalyzer.services.taskexecutor;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.concurrent.Future;

/**
 * An interface to the task executor service
 */
public interface TaskExecutorService {

    <T> Future<T> submit(Task<T> task);

    List<Task<?>> getTasks();

    Object getNextFuture();

    boolean addListener(TaskListener taskListener);

    boolean removeListener(TaskListener taskListener);

    <T> void fireBeforeStarted(Task<T> task);

    <T> void fireAfterEnd(Task<T> task);

    <T> void firePourcentageChanged(Task<T> task);

}
