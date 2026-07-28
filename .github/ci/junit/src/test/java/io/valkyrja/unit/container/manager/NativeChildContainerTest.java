/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.container.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.NativeChildContainer;
import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;
import io.valkyrja.fixtures.container.ServiceClass;
import io.valkyrja.fixtures.container.SingletonClass;
import io.valkyrja.fixtures.container.provider.BindingProviderClass;
import io.valkyrja.fixtures.container.provider.ProvidedClass;
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
        parent.bind(ServiceClass.class, ServiceClass::make);
        parent.bindAlias(CharSequence.class, raw(ServiceClass.class));

        assertTrue(child.isAlias(CharSequence.class));
        assertFalse(child.isAlias(Runnable.class));
    }

    @Test
    void isAliasFromChild() {
        child.bind(ServiceClass.class, ServiceClass::make);
        child.bindAlias(Runnable.class, raw(ServiceClass.class));

        assertTrue(child.isAlias(Runnable.class));
        assertFalse(parent.isAlias(Runnable.class));
    }

    @Test
    void isServiceFromParent() {
        parent.bind(ServiceClass.class, ServiceClass::make);

        assertTrue(child.isService(ServiceClass.class));
        assertFalse(child.isService(SingletonClass.class));
    }

    @Test
    void isServiceFromChild() {
        child.bind(ServiceClass.class, ServiceClass::make);

        assertTrue(child.isService(ServiceClass.class));
        assertFalse(parent.isService(ServiceClass.class));
    }

    @Test
    void isSingletonBindingFromParent() {
        parent.bindSingleton(SingletonClass.class, SingletonClass::make);

        assertTrue(child.isSingletonBinding(SingletonClass.class));
        assertTrue(child.isSingleton(SingletonClass.class));
        assertFalse(child.isSingletonInstance(SingletonClass.class));
    }

    @Test
    void isSingletonInstanceFromParent() {
        parent.setSingleton(SingletonClass.class, new SingletonClass());

        assertTrue(child.isSingletonInstance(SingletonClass.class));
        assertTrue(child.isSingleton(SingletonClass.class));
    }

    @Test
    void isSingletonBindingFromChild() {
        child.bindSingleton(SingletonClass.class, SingletonClass::make);

        assertTrue(child.isSingletonBinding(SingletonClass.class));
        assertFalse(parent.isSingletonBinding(SingletonClass.class));
    }

    @Test
    void isSingletonInstanceFromChild() {
        child.setSingleton(SingletonClass.class, new SingletonClass());

        assertTrue(child.isSingletonInstance(SingletonClass.class));
        assertFalse(parent.isSingletonInstance(SingletonClass.class));
    }

    @Test
    void hasFromParentWhenRegisteredInParent() {
        parent.register(new BindingProviderClass());

        assertTrue(child.has(ProvidedClass.class));
    }

    @Test
    void hasFromChildWhenRegisteredInChild() {
        child.register(new BindingProviderClass());

        assertTrue(child.has(ProvidedClass.class));
        assertFalse(parent.has(ProvidedClass.class));
    }

    @Test
    void isPublishedFromParent() {
        parent.bind(ServiceClass.class, ServiceClass::make);

        assertTrue(child.isPublished(ServiceClass.class));
    }

    @Test
    void isPublishedFromChild() {
        child.bind(ServiceClass.class, ServiceClass::make);

        assertTrue(child.isPublished(ServiceClass.class));
        assertFalse(parent.isPublished(ServiceClass.class));
    }

    @Test
    void getSingletonFromParentBinding() {
        parent.bindSingleton(SingletonClass.class, SingletonClass::make);

        var instance = child.getSingleton(SingletonClass.class);

        assertInstanceOf(SingletonClass.class, instance);
        assertSame(instance, child.getSingleton(SingletonClass.class));
        assertFalse(parent.isSingletonInstance(SingletonClass.class));
    }

    @Test
    void getSingletonFromParentInstance() {
        var parentInstance = new SingletonClass();
        parent.setSingleton(SingletonClass.class, parentInstance);

        assertSame(parentInstance, child.getSingleton(SingletonClass.class));
    }

    @Test
    void getSingletonFromChildOverridesParent() {
        var parentInstance = new SingletonClass();
        parent.setSingleton(SingletonClass.class, parentInstance);
        var childInstance = new SingletonClass();
        child.setSingleton(SingletonClass.class, childInstance);

        assertSame(childInstance, child.getSingleton(SingletonClass.class));
        assertNotSame(parentInstance, child.getSingleton(SingletonClass.class));
    }

    @Test
    void childSingletonDoesNotPolluteParent() {
        parent.bindSingleton(SingletonClass.class, SingletonClass::make);

        var childInstance = child.getSingleton(SingletonClass.class);

        assertFalse(parent.isSingletonInstance(SingletonClass.class));
        assertNotNull(childInstance);
    }

    @Test
    void getServiceFromParentPassesChildAsContainer() {
        parent.bind(ServiceClass.class, ServiceClass::make);

        var instance = child.getService(ServiceClass.class, Map.of());

        assertInstanceOf(ServiceClass.class, instance);
        assertSame(child, instance.getContainer());
        assertNotSame(instance, child.getService(ServiceClass.class, Map.of()));
    }

    @Test
    void getServiceFromChild() {
        child.bind(ServiceClass.class, ServiceClass::make);

        assertInstanceOf(ServiceClass.class, child.getService(ServiceClass.class, Map.of()));
        assertFalse(parent.isService(ServiceClass.class));
    }

    @Test
    void getAliasedFromParent() {
        parent.bind(ServiceClass.class, ServiceClass::make);
        parent.bindAlias(CharSequence.class, raw(ServiceClass.class));

        assertInstanceOf(ServiceClass.class, child.getAliased(CharSequence.class, Map.of()));
    }

    @Test
    void parentStateUnchangedAfterChildOperations() {
        parent.bind(ServiceClass.class, ServiceClass::make);
        parent.bindAlias(CharSequence.class, raw(ServiceClass.class));
        parent.bindSingleton(SingletonClass.class, SingletonClass::make);
        parent.register(new BindingProviderClass());

        var aliasesBefore = parent.getData().aliases();
        var servicesBefore = parent.getData().services();
        var singletonsBefore = parent.getData().singletons();
        var singletonInstanceBefore = parent.isSingletonInstance(SingletonClass.class);
        var providedPublishedBefore = parent.isPublished(ProvidedClass.class);

        child.get(ServiceClass.class);
        child.getService(ServiceClass.class, Map.of());
        child.getAliased(CharSequence.class, Map.of());
        child.getSingleton(SingletonClass.class);
        child.get(ProvidedClass.class);

        assertEquals(aliasesBefore, parent.getData().aliases());
        assertEquals(servicesBefore, parent.getData().services());
        assertEquals(singletonsBefore, parent.getData().singletons());
        assertEquals(singletonInstanceBefore, parent.isSingletonInstance(SingletonClass.class));
        assertEquals(providedPublishedBefore, parent.isPublished(ProvidedClass.class));
    }

    @Test
    void providerFromChildPublishedInChild() {
        child.register(new BindingProviderClass());

        assertTrue(child.has(ProvidedClass.class));
        assertInstanceOf(ProvidedClass.class, child.get(ProvidedClass.class));
        assertFalse(parent.isPublished(ProvidedClass.class));
    }

    @Test
    void providerFromParentPublishedInChild() {
        parent.register(new BindingProviderClass());

        assertTrue(child.has(ProvidedClass.class));
        assertInstanceOf(ProvidedClass.class, child.get(ProvidedClass.class));
        assertFalse(parent.isPublished(ProvidedClass.class));
    }

    @Test
    void unknownTypeFallsBackToNewInstance() {
        // Nothing registered in child or parent — exercises the neither-has-it null paths.
        assertFalse(child.has(SingletonClass.class));
        assertInstanceOf(SingletonClass.class, child.get(SingletonClass.class));
    }

    @Test
    void publishUnknownIsNoOp() {
        child.publish(SingletonClass.class);

        assertFalse(child.isPublished(SingletonClass.class));
    }

    @Test
    void resolvesSingletonFromParentAndCaches() {
        parent.bindSingleton(SingletonClass.class, SingletonClass::make);
        var freshChild = new NativeChildContainer(parent);

        assertNotNull(freshChild.getSingleton(SingletonClass.class));
    }

    @Test
    void resolvesAliasFromParentThenChild() {
        parent.bind(ServiceClass.class, ServiceClass::make);
        parent.bindAlias(CharSequence.class, raw(ServiceClass.class));
        var freshChild = new NativeChildContainer(parent);
        assertNotNull(freshChild.get(CharSequence.class));

        freshChild.bind(ServiceClass.class, ServiceClass::make);
        freshChild.bindAlias(Runnable.class, raw(ServiceClass.class));
        assertNotNull(freshChild.get(Runnable.class));
    }

    @Test
    void getSingletonThrowsWhenNeitherBound() {
        // Neither child nor parent has the singleton binding → null path, then a failing lookup.
        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> child.getSingleton(SingletonClass.class));
    }

    @Test
    void getSingletonWithNullParentFactoryThrows() {
        parent.bindSingleton(SingletonClass.class, (c, a) -> null);

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> child.getSingleton(SingletonClass.class));
    }

    @Test
    void providerFromChildPublishesOnlyOnce() {
        child.register(new BindingProviderClass());
        // Resolve twice — the second resolution finds the provider already published.
        assertInstanceOf(ProvidedClass.class, child.get(ProvidedClass.class));
        assertInstanceOf(ProvidedClass.class, child.get(ProvidedClass.class));
    }

    @Test
    void getSingletonFromChildBinding() {
        child.bindSingleton(SingletonClass.class, SingletonClass::make);

        // Child has its own singleton binding → creates and caches without consulting the parent.
        assertInstanceOf(SingletonClass.class, child.getSingleton(SingletonClass.class));
    }
}
