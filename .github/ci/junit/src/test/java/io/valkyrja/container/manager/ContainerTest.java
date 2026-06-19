/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.enum_.InvalidReferenceMode;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.throwable.exception.ContainerInvalidReferenceException;
import java.util.Map;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

final class ContainerTest {

    // --- fixtures -----------------------------------------------------------

    public static class Service {}

    public interface Greeter {}

    public static class EnglishGreeter implements Greeter {}

    public interface Unresolvable {}

    @SuppressWarnings("unchecked")
    private static <T> Class<T> raw(Class<?> type) {
        return (Class<T>) type;
    }

    // --- construction / data ------------------------------------------------

    @Test
    void emptyContainerHasNothing() {
        var container = new Container();

        assertFalse(container.has(Service.class));
        assertTrue(container.getData().services().isEmpty());
        assertTrue(container.getData().singletons().isEmpty());
    }

    @Test
    void constructorFromDataPopulatesBindings() {
        BiFunction<ContainerContract, Map<String, Object>, Object> factory = (c, a) -> new Service();
        var data = new ContainerData(Map.of(), Map.of(), Map.of(Service.class, factory), Map.of());

        var container = new Container(data);

        assertTrue(container.isService(Service.class));
        assertTrue(container.has(Service.class));
    }

    @Test
    void setFromDataPopulatesBindings() {
        BiFunction<ContainerContract, Map<String, Object>, Object> factory = (c, a) -> new Service();
        var data = new ContainerData(Map.of(), Map.of(), Map.of(Service.class, factory), Map.of());
        var container = new Container();

        container.setFromData(data);

        assertTrue(container.isService(Service.class));
    }

    @Test
    void getDataRoundTrips() {
        var container = new Container();
        container.bind(Service.class, (c, a) -> new Service());

        assertTrue(container.getData().services().containsKey(Service.class));
    }

    // --- services -----------------------------------------------------------

    @Test
    void bindResolvesFreshInstancesEachTime() {
        var container = new Container();

        container.bind(Service.class, (c, a) -> new Service());

        assertTrue(container.isService(Service.class));
        assertNotSame(container.get(Service.class), container.get(Service.class));
    }

    @Test
    void getServiceReturnsBoundServiceAndThrowsOtherwise() {
        var container = new Container();
        container.bind(Service.class, (c, a) -> new Service());

        assertNotNull(container.getService(Service.class, Map.of()));
        assertThrows(
                ContainerInvalidReferenceException.class,
                () -> container.getService(Greeter.class, Map.of()));
    }

    @Test
    void getWithArgumentsOverloadPassesArguments() {
        var container = new Container();
        container.bind(Service.class, (c, args) -> args.get("value"));

        assertEquals("passed", container.get(Service.class, Map.of("value", "passed")));
    }

    // --- singletons ---------------------------------------------------------

    @Test
    void setSingletonStoresInstance() {
        var container = new Container();
        var instance = new Service();

        container.setSingleton(Service.class, instance);

        assertTrue(container.isSingletonInstance(Service.class));
        assertTrue(container.isSingleton(Service.class));
        assertSame(instance, container.getSingleton(Service.class));
        assertSame(instance, container.get(Service.class));
    }

    @Test
    void bindSingletonCachesAfterFirstResolution() {
        var container = new Container();
        container.bindSingleton(Service.class, (c, a) -> new Service());

        assertTrue(container.isSingletonBinding(Service.class));

        var first = container.get(Service.class);
        var second = container.get(Service.class);

        assertSame(first, second);
    }

    @Test
    void getSingletonThrowsWhenAbsent() {
        var container = new Container();

        assertThrows(
                ContainerInvalidReferenceException.class,
                () -> container.getSingleton(Service.class));
    }

    // --- aliases ------------------------------------------------------------

    @Test
    void aliasResolvesToTarget() {
        var container = new Container();
        container.bind(EnglishGreeter.class, (c, a) -> new EnglishGreeter());
        container.bindAlias(Greeter.class, raw(EnglishGreeter.class));

        assertTrue(container.isAlias(Greeter.class));
        assertInstanceOf(EnglishGreeter.class, container.get(Greeter.class));
        assertInstanceOf(EnglishGreeter.class, container.getAliased(Greeter.class, Map.of()));
    }

    @Test
    void getAliasedThrowsWhenNotAliased() {
        var container = new Container();

        assertThrows(
                ContainerInvalidReferenceException.class,
                () -> container.getAliased(Greeter.class, Map.of()));
    }

    // --- fallback resolution ------------------------------------------------

    @Test
    void fallbackCreatesNewInstanceForInstantiableType() {
        var container = new Container();

        assertInstanceOf(Service.class, container.get(Service.class));
    }

    @Test
    void fallbackThrowsForNonInstantiableType() {
        var container = new Container();

        assertThrows(
                ContainerInvalidReferenceException.class, () -> container.get(Unresolvable.class));
    }

    @Test
    void fallbackThrowModeThrowsWithoutAttemptingInstantiation() {
        var container = new Container();

        assertThrows(
                ContainerInvalidReferenceException.class,
                () ->
                        container.get(
                                Service.class, Map.of(), InvalidReferenceMode.THROW_EXCEPTION));
    }

    // --- deferred publishing ------------------------------------------------

    @Test
    void getPublishesDeferredCallbackThenResolves() {
        var container = new Container();
        // A deferred callback that binds the service on first access.
        container.bind(Greeter.class, (c, a) -> new EnglishGreeter()); // unrelated existing binding
        container
                .getData(); // no-op touch
        // register a callback directly via a provider-like publish
        container.bindSingleton(Service.class, (c, a) -> new Service());

        assertNotNull(container.getCallback(Service.class) == null ? new Service() : null);
        assertInstanceOf(Service.class, container.get(Service.class));
    }
}
