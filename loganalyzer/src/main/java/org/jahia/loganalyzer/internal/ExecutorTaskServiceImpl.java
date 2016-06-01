package org.jahia.loganalyzer.internal;

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

import org.jahia.loganalyzer.ExecutorTask;
import org.jahia.loganalyzer.ExecutorTaskListener;
import org.jahia.loganalyzer.ExecutorTaskService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by loom on 31.05.16.
 */
public class ExecutorTaskServiceImpl implements ExecutorTaskService {

    private ExecutorService executorService;
    private List<ExecutorTask<?>> executorTasks = new ArrayList<>();
    private Set<ExecutorTaskListener> executorTaskListeners = new LinkedHashSet<>();

    public ExecutorTaskServiceImpl() {
    }

    @Override
    public <T> Future<T> submit(ExecutorTask<T> executorTask) {
        if (executorService == null) {
            return null;
        }
        executorTask.setExecutorTaskService(this);
        Future<T> future = executorService.submit(executorTask);
        executorTask.setFuture(future);
        executorTasks.add(executorTask);
        return future;
    }

    public void start() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
    }

    public void stop() {
        if (executorService != null) {
            shutdownAndAwaitTermination(executorService);
        }
    }

    public List<ExecutorTask<?>> getExecutorTasks() {
        return executorTasks;
    }

    @Override
    public Object getNextFuture() {
        if (executorTasks.size() > 0) {
            Object result = null;
            try {
                ExecutorTask<?> task = executorTasks.get(0);
                if (task.getFuture() != null) {
                    result = task.getFuture().get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            executorTasks.remove(0);
            return result;
        }
        return null;
    }

    @Override
    public boolean addListener(ExecutorTaskListener executorTaskListener) {
        return executorTaskListeners.add(executorTaskListener);
    }

    @Override
    public boolean removeListener(ExecutorTaskListener executorTaskListener) {
        return executorTaskListeners.remove(executorTaskListener);
    }


    @Override
    public <T> void fireBeforeStarted(ExecutorTask<T> executorTask) {
        for (ExecutorTaskListener executorTaskListener : executorTaskListeners) {
            executorTaskListener.beforeStart(executorTask);
        }
    }

    @Override
    public <T> void fireAfterEnd(ExecutorTask<T> executorTask) {
        for (ExecutorTaskListener executorTaskListener : executorTaskListeners) {
            executorTaskListener.afterEnd(executorTask);
        }
        executorTasks.remove(executorTask);
        executorTask.setExecutorTaskService(null);
    }

    @Override
    public <T> void firePourcentageChanged(ExecutorTask<T> executorTask) {
        for (ExecutorTaskListener executorTaskListener : executorTaskListeners) {
            executorTaskListener.pourcentageChanged(executorTask);
        }
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
