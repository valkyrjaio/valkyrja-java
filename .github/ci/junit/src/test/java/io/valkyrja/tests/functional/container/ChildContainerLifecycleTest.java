/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.functional.container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.container.ServiceFixture;
import io.valkyrja.tests.fixtures.container.SingletonFixture;
import io.valkyrja.tests.fixtures.container.provider.ProvidedFixture;
import io.valkyrja.tests.fixtures.container.provider.PublishingProviderFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class ChildContainerLifecycleTest {

    @SuppressWarnings("unchecked")
    private static <T> Class<T> raw(Class<?> type) {
        return (Class<T>) type;
    }

    @Test
    void eachRequestKeepsItsOwnScopeAndLeavesTheParentAlone() {
        var parent = new Container();

        // Boot. Everything a worker registers before the request loop begins.
        parent.register(new PublishingProviderFixture());
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        parent.bindSingleton(raw(Runnable.class), SingletonFixture::make);
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(SingletonFixture.class));
        var shared = parent.getSingleton(SingletonFixture.class);

        // One snapshot, taken once, read by every request.
        ContainerDataContract data = parent.getData();
        ContainerDataContract registrations = parent.getData();

        List<Object> unbuilt = new ArrayList<>();
        List<Object> provided = new ArrayList<>();

        for (var request = 0; request < 3; request++) {
            var child = new ChildContainer(parent, (ContainerData) data);

            // A fresh child carries nothing the last request registered
            assertFalse(child.isSingletonInstance(raw(Comparable.class)));

            var requestScoped = new SingletonFixture();
            child.setSingleton(raw(Comparable.class), requestScoped);

            // The parent built this one before the loop, so every request shares it
            assertSame(shared, child.getSingleton(SingletonFixture.class));
            assertSame(shared, child.getAliased(CharSequence.class, Map.of()));

            // The parent never built this one, so the request builds its own
            Object own = child.getSingleton(Runnable.class);
            unbuilt.add(own);
            assertSame(own, child.getSingleton(Runnable.class));

            // The child holds the publish callback, so it publishes into itself
            provided.add(child.get(ProvidedFixture.class, Map.of()));

            // A bound factory runs for each call, and caches nowhere
            assertNotSame(
                    child.get(ServiceFixture.class, Map.of()),
                    child.get(ServiceFixture.class, Map.of()));

            assertSame(requestScoped, child.getSingleton(Comparable.class));
        }

        // Nothing a request registered reaches the parent
        assertFalse(parent.has(Comparable.class));
        assertFalse(parent.isSingletonInstance(Runnable.class));
        assertFalse(parent.isSingletonInstance(ServiceFixture.class));
        assertFalse(parent.isPublished(ProvidedFixture.class));
        assertFalse(parent.isSingletonInstance(ProvidedFixture.class));

        // Nothing one request built reaches another
        assertNotSame(unbuilt.get(0), unbuilt.get(1));
        assertNotSame(unbuilt.get(1), unbuilt.get(2));
        assertNotSame(provided.get(0), provided.get(1));
        assertNotSame(provided.get(1), provided.get(2));

        // The parent still holds the registrations it booted with
        assertEquals(registrations.aliases(), parent.getData().aliases());
        assertEquals(registrations.singletons(), parent.getData().singletons());
        assertEquals(registrations.services().keySet(), parent.getData().services().keySet());
        assertEquals(registrations.callbacks().keySet(), parent.getData().callbacks().keySet());
    }
}
