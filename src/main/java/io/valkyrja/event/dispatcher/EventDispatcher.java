/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.dispatcher;

import io.valkyrja.container.enum_.InvalidReferenceMode;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.event.collection.ListenerCollection;
import io.valkyrja.event.collection.contract.ListenerCollectionContract;
import io.valkyrja.event.contract.ArgumentsCapableEventContract;
import io.valkyrja.event.contract.DispatchCollectableEventContract;
import io.valkyrja.event.contract.StoppableEventContract;
import io.valkyrja.event.data.contract.ListenerContract;
import io.valkyrja.event.dispatcher.contract.EventDispatcherContract;
import io.valkyrja.event.throwable.exception.EventInvalidEventException;
import java.util.Map;

/** Default event dispatcher implementation. */
public class EventDispatcher implements EventDispatcherContract {

    protected final ListenerCollectionContract collection;
    protected final ContainerContract container;

    public EventDispatcher() {
        this(new ListenerCollection(), new Container());
    }

    public EventDispatcher(ListenerCollectionContract collection, ContainerContract container) {
        this.collection = collection;
        this.container = container;
    }

    @Override
    public Object dispatch(Object event) {
        Map<String, ListenerContract> listeners = collection.getListenersForEvent(event);
        return dispatchListeners(event, listeners.values().toArray(new ListenerContract[0]));
    }

    @Override
    public Object dispatchIfHasListeners(Object event) {
        if (collection.hasListenersForEvent(event)) {
            return dispatch(event);
        }
        return event;
    }

    @Override
    public Object dispatchById(Class<?> eventId, Map<String, Object> arguments) {
        return dispatch(getEventFromId(eventId, arguments));
    }

    @Override
    public Object dispatchByIdIfHasListeners(Class<?> eventId, Map<String, Object> arguments) {
        Object event = getEventFromId(eventId, arguments);

        if (collection.hasListenersForEventById(eventId)) {
            return dispatch(event);
        }

        return event;
    }

    @Override
    public Object dispatchListeners(Object event, ListenerContract... listeners) {
        for (ListenerContract listener : listeners) {
            event = dispatchListener(event, listener);

            if (event instanceof StoppableEventContract stoppable
                    && stoppable.isPropagationStopped()) {
                return event;
            }
        }
        return event;
    }

    @Override
    public Object dispatchListener(Object event, ListenerContract listener) {
        Object result = listener.getHandler().apply(container, Map.of("event", event));

        if (event instanceof DispatchCollectableEventContract collectable) {
            collectable.addDispatch(result);
        }

        return event;
    }

    /**
     * Resolve an event from the container.
     *
     * <p>The container is the only source of an event instance. The application binds each event
     * that it dispatches. The dispatcher does not construct the event itself, for three reasons:
     *
     * <ul>
     *   <li>Reflection is slow.
     *   <li>Reflection assumes a constructor that takes no argument.
     *   <li>No other port has an equivalent mechanism.
     * </ul>
     *
     * <p>The container resolves a binding key to a value of any type, so the dispatcher tests the
     * value against the key.
     *
     * @param eventId the binding key of the event
     * @param arguments the arguments for the binding, and for the event
     * @return the event
     * @throws io.valkyrja.container.throwable.exception.ContainerInvalidReferenceException if the
     *     container holds no binding for the key
     * @throws EventInvalidEventException if the container resolves a value of another type
     */
    protected Object getEventFromId(Class<?> eventId, Map<String, Object> arguments) {
        Object event = container.get(eventId, arguments, InvalidReferenceMode.THROW_EXCEPTION);

        if (!eventId.isInstance(event)) {
            throw new EventInvalidEventException(eventId.getName());
        }

        if (event instanceof ArgumentsCapableEventContract capable) {
            return capable.setArguments(arguments);
        }

        return event;
    }
}
