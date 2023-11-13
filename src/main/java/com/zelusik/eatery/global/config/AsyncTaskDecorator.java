package com.zelusik.eatery.global.config;

import com.zelusik.eatery.global.log.LogUtils;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

public class AsyncTaskDecorator implements TaskDecorator {

    @NonNull
    @Override
    public Runnable decorate(@NonNull Runnable task) {
        return loggingDecorate(task);
    }

    @NonNull
    private Runnable loggingDecorate(@NonNull Runnable task) {
        String logTraceId = MDC.get(LogUtils.LOG_TRACE_ID_MDC_KEY);
        return () -> {
            MDC.put(LogUtils.LOG_TRACE_ID_MDC_KEY, logTraceId);
            task.run();
        };
    }
}
