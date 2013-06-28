package no.found.elasticsearch.transport.netty.ssl.internal;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class NonReentrantLock extends AbstractQueuedSynchronizer
        implements Lock {

    private static final long serialVersionUID = -833780837233068610L;

    private Thread owner;

    public void lock() {
        acquire(1);
    }

    public void lockInterruptibly() throws InterruptedException {
        acquireInterruptibly(1);
    }

    public boolean tryLock() {
        return tryAcquire(1);
    }

    public boolean tryLock(long time, TimeUnit unit)
            throws InterruptedException {
        return tryAcquireNanos(1, unit.toNanos(time));
    }

    public void unlock() {
        release(1);
    }

    public boolean isHeldByCurrentThread() {
        return isHeldExclusively();
    }

    public Condition newCondition() {
        return new ConditionObject();
    }

    @Override
    protected boolean tryAcquire(int acquires) {
        if (compareAndSetState(0, 1)) {
            owner = Thread.currentThread();
            return true;
        }
        return false;
    }

    @Override
    protected boolean tryRelease(int releases) {
        if (Thread.currentThread() != owner) {
            throw new IllegalMonitorStateException();
        }
        owner = null;
        setState(0);
        return true;
    }

    @Override
    protected boolean isHeldExclusively() {
        return getState() != 0 && owner == Thread.currentThread();
    }
}
