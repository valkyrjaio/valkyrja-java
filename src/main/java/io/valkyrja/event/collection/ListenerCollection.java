/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.collection;

import io.valkyrja.event.collection.contract.ListenerCollectionContract;
import io.valkyrja.event.data.EventData;
import io.valkyrja.event.data.contract.ListenerContract;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link ListenerCollectionContract}.
 *
 * <p>Tracks listeners indexed by event class, supporting fast lookup, add, and remove operations.
 */
public class ListenerCollection implements ListenerCollectionContract {

    /** event class → (listener name → listener name) index */
    protected final Map<Class<?>, Map<String, String>> events = new HashMap<>();

    /** listener name → listener instance */
    protected final Map<String, ListenerContract> listeners = new LinkedHashMap<>();

    public EventData getData() {
        return new EventData(Map.copyOf(events), Map.copyOf(listeners));
    }

    public void setFromData(EventData data) {
        events.putAll(data.events());
        listeners.putAll(data.listeners());
    }

    @Override
    public boolean hasListener(ListenerContract listener) {
        return hasListenerById(listener.getName());
    }

    @Override
    public boolean hasListenerById(String listenerId) {
        return listeners.containsKey(listenerId);
    }

    @Override
    public void addListener(ListenerContract listener) {
        String listenerId = listener.getName();
        Class<?> eventId = listener.getEventId();

        events.computeIfAbsent(eventId, k -> new LinkedHashMap<>()).put(listenerId, listenerId);
        listeners.put(listenerId, listener);
    }

    @Override
    public void removeListener(ListenerContract listener) {
        removeListenerById(listener.getName());
    }

    @Override
    public void removeListenerById(String listenerId) {
        for (Map<String, String> listenerIds : events.values()) {
            listenerIds.remove(listenerId);
        }
        listeners.remove(listenerId);
    }

    @Override
    public boolean hasListenersForEvent(Object event) {
        return hasListenersForEventById(event.getClass());
    }

    @Override
    public boolean hasListenersForEventById(Class<?> eventId) {
        Map<String, String> ids = events.get(eventId);
        return ids != null && !ids.isEmpty();
    }

    @Override
    public Map<String, ListenerContract> getListenersForEvent(Object event) {
        return getListenersForEventById(event.getClass());
    }

    @Override
    public Map<String, ListenerContract> getListenersForEventById(Class<?> eventId) {
        Map<String, String> listenerIds = events.get(eventId);
        if (listenerIds == null || listenerIds.isEmpty()) {
            return Map.of();
        }

        Map<String, ListenerContract> result = new LinkedHashMap<>();
        for (String listenerId : listenerIds.keySet()) {
            ListenerContract listener = listeners.get(listenerId);
            if (listener != null) {
                result.put(listenerId, listener);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public void setListenersForEvent(Object event, ListenerContract... listeners) {
        setListenersForEventById(event.getClass(), listeners);
    }

    @Override
    public void setListenersForEventById(Class<?> eventId, ListenerContract... listeners) {
        for (ListenerContract listener : listeners) {
            addListener((ListenerContract) listener.withEventId(eventId));
        }
    }

    @Override
    public void removeListenersForEvent(Object event) {
        removeListenersForEventById(event.getClass());
    }

    @Override
    public void removeListenersForEventById(Class<?> eventId) {
        Map<String, String> listenerIds = events.remove(eventId);
        if (listenerIds != null) {
            listenerIds.keySet().forEach(listeners::remove);
        }
    }

    @Override
    public Map<String, ListenerContract> getListeners() {
        return Collections.unmodifiableMap(listeners);
    }

    @Override
    public List<Class<?>> getEvents() {
        return new ArrayList<>(events.keySet());
    }

    @Override
    public Map<Class<?>, Map<String, ListenerContract>> getEventsWithListeners() {
        Map<Class<?>, Map<String, ListenerContract>> result = new LinkedHashMap<>();
        for (Class<?> eventId : events.keySet()) {
            result.put(eventId, getListenersForEventById(eventId));
        }
        return Collections.unmodifiableMap(result);
    }
}
