/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.application.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.Config;
import io.valkyrja.application.kernel.Valkyrja;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.provider.ContainerComponentProvider;
import io.valkyrja.event.provider.EventComponentProvider;
import io.valkyrja.tests.fixtures.application.provider.CliComponentProviderFixture;
import io.valkyrja.tests.fixtures.application.provider.CliRouteComponentProviderFixture;
import io.valkyrja.tests.fixtures.application.provider.ComponentProviderFixture;
import io.valkyrja.tests.fixtures.application.provider.EventComponentProviderFixture;
import io.valkyrja.tests.fixtures.application.provider.GrpcRouteComponentProviderFixture;
import io.valkyrja.tests.fixtures.application.provider.HttpComponentProviderFixture;
import io.valkyrja.tests.fixtures.application.provider.HttpRouteComponentProviderFixture;
import io.valkyrja.tests.fixtures.cli.routing.provider.CliRouteProviderFixture;
import io.valkyrja.tests.fixtures.event.provider.ListenerProviderFixture;
import io.valkyrja.tests.fixtures.grpc.GreeterRouteProviderFixture;
import io.valkyrja.tests.fixtures.http.routing.provider.HttpRouteProviderFixture;
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
    void getProvidersExpandsComponentProviders() {
        var application = appWith(new ComponentProviderFixture());

        var providers = application.getProviders();
        assertEquals(3, providers.size());
        assertInstanceOf(CliComponentProviderFixture.class, providers.get(0));
        assertInstanceOf(HttpComponentProviderFixture.class, providers.get(1));
        assertInstanceOf(ComponentProviderFixture.class, providers.get(2));
    }

    @Test
    void getProvidersIsCached() {
        var application = appWith(new ComponentProviderFixture());

        var first = application.getProviders();

        assertEquals(3, first.size());
        assertSame(first, application.getProviders());
    }

    @Test
    void getContainerProviders() {
        var application = appWith(new ComponentProviderFixture());

        var result = application.getContainerProviders();

        assertEquals(4, result.size());
    }

    @Test
    void getContainerProvidersIsCached() {
        var application = appWith(new ComponentProviderFixture());

        var first = application.getContainerProviders();

        assertEquals(4, first.size());
        assertSame(first, application.getContainerProviders());
    }

    @Test
    void getEventProviders() {
        var application = appWith(new EventComponentProviderFixture());

        var result = application.getEventProviders();

        assertEquals(1, result.size());
        assertInstanceOf(ListenerProviderFixture.class, result.get(0));
    }

    @Test
    void getEventProvidersIsCached() {
        var application = appWith(new EventComponentProviderFixture());

        var first = application.getEventProviders();

        assertEquals(1, first.size());
        assertSame(first, application.getEventProviders());
    }

    @Test
    void getCliProviders() {
        var application = appWith(new CliRouteComponentProviderFixture());

        var result = application.getCliProviders();

        assertEquals(1, result.size());
        assertInstanceOf(CliRouteProviderFixture.class, result.get(0));
    }

    @Test
    void getCliProvidersIsCached() {
        var application = appWith(new CliRouteComponentProviderFixture());

        var first = application.getCliProviders();

        assertEquals(1, first.size());
        assertSame(first, application.getCliProviders());
    }

    @Test
    void getHttpProviders() {
        var application = appWith(new HttpRouteComponentProviderFixture());

        var result = application.getHttpProviders();

        assertEquals(1, result.size());
        assertInstanceOf(HttpRouteProviderFixture.class, result.get(0));
    }

    @Test
    void getHttpProvidersIsCached() {
        var application = appWith(new HttpRouteComponentProviderFixture());

        var first = application.getHttpProviders();

        assertEquals(1, first.size());
        assertSame(first, application.getHttpProviders());
    }

    @Test
    void getGrpcProviders() {
        var application = appWith(new GrpcRouteComponentProviderFixture());

        var result = application.getGrpcProviders();

        assertEquals(1, result.size());
        assertInstanceOf(GreeterRouteProviderFixture.class, result.get(0));
    }

    @Test
    void getGrpcProvidersIsCached() {
        var application = appWith(new GrpcRouteComponentProviderFixture());

        var first = application.getGrpcProviders();

        assertEquals(1, first.size());
        assertSame(first, application.getGrpcProviders());
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
