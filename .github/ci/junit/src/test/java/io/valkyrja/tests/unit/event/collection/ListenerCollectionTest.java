/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.event.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.event.collection.ListenerCollection;
import io.valkyrja.event.data.EventData;
import io.valkyrja.event.data.Listener;
import io.valkyrja.tests.fixtures.event.EventFixture;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

final class ListenerCollectionTest {

    private static final BiFunction<ContainerContract, Map<String, Object>, Object> HANDLER =
            (container, arguments) -> null;

    private static Listener listener(String name) {
        return new Listener(EventFixture.class, name, HANDLER);
    }

    @Test
    void getDataEmptyThenPopulated() {
        var collection = new ListenerCollection();

        var empty = collection.getData();
        assertTrue(empty.listeners().isEmpty());
        assertTrue(empty.events().isEmpty());

        var listener = listener("listener");
        collection.addListener(listener);

        var data = collection.getData();
        assertSame(listener, data.listeners().get("listener"));
        assertEquals(Map.of(EventFixture.class, Map.of("listener", "listener")), data.events());
        assertEquals(Map.of("listener", listener), collection.getListeners());
        assertEquals(List.of(EventFixture.class), collection.getEvents());
        assertEquals(
                Map.of(EventFixture.class, Map.of("listener", listener)),
                collection.getEventsWithListeners());
        assertEquals(
                Map.of("listener", listener), collection.getListenersForEvent(new EventFixture()));
        assertEquals(
                Map.of("listener", listener),
                collection.getListenersForEventById(EventFixture.class));
    }

    @Test
    void setFromDataReplacesState() {
        var listener = listener("listener");
        var listener2 = listener("listener2");
        var data =
                new EventData(
                        Map.of(
                                EventFixture.class,
                                Map.of("listener", "listener", "listener2", "listener2")),
                        Map.of("listener", listener, "listener2", listener2));

        var collection = new ListenerCollection();
        collection.setFromData(data);

        assertEquals(2, collection.getListeners().size());
        assertSame(listener, collection.getListeners().get("listener"));
        assertSame(listener2, collection.getListeners().get("listener2"));
        assertEquals(List.of(EventFixture.class), collection.getEvents());
        assertTrue(
                collection.getListenersForEventById(EventFixture.class).containsKey("listener2"));

        // Re-setting with empty data must REPLACE (clear), not merge.
        collection.setFromData(new EventData());

        assertTrue(collection.getListeners().isEmpty());
        assertTrue(collection.getEvents().isEmpty());
        assertTrue(collection.getEventsWithListeners().isEmpty());
        assertTrue(collection.getListenersForEvent(new EventFixture()).isEmpty());
        assertTrue(collection.getListenersForEventById(EventFixture.class).isEmpty());
    }

    @Test
    void addAndRemoveListenerVariants() {
        var collection = new ListenerCollection();
        var listener = listener("listener");
        var event = new EventFixture();

        assertFalse(collection.hasListener(listener));
        assertFalse(collection.hasListenerById("listener"));
        assertFalse(collection.hasListenersForEvent(event));
        assertFalse(collection.hasListenersForEventById(EventFixture.class));

        collection.addListener(listener);
        assertTrue(collection.hasListener(listener));
        assertTrue(collection.hasListenerById("listener"));
        assertTrue(collection.hasListenersForEvent(event));
        assertTrue(collection.hasListenersForEventById(EventFixture.class));

        collection.removeListener(listener);
        assertFalse(collection.hasListenerById("listener"));

        collection.addListener(listener);
        collection.removeListenerById("listener");
        assertFalse(collection.hasListenerById("listener"));

        collection.setListenersForEvent(event, listener);
        assertTrue(collection.hasListenersForEvent(event));

        collection.removeListenersForEvent(event);
        assertFalse(collection.hasListenersForEvent(event));

        collection.setListenersForEventById(EventFixture.class, listener);
        assertTrue(collection.hasListenersForEventById(EventFixture.class));

        collection.removeListenersForEventById(EventFixture.class);
        assertFalse(collection.hasListenersForEventById(EventFixture.class));
    }

    @Test
    void getListenersForUnknownEventIsEmpty() {
        var collection = new ListenerCollection();

        assertTrue(collection.getListenersForEventById(EventFixture.class).isEmpty());
    }

    @Test
    void removeListenersForUnknownEventIsNoOp() {
        var collection = new ListenerCollection();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> collection.removeListenersForEventById(EventFixture.class));
    }

    @Test
    void emptyEventEntryReportsNoListeners() {
        var collection = new ListenerCollection();
        collection.setFromData(new EventData(Map.of(EventFixture.class, Map.of()), Map.of()));

        // Event key present but no listener ids → non-null-but-empty branch.
        assertFalse(collection.hasListenersForEventById(EventFixture.class));
        assertTrue(collection.getListenersForEventById(EventFixture.class).isEmpty());
    }

    @Test
    void getListenersSkipsMissingListenerReferences() {
        var collection = new ListenerCollection();
        collection.setFromData(
                new EventData(Map.of(EventFixture.class, Map.of("ghost", "ghost")), Map.of()));

        // Listener id referenced by the event but absent from the listeners map → skipped.
        assertTrue(collection.getListenersForEventById(EventFixture.class).isEmpty());
    }
}
