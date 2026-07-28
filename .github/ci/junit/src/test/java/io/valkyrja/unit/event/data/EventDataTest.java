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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.event.data.EventData;
import io.valkyrja.event.data.Listener;
import io.valkyrja.event.data.contract.ListenerContract;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class EventDataTest {

    @Test
    void defaultIsEmpty() {
        var data = new EventData();

        assertTrue(data.events().isEmpty());
        assertTrue(data.listeners().isEmpty());
    }

    @Test
    void retainsProvidedData() {
        Map<Class<?>, Map<String, String>> events =
                Map.of(String.class, Map.of("listener", "listener"));
        Map<String, ListenerContract> listeners =
                Map.of("listener", new Listener(String.class, "listener", (c, a) -> null));

        var data = new EventData(events, listeners);

        assertEquals(events, data.events());
        assertEquals(listeners, data.listeners());
    }
}
