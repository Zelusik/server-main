package com.zelusik.eatery.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    /**
     * <p>Core Pool Size: Thread pool이 상시 유지하는 최소 thread 수. 보통 cpu core 수와 비슷하게 설정하는 것으로 보임.</p>
     * <p>Max Pool Size: Thread pool의 최대 size. Bloking queue가 꽉 차면 Core Pool Size에서 지정한 수를 초과하여 추가 thread가 생성됨(Max Pool Size까지). CorePoolSize의 2배 정도로 설정하는 것이 좋아보임.</p>
     * <p>Queue Capacity: Thread pool에 생성된 thread가 전부 사용중일 때, task들이 대기하는 queue의 capacity. 작업이 급증할 경우를 대비하여 크게 설정하는 것도 좋음</p>
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(3);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setQueueCapacity(300);
        threadPoolTaskExecutor.setTaskDecorator(new AsyncTaskDecorator());
        threadPoolTaskExecutor.setThreadNamePrefix("eatery-async-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
