/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.dispatcher;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.event.collection.ListenerCollection;
import io.valkyrja.event.collection.contract.ListenerCollectionContract;
import io.valkyrja.event.contract.ArgumentsCapableEventContract;
import io.valkyrja.event.contract.DispatchCollectableEventContract;
import io.valkyrja.event.contract.StoppableEventContract;
import io.valkyrja.event.data.contract.ListenerContract;
import io.valkyrja.event.dispatcher.contract.EventDispatcherContract;
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
        if (collection.hasListenersForEventById(eventId)) {
            return dispatchById(eventId, arguments);
        }
        return getEventFromId(eventId, arguments);
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

    /** Instantiate an event from its class, optionally passing arguments if it supports them. */
    protected Object getEventFromId(Class<?> eventId, Map<String, Object> arguments) {
        try {
            Object event = eventId.getDeclaredConstructor().newInstance();
            if (event instanceof ArgumentsCapableEventContract capable) {
                return capable.setArguments(arguments);
            }
            return event;
        } catch (ReflectiveOperationException e) {
            return new Object();
        }
    }
}
