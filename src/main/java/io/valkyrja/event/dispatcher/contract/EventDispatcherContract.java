/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.dispatcher.contract;

import io.valkyrja.event.data.contract.ListenerContract;
import java.util.Map;

/**
 * Contract for the event dispatcher.
 *
 * <p>Equivalent to PSR-14's {@code EventDispatcherInterface} with additional helpers.
 */
public interface EventDispatcherContract {

    /**
     * Dispatch an event to all registered listeners.
     *
     * @param event the event to dispatch
     * @return the event (possibly mutated by listeners)
     */
    Object dispatch(Object event);

    /**
     * Dispatch an event only if listeners are registered for it.
     *
     * @param event the event to dispatch
     * @return the event
     */
    Object dispatchIfHasListeners(Object event);

    /**
     * Dispatch an event by its class, constructing a new instance.
     *
     * @param eventId the event class
     * @param arguments constructor arguments
     * @return the dispatched event
     */
    Object dispatchById(Class<?> eventId, Map<String, Object> arguments);

    /**
     * Dispatch an event by class only if listeners exist.
     *
     * @param eventId the event class
     * @param arguments constructor arguments
     * @return the event
     */
    Object dispatchByIdIfHasListeners(Class<?> eventId, Map<String, Object> arguments);

    /**
     * Dispatch an event to specific listeners.
     *
     * @param event the event
     * @param listeners the listeners to invoke
     * @return the event
     */
    Object dispatchListeners(Object event, ListenerContract... listeners);

    /**
     * Dispatch an event to a single listener.
     *
     * @param event the event
     * @param listener the listener to invoke
     * @return the event
     */
    Object dispatchListener(Object event, ListenerContract listener);
}