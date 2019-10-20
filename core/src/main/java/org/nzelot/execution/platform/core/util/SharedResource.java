package org.nzelot.execution.platform.core.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedResource<M> {

    private M resource;

    private Lock lock = new ReentrantLock();

    public SharedResource(M resource) {
        this.resource = resource;
    }

    public M getResource() {
        return resource;
    }

    public void setResource(M resource) {
        this.resource = resource;
    }

    public void lock() {
        var frame = StackWalker.getInstance().walk(frames -> frames.skip(1).findFirst()).orElseThrow();
        System.out.println("[SHR-" + this.hashCode()
                + "] Lock Request by " + frame.getClassName() + "." + frame.getMethodName()
                + " Line " + frame.getLineNumber()
                + " on Thread " + Thread.currentThread().getName());

        lock.lock();

        System.out.println("[SHR-" + this.hashCode()
                + "] Lock Request Granted to " + frame.getClassName() + "." + frame.getMethodName()
                + " Line " + frame.getLineNumber()
                + " on Thread " + Thread.currentThread().getName());
    }

    public void unlock() {
        var frame = StackWalker.getInstance().walk(frames -> frames.skip(1).findFirst()).orElseThrow();
        System.out.println("[SHR-" + this.hashCode()
                + "] Unlocked by " + frame.getClassName() + "." + frame.getMethodName()
                + " Line " + frame.getLineNumber()
                + " on Thread " + Thread.currentThread().getName());
        lock.unlock();
    }
}
