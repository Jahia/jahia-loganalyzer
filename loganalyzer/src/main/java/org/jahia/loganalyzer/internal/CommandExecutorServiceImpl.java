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

import org.jahia.loganalyzer.CommandExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by loom on 31.05.16.
 */
public class CommandExecutorServiceImpl implements CommandExecutorService {

    private ExecutorService executorService;
    private List<Future<? extends Object>> futures = new ArrayList<>();

    public CommandExecutorServiceImpl() {
    }

    @Override
    public Future<? extends Object> submit(Callable<? extends Object> task) {
        if (executorService == null) {
            return null;
        }
        Future<? extends Object> future = executorService.submit(task);
        futures.add(future);
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

    @Override
    public List<Future<? extends Object>> getFutures() {
        return futures;
    }

    @Override
    public Object getNextFuture() {
        if (futures.size() > 0) {
            Object result = null;
            try {
                result = futures.get(0).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            futures.remove(0);
            return result;
        }
        return null;
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
