package org.jahia.loganalyzer;

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
 * An interface to the command executor service
 */
public interface ExecutorTaskService {

    <T> Future<T> submit(ExecutorTask<T> executorTask);

    List<ExecutorTask<?>> getExecutorTasks();

    Object getNextFuture();

    boolean addListener(ExecutorTaskListener executorTaskListener);

    boolean removeListener(ExecutorTaskListener executorTaskListener);

    <T> void fireBeforeStarted(ExecutorTask<T> executorTask);

    <T> void fireAfterEnd(ExecutorTask<T> executorTask);

    <T> void firePourcentageChanged(ExecutorTask<T> executorTask);

}
