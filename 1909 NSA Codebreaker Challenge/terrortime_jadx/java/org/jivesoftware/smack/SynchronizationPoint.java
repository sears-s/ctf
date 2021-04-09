package org.jivesoftware.smack;

import java.lang.Exception;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.SmackWrappedException;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.TopLevelStreamElement;

public class SynchronizationPoint<E extends Exception> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Condition condition;
    private final AbstractXMPPConnection connection;
    private final Lock connectionLock;
    private E failureException;
    private SmackWrappedException smackWrappedExcpetion;
    private State state;
    private final String waitFor;

    /* renamed from: org.jivesoftware.smack.SynchronizationPoint$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State = new int[State.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State[State.Failure.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State[State.Success.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State[State.Initial.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State[State.NoResponse.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State[State.RequestSent.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private enum State {
        Initial,
        RequestSent,
        NoResponse,
        Success,
        Failure
    }

    public SynchronizationPoint(AbstractXMPPConnection connection2, String waitFor2) {
        this.connection = connection2;
        this.connectionLock = connection2.getConnectionLock();
        this.condition = connection2.getConnectionLock().newCondition();
        this.waitFor = waitFor2;
        init();
    }

    public void init() {
        this.connectionLock.lock();
        this.state = State.Initial;
        this.failureException = null;
        this.smackWrappedExcpetion = null;
        this.connectionLock.unlock();
    }

    public Exception sendAndWaitForResponse(TopLevelStreamElement request) throws NoResponseException, NotConnectedException, InterruptedException {
        this.connectionLock.lock();
        if (request != null) {
            try {
                if (request instanceof Stanza) {
                    this.connection.sendStanza((Stanza) request);
                } else if (request instanceof Nonza) {
                    this.connection.sendNonza((Nonza) request);
                } else {
                    throw new IllegalStateException("Unsupported element type");
                }
                this.state = State.RequestSent;
            } catch (Throwable th) {
                this.connectionLock.unlock();
                throw th;
            }
        }
        waitForConditionOrTimeout();
        this.connectionLock.unlock();
        return checkForResponse();
    }

    public void sendAndWaitForResponseOrThrow(Nonza request) throws Exception, NoResponseException, NotConnectedException, InterruptedException, SmackWrappedException {
        sendAndWaitForResponse(request);
        if (AnonymousClass1.$SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State[this.state.ordinal()] == 1) {
            throwException();
        }
    }

    public void checkIfSuccessOrWaitOrThrow() throws NoResponseException, Exception, InterruptedException, SmackWrappedException {
        checkIfSuccessOrWait();
        if (this.state == State.Failure) {
            throwException();
        }
    }

    /* JADX INFO: finally extract failed */
    public Exception checkIfSuccessOrWait() throws NoResponseException, InterruptedException {
        this.connectionLock.lock();
        try {
            int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State[this.state.ordinal()];
            if (i == 1) {
                Exception exception = getException();
                this.connectionLock.unlock();
                return exception;
            } else if (i != 2) {
                waitForConditionOrTimeout();
                this.connectionLock.unlock();
                return checkForResponse();
            } else {
                this.connectionLock.unlock();
                return null;
            }
        } catch (Throwable th) {
            this.connectionLock.unlock();
            throw th;
        }
    }

    public void reportSuccess() {
        this.connectionLock.lock();
        try {
            this.state = State.Success;
            this.condition.signalAll();
        } finally {
            this.connectionLock.unlock();
        }
    }

    @Deprecated
    public void reportFailure() {
        reportFailure(null);
    }

    public void reportFailure(E failureException2) {
        this.connectionLock.lock();
        try {
            this.state = State.Failure;
            this.failureException = failureException2;
            this.condition.signalAll();
        } finally {
            this.connectionLock.unlock();
        }
    }

    public void reportGenericFailure(SmackWrappedException exception) {
        this.connectionLock.lock();
        try {
            this.state = State.Failure;
            this.smackWrappedExcpetion = exception;
            this.condition.signalAll();
        } finally {
            this.connectionLock.unlock();
        }
    }

    public boolean wasSuccessful() {
        this.connectionLock.lock();
        try {
            return this.state == State.Success;
        } finally {
            this.connectionLock.unlock();
        }
    }

    public boolean isNotInInitialState() {
        this.connectionLock.lock();
        try {
            return this.state != State.Initial;
        } finally {
            this.connectionLock.unlock();
        }
    }

    public boolean requestSent() {
        this.connectionLock.lock();
        try {
            return this.state == State.RequestSent;
        } finally {
            this.connectionLock.unlock();
        }
    }

    public E getFailureException() {
        this.connectionLock.lock();
        try {
            return this.failureException;
        } finally {
            this.connectionLock.unlock();
        }
    }

    private void waitForConditionOrTimeout() throws InterruptedException {
        long remainingWait = TimeUnit.MILLISECONDS.toNanos(this.connection.getReplyTimeout());
        while (true) {
            if (this.state != State.RequestSent && this.state != State.Initial) {
                return;
            }
            if (remainingWait <= 0) {
                this.state = State.NoResponse;
                return;
            }
            remainingWait = this.condition.awaitNanos(remainingWait);
        }
    }

    private Exception getException() {
        E e = this.failureException;
        if (e != null) {
            return e;
        }
        return this.smackWrappedExcpetion;
    }

    private void throwException() throws Exception, SmackWrappedException {
        E e = this.failureException;
        if (e != null) {
            throw e;
        }
        throw this.smackWrappedExcpetion;
    }

    private Exception checkForResponse() throws NoResponseException {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$SynchronizationPoint$State[this.state.ordinal()];
        if (i == 1) {
            return getException();
        }
        if (i == 2) {
            return null;
        }
        if (i == 3 || i == 4 || i == 5) {
            throw NoResponseException.newWith((XMPPConnection) this.connection, this.waitFor);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown state ");
        sb.append(this.state);
        throw new AssertionError(sb.toString());
    }
}
