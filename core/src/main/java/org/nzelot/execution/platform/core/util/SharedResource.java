package org.nzelot.execution.platform.core.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedResource<M> {

    private M resource;

    private Lock lock = new ReentrantLock();

    public SharedResource(M resource) {
        this.resource = resource;
    }

    public synchronized M getResource() {
        return resource;
    }

    public synchronized void setResource(M resource) {
        this.resource = resource;
    }

    public synchronized void lock() {
        lock.lock();
    }

    public synchronized void unlock() {
        lock.unlock();
    }
}
