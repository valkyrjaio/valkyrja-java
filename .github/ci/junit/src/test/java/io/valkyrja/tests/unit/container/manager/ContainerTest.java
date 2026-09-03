/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.container.manager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;
import io.valkyrja.tests.fixtures.container.ServiceFixture;
import io.valkyrja.tests.fixtures.container.SingletonFixture;
import io.valkyrja.tests.fixtures.container.provider.ProvidedFixture;
import io.valkyrja.tests.fixtures.container.provider.ProvidedSecondaryFixture;
import io.valkyrja.tests.fixtures.container.provider.ProviderFixture;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class ContainerTest {

    @SuppressWarnings("unchecked")
    private static <T> Class<T> raw(Class<?> type) {
        return (Class<T>) type;
    }

    @Test
    void bind() {
        var container = new Container();
        container.bind(ServiceFixture.class, ServiceFixture::make);

        assertTrue(container.has(ServiceFixture.class));
        assertTrue(container.isService(ServiceFixture.class));
        assertTrue(container.isPublished(ServiceFixture.class));
        assertFalse(container.isAlias(ServiceFixture.class));
        assertFalse(container.isSingleton(ServiceFixture.class));

        var service = container.get(ServiceFixture.class);
        assertInstanceOf(ServiceFixture.class, service);
        // A bound service returns a fresh instance each time.
        assertNotSame(service, container.get(ServiceFixture.class));
        assertNotSame(service, container.getService(ServiceFixture.class, Map.of()));
    }

    @Test
    void bindAlias() {
        var container = new Container();
        container.bind(ServiceFixture.class, ServiceFixture::make);
        container.bindAlias(CharSequence.class, raw(ServiceFixture.class));

        assertTrue(container.has(CharSequence.class));
        assertTrue(container.isAlias(CharSequence.class));
        assertTrue(container.isPublished(ServiceFixture.class));
        assertFalse(container.isService(CharSequence.class));

        Object service = container.get(CharSequence.class);
        assertInstanceOf(ServiceFixture.class, service);
        assertNotSame(service, container.get(CharSequence.class));
        assertInstanceOf(ServiceFixture.class, container.getAliased(CharSequence.class, Map.of()));
    }

    @Test
    void bindSingleton() {
        var container = new Container();
        container.bindSingleton(SingletonFixture.class, SingletonFixture::make);

        assertTrue(container.has(SingletonFixture.class));
        assertTrue(container.isSingleton(SingletonFixture.class));
        assertTrue(container.isService(SingletonFixture.class));
        assertTrue(container.isPublished(SingletonFixture.class));
        assertFalse(container.isAlias(SingletonFixture.class));

        var service = container.get(SingletonFixture.class);
        assertInstanceOf(SingletonFixture.class, service);
        // A bound singleton returns the same instance each time.
        assertSame(service, container.get(SingletonFixture.class));
        assertSame(service, container.getSingleton(SingletonFixture.class));
    }

    @Test
    void provided() {
        var container = new Container();
        container.register(new ProviderFixture());

        assertTrue(container.has(ProvidedFixture.class));
    }

    @Test
    void getNonExistentThrows() {
        var container = new Container();

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> container.get(ApplicationContract.class));
    }

    @Test
    void getNonExistentSingletonThrows() {
        var container = new Container();

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> container.getSingleton(ServiceFixture.class));
    }

    @Test
    void getNonExistentAliasedThrows() {
        var container = new Container();

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> container.getAliased(ContainerTest.class, Map.of()));
    }

    @Test
    void getNonExistentServiceThrows() {
        var container = new Container();

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> container.getService(ServiceFixture.class, Map.of()));
    }

    @Test
    void getDataReflectsRegisteredCallbacks() {
        var container = new Container();
        container.register(new ProviderFixture());

        var data = container.getData();

        assertTrue(data.callbacks().containsKey(ProvidedFixture.class));
        assertTrue(data.callbacks().containsKey(ProvidedSecondaryFixture.class));
        assertTrue(data.aliases().isEmpty());
        assertTrue(data.services().isEmpty());
        assertTrue(data.singletons().isEmpty());
    }

    @Test
    void setFromDataImportsCallbacks() {
        var source = new Container();
        source.register(new ProviderFixture());
        var data = source.getData();

        var container = new Container();
        assertFalse(container.has(ProvidedFixture.class));

        container.setFromData(data);

        assertTrue(container.has(ProvidedFixture.class));
        assertTrue(container.getData().callbacks().containsKey(ProvidedFixture.class));
    }

    @Test
    void constructWithDataImportsCallbacks() {
        var source = new Container();
        source.register(new ProviderFixture());
        var data = source.getData();

        var container = new Container(data);

        assertTrue(container.has(ProvidedFixture.class));
        assertTrue(container.getData().callbacks().containsKey(ProvidedFixture.class));
    }

    @Test
    void getPublishesTheDeferredProviderThenResolvesItsBinding() {
        var container = new Container();
        container.register(new ProviderFixture());

        Object provided = container.get(ProvidedFixture.class);

        assertInstanceOf(ProvidedFixture.class, provided);
        assertTrue(container.isPublished(ProvidedFixture.class));
    }

    @Test
    void getUnboundTypeThrows() {
        var container = new Container();

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> container.get(SingletonFixture.class, Map.of()));
    }

    @Test
    void getBoundTypeResolvesThroughTheBinding() {
        var container = new Container();
        container.bind(SingletonFixture.class, (c, a) -> new SingletonFixture());

        var object = container.get(SingletonFixture.class, Map.of());

        assertInstanceOf(SingletonFixture.class, object);
    }

    @Test
    void getSingletonWithNullFactoryThrows() {
        var container = new Container();
        // Singleton factory yields null → the cache-put is skipped and the lookup ultimately fails.
        container.bindSingleton(SingletonFixture.class, (c, a) -> null);

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> container.getSingleton(SingletonFixture.class));
    }

    @Test
    void getSingletonInstanceReadsTheCacheWithoutBuilding() {
        var container = new Container();
        var instance = new SingletonFixture();
        container.setSingleton(SingletonFixture.class, instance);

        assertSame(instance, container.getSingletonInstance(SingletonFixture.class));
    }

    @Test
    void getSingletonInstanceReturnsNullForAnUnresolvedBinding() {
        var container = new Container();
        container.bindSingleton(SingletonFixture.class, SingletonFixture::make);

        assertNull(container.getSingletonInstance(SingletonFixture.class));
        assertNull(container.getSingletonInstance(ServiceFixture.class));
    }

    @Test
    void getServiceCallableReturnsTheBinding() {
        var container = new Container();
        container.bind(ServiceFixture.class, ServiceFixture::make);

        var callable = container.getServiceCallable(ServiceFixture.class);

        assertNotNull(callable);
        assertInstanceOf(ServiceFixture.class, callable.apply(container, Map.of()));
    }

    @Test
    void getServiceCallableReturnsNullForAnUnboundType() {
        var container = new Container();

        assertNull(container.getServiceCallable(ServiceFixture.class));
    }
}
