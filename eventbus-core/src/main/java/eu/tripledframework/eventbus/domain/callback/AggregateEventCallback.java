package eu.tripledframework.eventbus.domain.callback;

import eu.tripledframework.eventbus.domain.EventCallback;

public class AggregateEventCallback<ReturnType> implements EventCallback<ReturnType> {

    private final EventCallback<ReturnType>[] callbacks;

    public AggregateEventCallback(EventCallback<ReturnType>... callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void onSuccess(ReturnType result) {
        for (EventCallback<ReturnType> callback : callbacks) {
            callback.onSuccess(result);
        }
    }

    @Override
    public void onFailure(Throwable exception) {
        for (EventCallback<ReturnType> callback : callbacks) {
            callback.onFailure(exception);
        }
    }
}
