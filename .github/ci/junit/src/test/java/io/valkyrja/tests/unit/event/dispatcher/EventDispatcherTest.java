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
import io.valkyrja.tests.fixtures.event.ArgumentsCapableEventFixture;
import io.valkyrja.tests.fixtures.event.DispatchCollectableEventFixture;
import io.valkyrja.tests.fixtures.event.EventFixture;
import io.valkyrja.tests.fixtures.event.NonStoppingStoppableEventFixture;
import io.valkyrja.tests.fixtures.event.StoppableEventFixture;
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
        collection.addListener(listener(DispatchCollectableEventFixture.class, "listener"));
        var dispatcher = dispatcherWith(collection);

        var event =
                (DispatchCollectableEventFixture)
                        dispatcher.dispatch(new DispatchCollectableEventFixture());
        var byId =
                (DispatchCollectableEventFixture)
                        dispatcher.dispatchById(DispatchCollectableEventFixture.class, Map.of());

        assertTrue(dispatched.get());
        assertEquals(List.of("test"), event.getDispatches());
        assertEquals(List.of("test"), byId.getDispatches());

        // Three listeners → three dispatches.
        collection.addListener(listener(DispatchCollectableEventFixture.class, "listener2"));
        collection.addListener(listener(DispatchCollectableEventFixture.class, "listener3"));
        var multi =
                (DispatchCollectableEventFixture)
                        dispatcher.dispatch(new DispatchCollectableEventFixture());
        assertEquals(List.of("test", "test", "test"), multi.getDispatches());
    }

    @Test
    void dispatchIfHasListeners() {
        var collection = new ListenerCollection();
        var dispatcher = dispatcherWith(collection);
        var event = new DispatchCollectableEventFixture();

        // No listeners → event returned unchanged, callback never runs.
        assertSame(event, dispatcher.dispatchIfHasListeners(event));
        assertInstanceOf(
                DispatchCollectableEventFixture.class,
                dispatcher.dispatchByIdIfHasListeners(
                        DispatchCollectableEventFixture.class, Map.of()));
        assertFalse(dispatched.get());

        collection.addListener(listener(DispatchCollectableEventFixture.class, "listener"));

        var dispatchedEvent =
                (DispatchCollectableEventFixture)
                        dispatcher.dispatchIfHasListeners(new DispatchCollectableEventFixture());
        var dispatchedById =
                (DispatchCollectableEventFixture)
                        dispatcher.dispatchByIdIfHasListeners(
                                DispatchCollectableEventFixture.class, Map.of());

        assertTrue(dispatched.get());
        assertEquals(List.of("test"), dispatchedEvent.getDispatches());
        assertEquals(List.of("test"), dispatchedById.getDispatches());
    }

    @Test
    void stoppableEventStopsAfterFirstListener() {
        var collection = new ListenerCollection();
        collection.addListener(listener(StoppableEventFixture.class, "listener"));
        collection.addListener(listener(StoppableEventFixture.class, "listener2"));
        collection.addListener(listener(StoppableEventFixture.class, "listener3"));
        var dispatcher = dispatcherWith(collection);

        var event = (StoppableEventFixture) dispatcher.dispatch(new StoppableEventFixture());

        // Three listeners, but propagation stops after the first dispatch.
        assertEquals(List.of("test"), event.getDispatches());
    }

    @Test
    void dispatchListenerOnNonCollectableEventDoesNotCollect() {
        var collection = new ListenerCollection();
        collection.addListener(listener(EventFixture.class, "listener"));
        var dispatcher = dispatcherWith(collection);

        var event = dispatcher.dispatch(new EventFixture());

        assertTrue(dispatched.get());
        assertInstanceOf(EventFixture.class, event);
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
                dispatcher.dispatchById(ArgumentsCapableEventFixture.class, Map.of("key", "value"));

        assertInstanceOf(ArgumentsCapableEventFixture.class, result);
        assertEquals(
                Map.of("key", "value"), ((ArgumentsCapableEventFixture) result).getArguments());
    }

    @Test
    void nonStoppingStoppableEventRunsAllListeners() {
        var collection = new ListenerCollection();
        collection.addListener(listener(NonStoppingStoppableEventFixture.class, "listener"));
        collection.addListener(listener(NonStoppingStoppableEventFixture.class, "listener2"));
        var dispatcher = dispatcherWith(collection);

        var event =
                (NonStoppingStoppableEventFixture)
                        dispatcher.dispatch(new NonStoppingStoppableEventFixture());

        // Propagation is never stopped, so every listener still dispatches.
        assertEquals(List.of("test", "test"), event.getDispatches());
    }
}
