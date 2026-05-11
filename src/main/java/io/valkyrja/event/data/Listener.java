/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.data;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.event.data.contract.ListenerContract;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Default implementation of {@link ListenerContract}.
 *
 * <p>Immutable — all {@code with*} methods return a new instance.
 */
public class Listener implements ListenerContract {

    protected Class<?> eventId;
    protected String name;
    protected BiFunction<ContainerContract, Map<String, Object>, Object> handler;

    public Listener(
            Class<?> eventId,
            String name,
            BiFunction<ContainerContract, Map<String, Object>, Object> handler) {
        this.eventId = eventId;
        this.name = name;
        this.handler = handler;
    }

    @Override
    public Class<?> getEventId() {
        return eventId;
    }

    @Override
    public ListenerContract withEventId(Class<?> eventId) {
        return new Listener(eventId, name, handler);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ListenerContract withName(String name) {
        return new Listener(eventId, name, handler);
    }

    @Override
    public BiFunction<ContainerContract, Map<String, Object>, Object> getHandler() {
        return handler;
    }

    @Override
    public ListenerContract withHandler(
            BiFunction<ContainerContract, Map<String, Object>, Object> handler) {
        return new Listener(eventId, name, handler);
    }
}
