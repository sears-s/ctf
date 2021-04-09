package org.jivesoftware.smack.util;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueueWithShutdown<E> extends AbstractQueue<E> implements BlockingQueue<E> {
    /* access modifiers changed from: private */
    public int count;
    private volatile boolean isShutdown;
    /* access modifiers changed from: private */
    public final E[] items;
    /* access modifiers changed from: private */
    public final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    /* access modifiers changed from: private */
    public int putIndex;
    /* access modifiers changed from: private */
    public int takeIndex;

    private class Itr implements Iterator<E> {
        private int lastRet = -1;
        private int nextIndex;
        private E nextItem;

        Itr() {
            if (ArrayBlockingQueueWithShutdown.this.count == 0) {
                this.nextIndex = -1;
                return;
            }
            this.nextIndex = ArrayBlockingQueueWithShutdown.this.takeIndex;
            this.nextItem = ArrayBlockingQueueWithShutdown.this.items[ArrayBlockingQueueWithShutdown.this.takeIndex];
        }

        public boolean hasNext() {
            return this.nextIndex >= 0;
        }

        private void checkNext() {
            if (this.nextIndex == ArrayBlockingQueueWithShutdown.this.putIndex) {
                this.nextIndex = -1;
                this.nextItem = null;
                return;
            }
            this.nextItem = ArrayBlockingQueueWithShutdown.this.items[this.nextIndex];
            if (this.nextItem == null) {
                this.nextIndex = -1;
            }
        }

        public E next() {
            ArrayBlockingQueueWithShutdown.this.lock.lock();
            try {
                if (this.nextIndex >= 0) {
                    this.lastRet = this.nextIndex;
                    E e = this.nextItem;
                    this.nextIndex = ArrayBlockingQueueWithShutdown.this.inc(this.nextIndex);
                    checkNext();
                    return e;
                }
                throw new NoSuchElementException();
            } finally {
                ArrayBlockingQueueWithShutdown.this.lock.unlock();
            }
        }

        public void remove() {
            ArrayBlockingQueueWithShutdown.this.lock.lock();
            try {
                int i = this.lastRet;
                if (i >= 0) {
                    this.lastRet = -1;
                    int ti = ArrayBlockingQueueWithShutdown.this.takeIndex;
                    ArrayBlockingQueueWithShutdown.this.removeAt(i);
                    this.nextIndex = i == ti ? ArrayBlockingQueueWithShutdown.this.takeIndex : i;
                    checkNext();
                    return;
                }
                throw new IllegalStateException();
            } finally {
                ArrayBlockingQueueWithShutdown.this.lock.unlock();
            }
        }
    }

    /* access modifiers changed from: private */
    public int inc(int i) {
        int i2 = i + 1;
        if (i2 == this.items.length) {
            return 0;
        }
        return i2;
    }

    private void insert(E e) {
        E[] eArr = this.items;
        int i = this.putIndex;
        eArr[i] = e;
        this.putIndex = inc(i);
        this.count++;
        this.notEmpty.signal();
    }

    private E extract() {
        E[] eArr = this.items;
        int i = this.takeIndex;
        E e = eArr[i];
        eArr[i] = null;
        this.takeIndex = inc(i);
        this.count--;
        this.notFull.signal();
        return e;
    }

    /* access modifiers changed from: private */
    public void removeAt(int i) {
        int i2 = this.takeIndex;
        if (i == i2) {
            this.items[i2] = null;
            this.takeIndex = inc(i2);
        } else {
            while (true) {
                int nexti = inc(i);
                if (nexti == this.putIndex) {
                    break;
                }
                E[] eArr = this.items;
                eArr[i] = eArr[nexti];
                i = nexti;
            }
            this.items[i] = null;
            this.putIndex = i;
        }
        this.count--;
        this.notFull.signal();
    }

    private static void checkNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    private void checkNotShutdown() throws InterruptedException {
        if (this.isShutdown) {
            throw new InterruptedException();
        }
    }

    private boolean hasNoElements() {
        return this.count == 0;
    }

    private boolean hasElements() {
        return !hasNoElements();
    }

    private boolean isFull() {
        return this.count == this.items.length;
    }

    private boolean isNotFull() {
        return !isFull();
    }

    public ArrayBlockingQueueWithShutdown(int capacity) {
        this(capacity, false);
    }

    public ArrayBlockingQueueWithShutdown(int capacity, boolean fair) {
        this.isShutdown = false;
        if (capacity > 0) {
            this.items = new Object[capacity];
            this.lock = new ReentrantLock(fair);
            this.notEmpty = this.lock.newCondition();
            this.notFull = this.lock.newCondition();
            return;
        }
        throw new IllegalArgumentException();
    }

    public void shutdown() {
        this.lock.lock();
        try {
            this.isShutdown = true;
            this.notEmpty.signalAll();
            this.notFull.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    public void start() {
        this.lock.lock();
        try {
            this.isShutdown = false;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isShutdown() {
        this.lock.lock();
        try {
            return this.isShutdown;
        } finally {
            this.lock.unlock();
        }
    }

    public E poll() {
        this.lock.lock();
        try {
            if (hasNoElements()) {
                return null;
            }
            E e = extract();
            this.lock.unlock();
            return e;
        } finally {
            this.lock.unlock();
        }
    }

    public E peek() {
        this.lock.lock();
        try {
            return hasNoElements() ? null : this.items[this.takeIndex];
        } finally {
            this.lock.unlock();
        }
    }

    public boolean offer(E e) {
        checkNotNull(e);
        this.lock.lock();
        try {
            if (!isFull()) {
                if (!this.isShutdown) {
                    insert(e);
                    this.lock.unlock();
                    return true;
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    public void put(E e) throws InterruptedException {
        checkNotNull(e);
        this.lock.lockInterruptibly();
        while (isFull()) {
            try {
                this.notFull.await();
                checkNotShutdown();
            } catch (InterruptedException ie) {
                this.notFull.signal();
                throw ie;
            } catch (Throwable th) {
                this.lock.unlock();
                throw th;
            }
        }
        insert(e);
        this.lock.unlock();
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        checkNotNull(e);
        long nanos = unit.toNanos(timeout);
        this.lock.lockInterruptibly();
        while (!isNotFull()) {
            try {
                if (nanos <= 0) {
                    this.lock.unlock();
                    return false;
                }
                nanos = this.notFull.awaitNanos(nanos);
                checkNotShutdown();
            } catch (InterruptedException ie) {
                this.notFull.signal();
                throw ie;
            } catch (Throwable ie2) {
                this.lock.unlock();
                throw ie2;
            }
        }
        insert(e);
        this.lock.unlock();
        return true;
    }

    public E take() throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            checkNotShutdown();
            while (hasNoElements()) {
                this.notEmpty.await();
                checkNotShutdown();
            }
            E e = extract();
            this.lock.unlock();
            return e;
        } catch (InterruptedException ie) {
            this.notEmpty.signal();
            throw ie;
        } catch (Throwable ie2) {
            this.lock.unlock();
            throw ie2;
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        this.lock.lockInterruptibly();
        try {
            checkNotShutdown();
            while (!hasElements()) {
                if (nanos <= 0) {
                    this.lock.unlock();
                    return null;
                }
                nanos = this.notEmpty.awaitNanos(nanos);
                checkNotShutdown();
            }
            E e = extract();
            this.lock.unlock();
            return e;
        } catch (InterruptedException ie) {
            this.notEmpty.signal();
            throw ie;
        } catch (Throwable ie2) {
            this.lock.unlock();
            throw ie2;
        }
    }

    public int remainingCapacity() {
        this.lock.lock();
        try {
            return this.items.length - this.count;
        } finally {
            this.lock.unlock();
        }
    }

    public int drainTo(Collection<? super E> c) {
        checkNotNull(c);
        if (c != this) {
            this.lock.lock();
            try {
                int i = this.takeIndex;
                int n = 0;
                while (n < this.count) {
                    c.add(this.items[i]);
                    this.items[i] = null;
                    i = inc(i);
                    n++;
                }
                if (n > 0) {
                    this.count = 0;
                    this.putIndex = 0;
                    this.takeIndex = 0;
                    this.notFull.signalAll();
                }
                return n;
            } finally {
                this.lock.unlock();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        checkNotNull(c);
        if (c == this) {
            throw new IllegalArgumentException();
        } else if (maxElements <= 0) {
            return 0;
        } else {
            this.lock.lock();
            try {
                int i = this.takeIndex;
                int n = 0;
                int max = maxElements < this.count ? maxElements : this.count;
                while (n < max) {
                    c.add(this.items[i]);
                    this.items[i] = null;
                    i = inc(i);
                    n++;
                }
                if (n > 0) {
                    this.count -= n;
                    this.takeIndex = i;
                    this.notFull.signalAll();
                }
                return n;
            } finally {
                this.lock.unlock();
            }
        }
    }

    public int size() {
        this.lock.lock();
        try {
            return this.count;
        } finally {
            this.lock.unlock();
        }
    }

    public Iterator<E> iterator() {
        this.lock.lock();
        try {
            return new Itr();
        } finally {
            this.lock.unlock();
        }
    }
}
