/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.data;

import io.valkyrja.event.data.contract.EventDataContract;
import io.valkyrja.event.data.contract.ListenerContract;
import java.util.Map;

/**
 * Snapshot of the event collection's state, used for bulk import/export.
 *
 * @param events event class → (listener name → listener name) index
 * @param listeners listener name → listener
 */
public record EventData(
        Map<Class<?>, Map<String, String>> events, Map<String, ListenerContract> listeners)
        implements EventDataContract {

    // Compact constructor for defensive copying
    public EventData {
        events = Map.copyOf(events);
        listeners = Map.copyOf(listeners);
    }

    public EventData() {
        this(Map.of(), Map.of());
    }
}
