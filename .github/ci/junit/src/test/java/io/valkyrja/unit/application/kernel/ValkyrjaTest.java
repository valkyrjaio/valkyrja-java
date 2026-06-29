/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.Config;
import io.valkyrja.application.kernel.Valkyrja;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.fixtures.application.provider.CliComponentProviderClass;
import io.valkyrja.fixtures.application.provider.CliRouteComponentProviderClass;
import io.valkyrja.fixtures.application.provider.ComponentProviderClass;
import io.valkyrja.fixtures.application.provider.EventComponentProviderClass;
import io.valkyrja.fixtures.application.provider.HttpComponentProviderClass;
import io.valkyrja.fixtures.application.provider.HttpRouteComponentProviderClass;
import io.valkyrja.fixtures.cli.routing.provider.CliRouteProviderClass;
import io.valkyrja.fixtures.event.provider.ListenerProviderClass;
import io.valkyrja.fixtures.http.routing.provider.HttpRouteProviderClass;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.provider.ContainerComponentProvider;
import io.valkyrja.event.provider.EventComponentProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/** Test the {@link Valkyrja} application kernel. */
final class ValkyrjaTest {

    /** Build a config that overrides only providers and callbacks, keeping all other defaults. */
    private static Config configWith(
            List<ComponentProviderContract> providers,
            List<Consumer<ApplicationContract>> callbacks) {
        return new Config(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                false,
                "UTC",
                "secret_app_key",
                "app/provider/data",
                "app.provider.data",
                providers,
                callbacks);
    }

    /** Build an application whose config holds the given providers and no callbacks. */
    private static Valkyrja appWith(ComponentProviderContract... providers) {
        return new Valkyrja(new Container(), configWith(List.of(providers), List.of()));
    }

    @Test
    void defaults() {
        var config = new Config();
        var container = new Container();

        var application = new Valkyrja(container, config);

        assertSame(container, application.getContainer());
        assertEquals(config.environment(), application.getEnvironment());
        assertEquals(config.debugMode(), application.getDebugMode());
        assertEquals(config.version(), application.getVersion());
        assertEquals(config.timezone(), TimeZone.getDefault().getID());

        var providers = application.getProviders();
        assertEquals(3, providers.size());
        assertInstanceOf(ContainerComponentProvider.class, providers.get(0));
        assertInstanceOf(EventComponentProvider.class, providers.get(1));
        assertInstanceOf(ApplicationComponentProvider.class, providers.get(2));
    }

    @Test
    void singleArgConstructorUsesDefaultConfig() {
        var container = new Container();

        var application = new Valkyrja(container);

        assertSame(container, application.getContainer());
        assertEquals("production", application.getEnvironment());
        assertEquals("1.0.0", application.getVersion());
        assertEquals(3, application.getProviders().size());
    }

    @Test
    void getProvidersExpandsComponentProviders() {
        var application = appWith(new ComponentProviderClass());

        var providers = application.getProviders();
        assertEquals(3, providers.size());
        assertInstanceOf(CliComponentProviderClass.class, providers.get(0));
        assertInstanceOf(HttpComponentProviderClass.class, providers.get(1));
        assertInstanceOf(ComponentProviderClass.class, providers.get(2));
    }

    @Test
    void getProvidersIsCached() {
        var application = appWith(new ComponentProviderClass());

        var first = application.getProviders();

        assertEquals(3, first.size());
        assertSame(first, application.getProviders());
    }

    @Test
    void getContainerProviders() {
        var application = appWith(new ComponentProviderClass());

        var result = application.getContainerProviders();

        assertEquals(4, result.size());
    }

    @Test
    void getContainerProvidersIsCached() {
        var application = appWith(new ComponentProviderClass());

        var first = application.getContainerProviders();

        assertEquals(4, first.size());
        assertSame(first, application.getContainerProviders());
    }

    @Test
    void getEventProviders() {
        var application = appWith(new EventComponentProviderClass());

        var result = application.getEventProviders();

        assertEquals(1, result.size());
        assertInstanceOf(ListenerProviderClass.class, result.get(0));
    }

    @Test
    void getEventProvidersIsCached() {
        var application = appWith(new EventComponentProviderClass());

        var first = application.getEventProviders();

        assertEquals(1, first.size());
        assertSame(first, application.getEventProviders());
    }

    @Test
    void getCliProviders() {
        var application = appWith(new CliRouteComponentProviderClass());

        var result = application.getCliProviders();

        assertEquals(1, result.size());
        assertInstanceOf(CliRouteProviderClass.class, result.get(0));
    }

    @Test
    void getCliProvidersIsCached() {
        var application = appWith(new CliRouteComponentProviderClass());

        var first = application.getCliProviders();

        assertEquals(1, first.size());
        assertSame(first, application.getCliProviders());
    }

    @Test
    void getHttpProviders() {
        var application = appWith(new HttpRouteComponentProviderClass());

        var result = application.getHttpProviders();

        assertEquals(1, result.size());
        assertInstanceOf(HttpRouteProviderClass.class, result.get(0));
    }

    @Test
    void getHttpProvidersIsCached() {
        var application = appWith(new HttpRouteComponentProviderClass());

        var first = application.getHttpProviders();

        assertEquals(1, first.size());
        assertSame(first, application.getHttpProviders());
    }

    @Test
    void publishProviderCallbacks() {
        var received = new ArrayList<ApplicationContract>();

        List<Consumer<ApplicationContract>> callbacks = List.of(received::add, received::add);
        var application = new Valkyrja(new Container(), configWith(List.of(), callbacks));

        application.publishProviderCallbacks();

        assertEquals(2, received.size());
        assertSame(application, received.get(0));
        assertSame(application, received.get(1));
    }

    @Test
    void providerGettersWithNoProviders() {
        var application = new Valkyrja(new Container(), configWith(List.of(), List.of()));

        assertTrue(application.getProviders().isEmpty());
        assertTrue(application.getContainerProviders().isEmpty());
        assertTrue(application.getEventProviders().isEmpty());
        assertTrue(application.getCliProviders().isEmpty());
        assertTrue(application.getHttpProviders().isEmpty());
    }

    @Test
    void publishProviderCallbacksWithNoCallbacks() {
        var application = new Valkyrja(new Container(), configWith(List.of(), List.of()));

        // No exception should be thrown.
        application.publishProviderCallbacks();

        assertTrue(application.getProviders().isEmpty());
    }
}
