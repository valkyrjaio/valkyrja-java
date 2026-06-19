/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.event.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.event.data.Listener;
import java.util.Map;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

final class ListenerTest {

    private static final BiFunction<ContainerContract, Map<String, Object>, Object> HANDLER =
            (container, arguments) -> null;

    @Test
    void eventId() {
        var listener = new Listener(ListenerTest.class, "test", HANDLER);

        assertEquals(ListenerTest.class, listener.getEventId());

        var updated = listener.withEventId(Listener.class);

        assertNotSame(listener, updated);
        assertEquals(Listener.class, updated.getEventId());
    }

    @Test
    void name() {
        var listener = new Listener(ListenerTest.class, "test", HANDLER);

        assertEquals("test", listener.getName());

        var updated = listener.withName("test2");

        assertNotSame(listener, updated);
        assertEquals("test2", updated.getName());
    }

    @Test
    void handler() {
        BiFunction<ContainerContract, Map<String, Object>, Object> other =
                (container, arguments) -> "string";
        var listener = new Listener(ListenerTest.class, "test", HANDLER);

        assertSame(HANDLER, listener.getHandler());

        var updated = listener.withHandler(other);

        assertNotSame(listener, updated);
        assertSame(other, updated.getHandler());
    }
}
