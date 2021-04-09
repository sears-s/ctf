package org.jivesoftware.smack.util;

public interface CallbackRecipient<V, E> {
    CallbackRecipient<V, E> onError(ExceptionCallback<E> exceptionCallback);

    CallbackRecipient<V, E> onSuccess(SuccessCallback<V> successCallback);
}
