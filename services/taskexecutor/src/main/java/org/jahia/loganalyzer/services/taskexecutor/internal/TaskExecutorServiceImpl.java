package org.jahia.loganalyzer.services.taskexecutor.internal;

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

import org.jahia.loganalyzer.services.taskexecutor.Task;
import org.jahia.loganalyzer.services.taskexecutor.TaskExecutorService;
import org.jahia.loganalyzer.services.taskexecutor.TaskListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * An implementation of the TaskExecutorService interface that uses single threaded JDK ExecutorService
 */
public class TaskExecutorServiceImpl implements TaskExecutorService {

    private ExecutorService executorService;
    private List<Task<?>> tasks = new ArrayList<>();
    private Set<TaskListener> taskListeners = new LinkedHashSet<>();

    public TaskExecutorServiceImpl() {
    }

    @Override
    public <T> Future<T> submit(Task<T> task) {
        if (executorService == null) {
            return null;
        }
        task.setTaskExecutorService(this);
        Future<T> future = executorService.submit(task);
        task.setFuture(future);
        tasks.add(task);
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

    public List<Task<?>> getTasks() {
        return tasks;
    }

    @Override
    public Object getNextFuture() {
        if (tasks.size() > 0) {
            Object result = null;
            try {
                Task<?> task = tasks.get(0);
                if (task.getFuture() != null) {
                    result = task.getFuture().get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            tasks.remove(0);
            return result;
        }
        return null;
    }

    @Override
    public boolean addListener(TaskListener taskListener) {
        return taskListeners.add(taskListener);
    }

    @Override
    public boolean removeListener(TaskListener taskListener) {
        return taskListeners.remove(taskListener);
    }


    @Override
    public <T> void fireBeforeStarted(Task<T> task) {
        for (TaskListener taskListener : taskListeners) {
            taskListener.beforeStart(task);
        }
    }

    @Override
    public <T> void fireAfterEnd(Task<T> task) {
        for (TaskListener taskListener : taskListeners) {
            taskListener.afterEnd(task);
        }
        tasks.remove(task);
        task.setTaskExecutorService(null);
    }

    @Override
    public <T> void firePourcentageChanged(Task<T> task) {
        for (TaskListener taskListener : taskListeners) {
            taskListener.pourcentageChanged(task);
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
