/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.data.contract;

import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Contract for a registered event listener.
 *
 * <p>Associates an event type with a handler callable and a unique name.
 */
public interface ListenerContract {

    /**
     * Get the event class this listener is registered for.
     *
     * @return the event class
     */
    Class<?> getEventId();

    /**
     * Return a new instance with the given event class.
     *
     * @param eventId the event class
     * @return new listener
     */
    ListenerContract withEventId(Class<?> eventId);

    /**
     * Get the unique name identifying this listener registration.
     *
     * @return the listener name
     */
    String getName();

    /**
     * Return a new instance with the given name.
     *
     * @param name the name
     * @return new listener
     */
    ListenerContract withName(String name);

    /**
     * Get the handler callable that will be invoked when the event fires.
     *
     * @return the handler
     */
    BiFunction<ContainerContract, Map<String, Object>, Object> getHandler();

    /**
     * Return a new instance with the given handler.
     *
     * @param handler the handler
     * @return new listener
     */
    ListenerContract withHandler(BiFunction<ContainerContract, Map<String, Object>, Object> handler);
}