/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.event.data;

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
