/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.container.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;
import io.valkyrja.tests.fixtures.container.ServiceFixture;
import io.valkyrja.tests.fixtures.container.SingletonFixture;
import io.valkyrja.tests.fixtures.container.provider.BindingProviderFixture;
import io.valkyrja.tests.fixtures.container.provider.ProvidedFixture;
import io.valkyrja.tests.fixtures.container.provider.PublishingProviderFixture;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Per-request child container delegating to the parent through the contract only. */
final class ChildContainerTest {

    private Container parent;
    private ChildContainer child;

    @SuppressWarnings("unchecked")
    private static <T> Class<T> raw(Class<?> type) {
        return (Class<T>) type;
    }

    /** Build a child from the current parent state (callbacks + singletons are copied). */
    private ChildContainer createChild() {
        return new ChildContainer(parent, (ContainerData) parent.getData());
    }

    @BeforeEach
    void setUp() {
        parent = new Container();
        child = createChild();
    }

    @Test
    void isAliasFromParent() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));

        assertTrue(child.isAlias(CharSequence.class));
        assertFalse(child.isAlias(Runnable.class));
    }

    @Test
    void isAliasFromChild() {
        child.bind(ServiceFixture.class, ServiceFixture::make);
        child.bindAlias(Runnable.class, raw(ServiceFixture.class));

        assertTrue(child.isAlias(Runnable.class));
        assertFalse(parent.isAlias(Runnable.class));
    }

    @Test
    void isServiceFromParent() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);

        assertTrue(child.isService(ServiceFixture.class));
        assertFalse(child.isService(SingletonFixture.class));
    }

    @Test
    void isServiceFromChild() {
        child.bind(ServiceFixture.class, ServiceFixture::make);

        assertTrue(child.isService(ServiceFixture.class));
        assertFalse(parent.isService(ServiceFixture.class));
    }

    @Test
    void isSingletonBindingFromParent() {
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        var freshChild = createChild();

        assertTrue(freshChild.isSingletonBinding(SingletonFixture.class));
        assertTrue(freshChild.isSingleton(SingletonFixture.class));
        assertFalse(freshChild.isSingletonInstance(SingletonFixture.class));
    }

    @Test
    void isSingletonInstanceFromParent() {
        parent.setSingleton(SingletonFixture.class, new SingletonFixture());

        assertTrue(child.isSingletonInstance(SingletonFixture.class));
        assertTrue(child.isSingleton(SingletonFixture.class));
    }

    @Test
    void isSingletonBindingFromChild() {
        child.bindSingleton(SingletonFixture.class, SingletonFixture::make);

        assertTrue(child.isSingletonBinding(SingletonFixture.class));
        assertFalse(parent.isSingletonBinding(SingletonFixture.class));
    }

    @Test
    void isSingletonInstanceFromChild() {
        child.setSingleton(SingletonFixture.class, new SingletonFixture());

        assertTrue(child.isSingletonInstance(SingletonFixture.class));
        assertFalse(parent.isSingletonInstance(SingletonFixture.class));
    }

    @Test
    void hasFromParentWhenRegisteredInParent() {
        parent.register(new BindingProviderFixture());
        var freshChild = createChild();

        assertTrue(freshChild.has(ProvidedFixture.class));
    }

    @Test
    void hasFromChildWhenRegisteredInChild() {
        child.register(new BindingProviderFixture());

        assertTrue(child.has(ProvidedFixture.class));
        assertFalse(parent.has(ProvidedFixture.class));
    }

    @Test
    void isPublishedFromParent() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);

        assertTrue(child.isPublished(ServiceFixture.class));
    }

    @Test
    void isPublishedFromChild() {
        child.bind(ServiceFixture.class, ServiceFixture::make);

        assertTrue(child.isPublished(ServiceFixture.class));
        assertFalse(parent.isPublished(ServiceFixture.class));
    }

    @Test
    void getSingletonFromParentBinding() {
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        var freshChild = createChild();

        var instance = freshChild.getSingleton(SingletonFixture.class);

        assertInstanceOf(SingletonFixture.class, instance);
        assertSame(instance, freshChild.getSingleton(SingletonFixture.class));
        assertFalse(parent.isSingletonInstance(SingletonFixture.class));
    }

    @Test
    void getSingletonFromParentInstance() {
        var parentInstance = new SingletonFixture();
        parent.setSingleton(SingletonFixture.class, parentInstance);

        assertSame(parentInstance, child.getSingleton(SingletonFixture.class));
    }

    @Test
    void getSingletonFromChildOverridesParent() {
        var parentInstance = new SingletonFixture();
        parent.setSingleton(SingletonFixture.class, parentInstance);
        var childInstance = new SingletonFixture();
        child.setSingleton(SingletonFixture.class, childInstance);

        assertSame(childInstance, child.getSingleton(SingletonFixture.class));
        assertNotSame(parentInstance, child.getSingleton(SingletonFixture.class));
    }

    @Test
    void childSingletonDoesNotPolluteParent() {
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        var freshChild = createChild();

        var childInstance = freshChild.getSingleton(SingletonFixture.class);

        assertFalse(parent.isSingletonInstance(SingletonFixture.class));
        assertNotNull(childInstance);
    }

    @Test
    void getServiceFromParent() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);

        var instance = child.getService(ServiceFixture.class, Map.of());

        assertInstanceOf(ServiceFixture.class, instance);
        assertNotSame(instance, child.getService(ServiceFixture.class, Map.of()));
    }

    @Test
    void getServiceFromChild() {
        child.bind(ServiceFixture.class, ServiceFixture::make);

        assertInstanceOf(ServiceFixture.class, child.getService(ServiceFixture.class, Map.of()));
        assertFalse(parent.isService(ServiceFixture.class));
    }

    @Test
    void getAliasedFromParent() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));

        assertInstanceOf(ServiceFixture.class, child.getAliased(CharSequence.class, Map.of()));
    }

    @Test
    void getAliasedFromChild() {
        child.bind(ServiceFixture.class, ServiceFixture::make);
        child.bindAlias(Runnable.class, raw(ServiceFixture.class));

        assertInstanceOf(ServiceFixture.class, child.getAliased(Runnable.class, Map.of()));
        assertFalse(parent.isAlias(Runnable.class));
    }

    @Test
    void parentStateUnchangedAfterChildOperations() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        parent.register(new BindingProviderFixture());
        var freshChild = createChild();

        var aliasesBefore = parent.getData().aliases();
        var servicesBefore = parent.getData().services();
        var singletonsBefore = parent.getData().singletons();
        var singletonInstanceBefore = parent.isSingletonInstance(SingletonFixture.class);
        var providedPublishedBefore = parent.isPublished(ProvidedFixture.class);

        freshChild.get(ServiceFixture.class);
        freshChild.getService(ServiceFixture.class, Map.of());
        freshChild.getAliased(CharSequence.class, Map.of());
        freshChild.getSingleton(SingletonFixture.class);
        freshChild.get(ProvidedFixture.class);

        assertEquals(aliasesBefore, parent.getData().aliases());
        assertEquals(servicesBefore, parent.getData().services());
        assertEquals(singletonsBefore, parent.getData().singletons());
        assertEquals(singletonInstanceBefore, parent.isSingletonInstance(SingletonFixture.class));
        assertEquals(providedPublishedBefore, parent.isPublished(ProvidedFixture.class));
    }

    @Test
    void providerFromChildPublishedInChild() {
        child.register(new BindingProviderFixture());

        assertTrue(child.has(ProvidedFixture.class));
        assertInstanceOf(ProvidedFixture.class, child.get(ProvidedFixture.class));
        assertFalse(parent.isPublished(ProvidedFixture.class));
    }

    @Test
    void providerFromParentPublishedInChild() {
        parent.register(new BindingProviderFixture());
        var freshChild = createChild();

        assertTrue(freshChild.has(ProvidedFixture.class));
        assertInstanceOf(ProvidedFixture.class, freshChild.get(ProvidedFixture.class));
        assertFalse(parent.isPublished(ProvidedFixture.class));
    }

    @Test
    void publishUnknownIsNoOp() {
        child.publish(SingletonFixture.class);

        assertFalse(child.isPublished(SingletonFixture.class));
    }

    @Test
    void resolvesServiceFromParentThenChild() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        var freshChild = createChild();
        assertNotNull(freshChild.getService(ServiceFixture.class, java.util.Map.of()));

        freshChild.bind(SingletonFixture.class, SingletonFixture::make);
        assertNotNull(freshChild.getService(SingletonFixture.class, java.util.Map.of()));
    }

    @Test
    void resolvesAliasFromParentThenChild() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));
        var freshChild = createChild();
        assertNotNull(freshChild.get(CharSequence.class));

        freshChild.bind(ServiceFixture.class, ServiceFixture::make);
        freshChild.bindAlias(Runnable.class, raw(ServiceFixture.class));
        assertNotNull(freshChild.get(Runnable.class));
    }

    @Test
    void getAliasedThrowsWhenNeitherHasAlias() {
        // Neither child nor parent has the alias → falls through to the failing super lookup.
        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> child.getAliased(Runnable.class, Map.of()));
    }

    @Test
    void getAliasedIdReadsTheChildThenTheParent() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));
        ChildContainer localChild = createChild();

        assertEquals(ServiceFixture.class, localChild.getAliasedId(CharSequence.class));
        assertNull(localChild.getAliasedId(Runnable.class));

        localChild.bindAlias(Runnable.class, raw(SingletonFixture.class));

        assertEquals(SingletonFixture.class, localChild.getAliasedId(Runnable.class));
    }

    @Test
    void snapshotChildResolvesAnUnbuiltParentSingletonItself() {
        // Boot: two singletons on the parent, one resolved before any child exists
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        parent.bindSingleton(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));
        Object shared = parent.getSingleton(SingletonFixture.class);

        // The request loop begins from one snapshot
        ChildContainer localChild = createChild();

        // The resolved one is shared, and the unresolved one is the child's own
        assertSame(shared, localChild.get(SingletonFixture.class, Map.of()));
        assertInstanceOf(ServiceFixture.class, localChild.get(ServiceFixture.class, Map.of()));
        assertFalse(parent.isSingletonInstance(ServiceFixture.class));

        // The alias reaches the same copy, so the request holds one instance of it
        assertSame(
                localChild.get(ServiceFixture.class, Map.of()),
                localChild.get(CharSequence.class, Map.of()));
        assertFalse(parent.isSingletonInstance(ServiceFixture.class));
    }

    @Test
    void getAliasedReusesAParentSingletonTheParentAlreadyBuilt() {
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        parent.bindAlias(Runnable.class, raw(SingletonFixture.class));
        Object shared = parent.getSingleton(SingletonFixture.class);
        ChildContainer localChild = createChild();

        // The parent holds the instance, so the alias reaches it rather than rebuilding
        assertSame(shared, localChild.get(Runnable.class, Map.of()));
    }

    @Test
    void getAliasedPublishesADeferredParentTargetInTheChild() {
        parent.register(new PublishingProviderFixture());
        parent.bindAlias(CharSequence.class, raw(ProvidedFixture.class));
        ChildContainer localChild = createChild();

        // The child holds the same callback, so it publishes into itself
        Object fromId = localChild.get(ProvidedFixture.class, Map.of());
        Object fromAlias = localChild.get(CharSequence.class, Map.of());

        assertSame(fromId, fromAlias);
        assertFalse(parent.isPublished(ProvidedFixture.class));
        assertFalse(parent.isSingletonInstance(ProvidedFixture.class));
    }

    @Test
    void getAliasedStopsWhereTheParentStops() {
        // The parent answers Runnable as a singleton, so it never reaches the rest
        parent.bindAlias(CharSequence.class, raw(Runnable.class));
        parent.bindSingleton(raw(Runnable.class), SingletonFixture::make);
        parent.bindAlias(Runnable.class, raw(ServiceFixture.class));
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        ChildContainer localChild = createChild();

        assertInstanceOf(
                SingletonFixture.class, localChild.getAliased(CharSequence.class, Map.of()));
        assertFalse(parent.isSingletonInstance(Runnable.class));
    }

    @Test
    void aChainOntoAnUnbuiltParentSingletonResolvesInTheChild() {
        // outer → middle → the singleton, none of it built in the parent
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        parent.bindAlias(Runnable.class, raw(SingletonFixture.class));
        parent.bindAlias(CharSequence.class, raw(Runnable.class));
        ChildContainer localChild = createChild();

        Object instance = localChild.get(CharSequence.class, Map.of());

        assertInstanceOf(SingletonFixture.class, instance);
        assertSame(instance, localChild.get(SingletonFixture.class, Map.of()));
        assertFalse(parent.isSingletonInstance(SingletonFixture.class));
    }

    @Test
    void getAliasedReusesAParentTargetTheParentAlreadyPublished() {
        parent.register(new PublishingProviderFixture());
        parent.bindAlias(CharSequence.class, raw(ProvidedFixture.class));
        // The parent publishes at boot, so the request reuses what it holds
        Object shared = parent.get(ProvidedFixture.class, Map.of());
        ChildContainer localChild = createChild();

        assertSame(shared, localChild.getAliased(CharSequence.class, Map.of()));
    }

    @Test
    void getAliasedStopsAtAParentServiceInTheChain() {
        // The parent answers Runnable as a service, so it never reaches the rest
        parent.bindAlias(CharSequence.class, raw(Runnable.class));
        parent.bind(raw(Runnable.class), ServiceFixture::make);
        parent.bindAlias(Runnable.class, raw(SingletonFixture.class));
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        ChildContainer localChild = createChild();

        assertInstanceOf(
                ServiceFixture.class, localChild.getAliased(CharSequence.class, Map.of()));
        assertFalse(parent.isSingletonInstance(SingletonFixture.class));
    }

    @Test
    void isSingletonBindingReadsTheChildThenTheParent() {
        ChildContainer localChild = createChild();
        localChild.bindSingleton(ServiceFixture.class, ServiceFixture::make);
        // A snapshot copies the parent's bindings, so only a later one reaches the fallback
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);

        assertTrue(localChild.isSingletonBinding(ServiceFixture.class));
        assertTrue(localChild.isSingletonBinding(SingletonFixture.class));
        assertFalse(localChild.isSingletonBinding(Runnable.class));
    }

    @Test
    void getAliasedStopsAtADeferredHopInTheChain() {
        // The parent publishes before it reads any map, so it stops at the deferred hop
        parent.register(new PublishingProviderFixture());
        parent.bindAlias(CharSequence.class, raw(ProvidedFixture.class));
        parent.bindAlias(ProvidedFixture.class, raw(ServiceFixture.class));
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        ChildContainer localChild = createChild();

        // The child holds the same callback, so it publishes into itself
        Object fromId = localChild.get(ProvidedFixture.class, Map.of());

        assertSame(fromId, localChild.getAliased(CharSequence.class, Map.of()));
        assertFalse(parent.isPublished(ProvidedFixture.class));
        assertFalse(parent.isSingletonInstance(ProvidedFixture.class));
    }

    @Test
    void getAliasedStopsAtAParentInstanceInTheChain() {
        // The parent holds Runnable as an instance, so it never reaches the rest
        var shared = new SingletonFixture();
        parent.bindAlias(CharSequence.class, raw(Runnable.class));
        parent.setSingleton(raw(Runnable.class), shared);
        parent.bindAlias(Runnable.class, raw(ServiceFixture.class));
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        ChildContainer localChild = createChild();

        assertSame(shared, localChild.getAliased(CharSequence.class, Map.of()));
    }
}
