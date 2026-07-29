/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.container.manager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.enum_.InvalidReferenceMode;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;
import io.valkyrja.tests.fixtures.container.ServiceClass;
import io.valkyrja.tests.fixtures.container.SingletonClass;
import io.valkyrja.tests.fixtures.container.provider.ProvidedClass;
import io.valkyrja.tests.fixtures.container.provider.ProvidedSecondaryClass;
import io.valkyrja.tests.fixtures.container.provider.ProviderClass;
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
        container.bind(ServiceClass.class, ServiceClass::make);

        assertTrue(container.has(ServiceClass.class));
        assertTrue(container.isService(ServiceClass.class));
        assertTrue(container.isPublished(ServiceClass.class));
        assertFalse(container.isAlias(ServiceClass.class));
        assertFalse(container.isSingleton(ServiceClass.class));

        var service = container.get(ServiceClass.class);
        assertInstanceOf(ServiceClass.class, service);
        // A bound service returns a fresh instance each time.
        assertNotSame(service, container.get(ServiceClass.class));
        assertNotSame(service, container.getService(ServiceClass.class, Map.of()));
    }

    @Test
    void bindAlias() {
        var container = new Container();
        container.bind(ServiceClass.class, ServiceClass::make);
        container.bindAlias(CharSequence.class, raw(ServiceClass.class));

        assertTrue(container.has(CharSequence.class));
        assertTrue(container.isAlias(CharSequence.class));
        assertTrue(container.isPublished(ServiceClass.class));
        assertFalse(container.isService(CharSequence.class));

        Object service = container.get(CharSequence.class);
        assertInstanceOf(ServiceClass.class, service);
        assertNotSame(service, container.get(CharSequence.class));
        assertInstanceOf(ServiceClass.class, container.getAliased(CharSequence.class, Map.of()));
    }

    @Test
    void bindSingleton() {
        var container = new Container();
        container.bindSingleton(SingletonClass.class, SingletonClass::make);

        assertTrue(container.has(SingletonClass.class));
        assertTrue(container.isSingleton(SingletonClass.class));
        assertTrue(container.isService(SingletonClass.class));
        assertTrue(container.isPublished(SingletonClass.class));
        assertFalse(container.isAlias(SingletonClass.class));

        var service = container.get(SingletonClass.class);
        assertInstanceOf(SingletonClass.class, service);
        // A bound singleton returns the same instance each time.
        assertSame(service, container.get(SingletonClass.class));
        assertSame(service, container.getSingleton(SingletonClass.class));
    }

    @Test
    void provided() {
        var container = new Container();
        container.register(new ProviderClass());

        assertTrue(container.has(ProvidedClass.class));
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
                () -> container.getSingleton(ServiceClass.class));
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
                () -> container.getService(ServiceClass.class, Map.of()));
    }

    @Test
    void getDataReflectsRegisteredCallbacks() {
        var container = new Container();
        container.register(new ProviderClass());

        var data = container.getData();

        assertTrue(data.callbacks().containsKey(ProvidedClass.class));
        assertTrue(data.callbacks().containsKey(ProvidedSecondaryClass.class));
        assertTrue(data.aliases().isEmpty());
        assertTrue(data.services().isEmpty());
        assertTrue(data.singletons().isEmpty());
    }

    @Test
    void setFromDataImportsCallbacks() {
        var source = new Container();
        source.register(new ProviderClass());
        var data = source.getData();

        var container = new Container();
        assertFalse(container.has(ProvidedClass.class));

        container.setFromData(data);

        assertTrue(container.has(ProvidedClass.class));
        assertTrue(container.getData().callbacks().containsKey(ProvidedClass.class));
    }

    @Test
    void constructWithDataImportsCallbacks() {
        var source = new Container();
        source.register(new ProviderClass());
        var data = source.getData();

        var container = new Container(data);

        assertTrue(container.has(ProvidedClass.class));
        assertTrue(container.getData().callbacks().containsKey(ProvidedClass.class));
    }

    @Test
    void getResolvesDeferredProviderThenFallsBack() {
        var container = new Container();
        container.register(new ProviderClass());

        Object provided = container.get(ProvidedClass.class);

        assertInstanceOf(ProvidedClass.class, provided);
        assertTrue(container.isPublished(ProvidedClass.class));
    }

    @Test
    void newInstanceOrThrowModeCreatesInstantiableType() {
        var container = new Container();

        var object =
                container.get(
                        SingletonClass.class,
                        Map.of(),
                        InvalidReferenceMode.NEW_INSTANCE_OR_THROW_EXCEPTION);

        assertInstanceOf(SingletonClass.class, object);
    }

    @Test
    void newInstanceOrThrowModeThrowsWhenConstructionFails() {
        var container = new Container();

        // ServiceClass has no no-arg constructor, so reflective instantiation fails.
        assertThrows(
                ContainerInvalidArgumentException.class,
                () ->
                        container.get(
                                ServiceClass.class,
                                Map.of(),
                                InvalidReferenceMode.NEW_INSTANCE_OR_THROW_EXCEPTION));
    }

    @Test
    void throwExceptionModeAlwaysThrows() {
        var container = new Container();

        assertThrows(
                ContainerInvalidArgumentException.class,
                () ->
                        container.get(
                                ServiceClass.class,
                                Map.of(),
                                InvalidReferenceMode.THROW_EXCEPTION));
    }

    @Test
    void newInstanceOrThrowModeThrowsForNonInstantiableType() {
        var container = new Container();

        // An interface cannot be instantiated reflectively.
        assertThrows(
                ContainerInvalidArgumentException.class,
                () ->
                        container.get(
                                Runnable.class,
                                Map.of(),
                                InvalidReferenceMode.NEW_INSTANCE_OR_THROW_EXCEPTION));
    }

    @Test
    void getSingletonWithNullFactoryThrows() {
        var container = new Container();
        // Singleton factory yields null → the cache-put is skipped and the lookup ultimately fails.
        container.bindSingleton(SingletonClass.class, (c, a) -> null);

        assertThrows(
                ContainerInvalidArgumentException.class,
                () -> container.getSingleton(SingletonClass.class));
    }
}
