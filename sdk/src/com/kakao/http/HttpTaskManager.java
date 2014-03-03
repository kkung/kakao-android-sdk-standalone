/**
 * Copyright 2014 Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.http;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * http 요청 처리 thread pool와 response를 받아 사용자의 callback을 불러주는 asyncHandler thread pool이 이 하나의 pool을 같이 이용
 * @author MJ
 */
public class HttpTaskManager {
    private static final int DEFAULT_CORE_POOL_SIZE = 0;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;
    private static final long DEFAULT_KEEP_ALIVE = 60L;
    private static final BlockingQueue<Runnable> DEFAULT_WORK_QUEUE = new SynchronousQueue<Runnable>();

    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger counter = new AtomicInteger(0);
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "KakaoHttpTask #" + counter.incrementAndGet());
        }
    };

    private static final ExecutorService defaultExecutor = new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE,
        DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS, DEFAULT_WORK_QUEUE, DEFAULT_THREAD_FACTORY);

    private static ExecutorService httpExecutor = defaultExecutor;

    public static ExecutorService getHttpExecutor() {
        return httpExecutor;
    }

    // default configuration을 쓰고 싶지 않으면 HttpTaskManager를 처음 사용하기 전에 호출해 준다.
    public static void setHttpExecutor(ExecutorService httpExecutor) {
        HttpTaskManager.httpExecutor = httpExecutor;
    }

    public static void execute(HttpRequestTask httpRequest){
        httpExecutor.execute(httpRequest);
    }
}
