/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.container.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.NativeChildContainer;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.throwable.exception.ContainerCyclicAliasException;
import io.valkyrja.container.throwable.exception.ContainerInvalidReferenceException;
import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;
import io.valkyrja.tests.fixtures.container.ServiceFixture;
import io.valkyrja.tests.fixtures.container.SingletonFixture;
import io.valkyrja.tests.fixtures.container.provider.BindingProviderFixture;
import io.valkyrja.tests.fixtures.container.provider.ProvidedFixture;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Per-request child container with child-first reads and parent fallback via field access. */
final class NativeChildContainerTest {

    private Container parent;
    private NativeChildContainer child;

    @SuppressWarnings("unchecked")
    private static <T> Class<T> raw(Class<?> type) {
        return (Class<T>) type;
    }

    @BeforeEach
    void setUp() {
        parent = new Container();
        child = new NativeChildContainer(parent);
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
    void getAliasedIdFromParent() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));

        assertEquals(ServiceFixture.class, child.getAliasedId(CharSequence.class));
        assertNull(child.getAliasedId(Runnable.class));
    }

    @Test
    void getAliasedIdFromChildTakesPrecedence() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));
        child.bindAlias(CharSequence.class, raw(SingletonFixture.class));

        assertEquals(SingletonFixture.class, child.getAliasedId(CharSequence.class));
        assertEquals(ServiceFixture.class, parent.getAliasedId(CharSequence.class));
    }

    @Test
    void isDeferredFromParent() {
        parent.register(new BindingProviderFixture());

        assertTrue(child.isDeferred(ProvidedFixture.class));
        assertFalse(child.isDeferred(Runnable.class));
    }

    @Test
    void isDeferredFromChild() {
        child.register(new BindingProviderFixture());

        assertTrue(child.isDeferred(ProvidedFixture.class));
        assertFalse(parent.isDeferred(ProvidedFixture.class));
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

        assertTrue(child.isSingletonBinding(SingletonFixture.class));
        assertTrue(child.isSingleton(SingletonFixture.class));
        assertFalse(child.isSingletonInstance(SingletonFixture.class));
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

        assertTrue(child.has(ProvidedFixture.class));
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

        var instance = child.getSingleton(SingletonFixture.class);

        assertInstanceOf(SingletonFixture.class, instance);
        assertSame(instance, child.getSingleton(SingletonFixture.class));
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

        var childInstance = child.getSingleton(SingletonFixture.class);

        assertFalse(parent.isSingletonInstance(SingletonFixture.class));
        assertNotNull(childInstance);
    }

    @Test
    void getServiceFromParentPassesChildAsContainer() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);

        var instance = child.getService(ServiceFixture.class, Map.of());

        assertInstanceOf(ServiceFixture.class, instance);
        assertSame(child, instance.getContainer());
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
    void parentStateUnchangedAfterChildOperations() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        parent.register(new BindingProviderFixture());

        var aliasesBefore = parent.getData().aliases();
        var servicesBefore = parent.getData().services();
        var singletonsBefore = parent.getData().singletons();
        var singletonInstanceBefore = parent.isSingletonInstance(SingletonFixture.class);
        var providedPublishedBefore = parent.isPublished(ProvidedFixture.class);

        child.get(ServiceFixture.class);
        child.getService(ServiceFixture.class, Map.of());
        child.getAliased(CharSequence.class, Map.of());
        child.getSingleton(SingletonFixture.class);
        child.get(ProvidedFixture.class);

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

        assertTrue(child.has(ProvidedFixture.class));
        assertInstanceOf(ProvidedFixture.class, child.get(ProvidedFixture.class));
        assertFalse(parent.isPublished(ProvidedFixture.class));
    }

    @Test
    void unknownTypeThrows() {
        // Nothing registered in child or parent — exercises the neither-has-it null paths.
        assertFalse(child.has(SingletonFixture.class));
        assertThrows(
                ContainerInvalidReferenceException.class, () -> child.get(SingletonFixture.class));
    }

    @Test
    void publishUnknownIsNoOp() {
        child.publish(SingletonFixture.class);

        assertFalse(child.isPublished(SingletonFixture.class));
    }

    @Test
    void resolvesSingletonFromParentAndCaches() {
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        var freshChild = new NativeChildContainer(parent);

        assertNotNull(freshChild.getSingleton(SingletonFixture.class));
    }

    @Test
    void resolvesAliasFromParentThenChild() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));
        var freshChild = new NativeChildContainer(parent);
        assertNotNull(freshChild.get(CharSequence.class));

        freshChild.bind(ServiceFixture.class, ServiceFixture::make);
        freshChild.bindAlias(Runnable.class, raw(ServiceFixture.class));
        assertNotNull(freshChild.get(Runnable.class));
    }

    @Test
    void getSingletonThrowsWhenNeitherBound() {
        // Neither child nor parent has the singleton binding → null path, then a failing lookup.
        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> child.getSingleton(SingletonFixture.class));
    }

    @Test
    void getSingletonWithNullParentFactoryThrows() {
        parent.bindSingleton(SingletonFixture.class, (c, a) -> null);

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> child.getSingleton(SingletonFixture.class));
    }

    @Test
    void providerFromChildPublishesOnlyOnce() {
        child.register(new BindingProviderFixture());
        // Resolve twice — the second resolution finds the provider already published.
        assertInstanceOf(ProvidedFixture.class, child.get(ProvidedFixture.class));
        assertInstanceOf(ProvidedFixture.class, child.get(ProvidedFixture.class));
    }

    @Test
    void getSingletonFromChildBinding() {
        child.bindSingleton(SingletonFixture.class, SingletonFixture::make);

        // Child has its own singleton binding → creates and caches without consulting the parent.
        assertInstanceOf(SingletonFixture.class, child.getSingleton(SingletonFixture.class));
    }

    // -----------------------------------------------------------------------
    // Parent alias resolution — the parent's binding, resolved in the child
    // -----------------------------------------------------------------------

    /** A parent hydrated from data carries a callback and a service with no published map. */
    private void hydrateParentWithUnrunCallback() {
        parent.setFromData(
                new ContainerData(
                        Map.of(),
                        Map.of(
                                ServiceFixture.class,
                                c -> c.bind(ServiceFixture.class, ServiceFixture::make)),
                        Map.of(ServiceFixture.class, ServiceFixture::make),
                        Map.of()));
    }

    @Test
    void getAliasedReusesAResolvedParentSingleton() {
        SingletonFixture parentInstance = new SingletonFixture();
        parent.setSingleton(SingletonFixture.class, parentInstance);
        parent.bindAlias(CharSequence.class, raw(SingletonFixture.class));

        assertSame(parentInstance, child.getAliased(CharSequence.class, Map.of()));
    }

    @Test
    void getAliasedBuildsAnUnresolvedParentSingletonInTheChild() {
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        parent.bindAlias(CharSequence.class, raw(SingletonFixture.class));

        Object instance = child.getAliased(CharSequence.class, Map.of());

        assertInstanceOf(SingletonFixture.class, instance);
        // One request holds one instance, and the parent still holds none
        assertSame(instance, child.getAliased(CharSequence.class, Map.of()));
        assertFalse(parent.isSingletonInstance(SingletonFixture.class));
    }

    @Test
    void getAliasedPublishesADeferredParentTargetInTheChild() {
        parent.register(new BindingProviderFixture());
        parent.bindAlias(CharSequence.class, raw(ProvidedFixture.class));

        assertInstanceOf(ProvidedFixture.class, child.getAliased(CharSequence.class, Map.of()));
        assertFalse(parent.isPublished(ProvidedFixture.class));
    }

    @Test
    void getAliasedRunsTheParentsBindingWhenThePublishedMapIsLost() {
        hydrateParentWithUnrunCallback();
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));

        assertTrue(parent.isService(ServiceFixture.class));
        assertFalse(parent.isPublished(ServiceFixture.class));

        ServiceFixture instance =
                assertInstanceOf(
                        ServiceFixture.class, child.getAliased(CharSequence.class, Map.of()));

        assertSame(child, instance.getContainer());
        assertFalse(parent.isPublished(ServiceFixture.class));
    }

    @Test
    void getAliasedStopsOnACyclicParentAliasChain() {
        parent.bindAlias(CharSequence.class, raw(Runnable.class));
        parent.bindAlias(Runnable.class, raw(CharSequence.class));

        assertThrows(
                ContainerCyclicAliasException.class,
                () -> child.getAliased(CharSequence.class, Map.of()));
    }

    @Test
    void getAliasedWalksAParentAliasChainToAnUnboundType() {
        parent.bindAlias(Runnable.class, raw(CharSequence.class));
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));

        // The walk runs out of aliases, so the lookup reports the reference
        assertThrows(
                ContainerInvalidReferenceException.class,
                () -> child.getAliased(Runnable.class, Map.of()));
    }

    @Test
    void getAliasedFromChildResolvesInTheChild() {
        child.bind(ServiceFixture.class, ServiceFixture::make);
        child.bindAlias(CharSequence.class, raw(ServiceFixture.class));

        ServiceFixture instance =
                assertInstanceOf(
                        ServiceFixture.class, child.getAliased(CharSequence.class, Map.of()));

        assertSame(child, instance.getContainer());
    }

    @Test
    void aParentDeclaredAliasUsesTheParentsBindingInTheChild() {
        parent.bindSingleton(SingletonFixture.class, SingletonFixture::make);
        parent.bind(
                ServiceFixture.class,
                (c, a) -> {
                    c.getSingleton(SingletonFixture.class);

                    return ServiceFixture.make(c, a);
                });
        parent.bindAlias(CharSequence.class, raw(ServiceFixture.class));

        ServiceFixture instance =
                assertInstanceOf(
                        ServiceFixture.class, child.getAliased(CharSequence.class, Map.of()));

        // The factory ran with the child, so its dependency cached in the child
        assertSame(child, instance.getContainer());
        assertFalse(parent.isSingletonInstance(SingletonFixture.class));
    }

    @Test
    void getSingletonInstanceReadsTheChildThenTheParent() {
        SingletonFixture parentInstance = new SingletonFixture();
        parent.setSingleton(SingletonFixture.class, parentInstance);

        assertSame(parentInstance, child.getSingletonInstance(SingletonFixture.class));
        assertNull(child.getSingletonInstance(ServiceFixture.class));

        SingletonFixture childInstance = new SingletonFixture();
        child.setSingleton(SingletonFixture.class, childInstance);

        assertSame(childInstance, child.getSingletonInstance(SingletonFixture.class));
    }

    @Test
    void getServiceCallableReadsTheChildThenTheParent() {
        parent.bind(ServiceFixture.class, ServiceFixture::make);

        assertNotNull(child.getServiceCallable(ServiceFixture.class));
        assertNull(child.getServiceCallable(SingletonFixture.class));

        child.bind(SingletonFixture.class, SingletonFixture::make);

        assertNotNull(child.getServiceCallable(SingletonFixture.class));
    }
}
