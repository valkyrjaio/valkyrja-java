/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.collection.contract;

import io.valkyrja.event.data.EventData;
import io.valkyrja.event.data.contract.ListenerContract;
import java.util.List;
import java.util.Map;

public interface ListenerCollectionContract {

    /**
     * Get a data snapshot of the collection's current state.
     *
     * @return collection data
     */
    EventData getData();

    /**
     * Merge state from a data object into this collection.
     *
     * @param data the data to merge
     */
    void setFromData(EventData data);

    /**
     * Check whether a listener (by identity) is registered.
     *
     * @param listener the listener
     * @return true if registered
     */
    boolean hasListener(ListenerContract listener);

    /**
     * Check whether a listener with the given name is registered.
     *
     * @param listenerId the listener name
     * @return true if registered
     */
    boolean hasListenerById(String listenerId);

    /**
     * Add a listener to the collection.
     *
     * @param listener the listener to add
     */
    void addListener(ListenerContract listener);

    /**
     * Remove a listener from the collection.
     *
     * @param listener the listener to remove
     */
    void removeListener(ListenerContract listener);

    /**
     * Remove a listener by name.
     *
     * @param listenerId the listener name
     */
    void removeListenerById(String listenerId);

    /**
     * Check whether any listeners are registered for an event.
     *
     * @param event the event instance
     * @return true if listeners exist
     */
    boolean hasListenersForEvent(Object event);

    /**
     * Check whether any listeners are registered for an event type.
     *
     * @param eventId the event class
     * @return true if listeners exist
     */
    boolean hasListenersForEventById(Class<?> eventId);

    /**
     * Get all listeners registered for an event.
     *
     * @param event the event instance
     * @return ordered map of listener name → listener
     */
    Map<String, ListenerContract> getListenersForEvent(Object event);

    /**
     * Get all listeners registered for an event type.
     *
     * @param eventId the event class
     * @return ordered map of listener name → listener
     */
    Map<String, ListenerContract> getListenersForEventById(Class<?> eventId);

    /**
     * Set (replace) all listeners for an event.
     *
     * @param event the event instance
     * @param listeners the listeners to set
     */
    void setListenersForEvent(Object event, ListenerContract... listeners);

    /**
     * Set (replace) all listeners for an event type.
     *
     * @param eventId the event class
     * @param listeners the listeners to set
     */
    void setListenersForEventById(Class<?> eventId, ListenerContract... listeners);

    /**
     * Remove all listeners for an event.
     *
     * @param event the event instance
     */
    void removeListenersForEvent(Object event);

    /**
     * Remove all listeners for an event type.
     *
     * @param eventId the event class
     */
    void removeListenersForEventById(Class<?> eventId);

    /**
     * Get all registered listeners.
     *
     * @return map of listener name → listener
     */
    Map<String, ListenerContract> getListeners();

    /**
     * Get all event types that have at least one listener.
     *
     * @return list of event classes
     */
    List<Class<?>> getEvents();

    /**
     * Get all event types mapped to their listeners.
     *
     * @return map of event class → (listener name → listener)
     */
    Map<Class<?>, Map<String, ListenerContract>> getEventsWithListeners();
}
