package com.supaham.supervisor.utils;

/**
 * Created by Ali on 30/10/2015.
 */
public final class TaskTimings {

    private long startMillis;
    private long endMillis;
    private long startNanos;
    private long endNanos;
    
    public void start() {
        this.startMillis = System.currentTimeMillis();
        this.startNanos = System.nanoTime();
    }
    
    public void stop() {
        this.endMillis = System.currentTimeMillis();
        this.endNanos = System.nanoTime();
    }
    
    public long getDurationInMillis() {
        return this.endMillis - this.startMillis;
    }
    
    public long getDurationInNanos() {
        return this.endNanos - this.startNanos;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public long getStartNanos() {
        return startNanos;
    }

    public long getEndNanos() {
        return endNanos;
    }
}
