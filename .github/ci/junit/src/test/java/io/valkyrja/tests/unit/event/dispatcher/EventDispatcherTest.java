/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.event.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.event.collection.ListenerCollection;
import io.valkyrja.event.data.Listener;
import io.valkyrja.event.dispatcher.EventDispatcher;
import io.valkyrja.tests.fixtures.event.ArgumentsCapableEventClass;
import io.valkyrja.tests.fixtures.event.DispatchCollectableEventClass;
import io.valkyrja.tests.fixtures.event.EventClass;
import io.valkyrja.tests.fixtures.event.NonStoppingStoppableEventClass;
import io.valkyrja.tests.fixtures.event.StoppableEventClass;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

final class EventDispatcherTest {

    private final AtomicBoolean dispatched = new AtomicBoolean(false);

    private Listener listener(Class<?> eventId, String name) {
        BiFunction<ContainerContract, Map<String, Object>, Object> handler =
                (container, arguments) -> {
                    dispatched.set(true);
                    return "test";
                };
        return new Listener(eventId, name, handler);
    }

    private EventDispatcher dispatcherWith(ListenerCollection collection) {
        return new EventDispatcher(collection, new Container());
    }

    @Test
    void dispatchInvokesListenersAndCollectsResults() {
        var collection = new ListenerCollection();
        collection.addListener(listener(DispatchCollectableEventClass.class, "listener"));
        var dispatcher = dispatcherWith(collection);

        var event =
                (DispatchCollectableEventClass)
                        dispatcher.dispatch(new DispatchCollectableEventClass());
        var byId =
                (DispatchCollectableEventClass)
                        dispatcher.dispatchById(DispatchCollectableEventClass.class, Map.of());

        assertTrue(dispatched.get());
        assertEquals(List.of("test"), event.getDispatches());
        assertEquals(List.of("test"), byId.getDispatches());

        // Three listeners → three dispatches.
        collection.addListener(listener(DispatchCollectableEventClass.class, "listener2"));
        collection.addListener(listener(DispatchCollectableEventClass.class, "listener3"));
        var multi =
                (DispatchCollectableEventClass)
                        dispatcher.dispatch(new DispatchCollectableEventClass());
        assertEquals(List.of("test", "test", "test"), multi.getDispatches());
    }

    @Test
    void dispatchIfHasListeners() {
        var collection = new ListenerCollection();
        var dispatcher = dispatcherWith(collection);
        var event = new DispatchCollectableEventClass();

        // No listeners → event returned unchanged, callback never runs.
        assertSame(event, dispatcher.dispatchIfHasListeners(event));
        assertInstanceOf(
                DispatchCollectableEventClass.class,
                dispatcher.dispatchByIdIfHasListeners(
                        DispatchCollectableEventClass.class, Map.of()));
        assertFalse(dispatched.get());

        collection.addListener(listener(DispatchCollectableEventClass.class, "listener"));

        var dispatchedEvent =
                (DispatchCollectableEventClass)
                        dispatcher.dispatchIfHasListeners(new DispatchCollectableEventClass());
        var dispatchedById =
                (DispatchCollectableEventClass)
                        dispatcher.dispatchByIdIfHasListeners(
                                DispatchCollectableEventClass.class, Map.of());

        assertTrue(dispatched.get());
        assertEquals(List.of("test"), dispatchedEvent.getDispatches());
        assertEquals(List.of("test"), dispatchedById.getDispatches());
    }

    @Test
    void stoppableEventStopsAfterFirstListener() {
        var collection = new ListenerCollection();
        collection.addListener(listener(StoppableEventClass.class, "listener"));
        collection.addListener(listener(StoppableEventClass.class, "listener2"));
        collection.addListener(listener(StoppableEventClass.class, "listener3"));
        var dispatcher = dispatcherWith(collection);

        var event = (StoppableEventClass) dispatcher.dispatch(new StoppableEventClass());

        // Three listeners, but propagation stops after the first dispatch.
        assertEquals(List.of("test"), event.getDispatches());
    }

    @Test
    void dispatchListenerOnNonCollectableEventDoesNotCollect() {
        var collection = new ListenerCollection();
        collection.addListener(listener(EventClass.class, "listener"));
        var dispatcher = dispatcherWith(collection);

        var event = dispatcher.dispatch(new EventClass());

        assertTrue(dispatched.get());
        assertInstanceOf(EventClass.class, event);
    }

    @Test
    void dispatchByIdUnknownClassReturnsPlainObject() {
        var dispatcher = new EventDispatcher();

        var result = dispatcher.dispatchById(CharSequence.class, Map.of());

        assertSame(Object.class, result.getClass());
    }

    @Test
    void dispatchByIdArgumentsCapableEventReceivesArguments() {
        var dispatcher = new EventDispatcher();

        var result =
                dispatcher.dispatchById(ArgumentsCapableEventClass.class, Map.of("key", "value"));

        assertInstanceOf(ArgumentsCapableEventClass.class, result);
        assertEquals(Map.of("key", "value"), ((ArgumentsCapableEventClass) result).getArguments());
    }

    @Test
    void nonStoppingStoppableEventRunsAllListeners() {
        var collection = new ListenerCollection();
        collection.addListener(listener(NonStoppingStoppableEventClass.class, "listener"));
        collection.addListener(listener(NonStoppingStoppableEventClass.class, "listener2"));
        var dispatcher = dispatcherWith(collection);

        var event =
                (NonStoppingStoppableEventClass)
                        dispatcher.dispatch(new NonStoppingStoppableEventClass());

        // Propagation is never stopped, so every listener still dispatches.
        assertEquals(List.of("test", "test"), event.getDispatches());
    }
}
