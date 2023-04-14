package com.zelusik.eatery.log.aop;

import com.zelusik.eatery.log.LogUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraceId {

    private String id;
    private int level;

    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        return LogUtils.getLogTraceId();
    }

    public TraceId createNextId() {
        return new TraceId(id, level + 1);
    }

    public TraceId createPrevId() {
        return new TraceId(id, level - 1);
    }

    public boolean isFirstLevel() {
        return level == 0;
    }
}
