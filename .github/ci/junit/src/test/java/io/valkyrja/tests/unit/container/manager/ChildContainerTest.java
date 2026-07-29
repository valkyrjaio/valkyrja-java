/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.container.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
