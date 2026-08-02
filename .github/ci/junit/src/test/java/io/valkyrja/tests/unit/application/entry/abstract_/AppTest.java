/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.CliConfig;
import io.valkyrja.application.data.Config;
import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.data.contract.CliConfigContract;
import io.valkyrja.application.data.contract.ConfigContract;
import io.valkyrja.application.data.contract.HttpConfigContract;
import io.valkyrja.application.directory.Directory;
import io.valkyrja.application.entry.abstract_.App;
import io.valkyrja.application.kernel.Valkyrja;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.support.time.Microtime;
import java.util.List;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link App} application entry point. */
final class AppTest {

    private String originalBasePath;

    /** Build a debug-mode config, overriding only the debug flag. */
    private static Config debugConfig(boolean debugMode) {
        return new Config(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                debugMode,
                "UTC",
                "secret_app_key",
                "app/provider/data",
                "app.provider.data",
                List.of(),
                List.of());
    }

    @BeforeEach
    void captureGlobals() {
        originalBasePath = Directory.basePath(null);
    }

    @AfterEach
    void restoreGlobals() {
        Directory.setBasePath(originalBasePath);
        Microtime.unfreeze();
    }

    @Test
    void appStartSetsAppStartTime() {
        Microtime.freeze();

        App.appStart();

        assertEquals(Microtime.get(), App.getAppStart());
    }

    @Test
    void directorySetsBasePath() {
        App.directory("/app/test/path");

        assertEquals("/app/test/path", Directory.basePath(null));
    }

    @Test
    void getContainerReturnsDistinctContainers() {
        var first = App.getContainer();
        var second = App.getContainer();

        assertInstanceOf(Container.class, first);
        assertNotSame(first, second);
    }

    @Test
    void getApplicationReturnsValkyrjaBackedByContainer() {
        var container = new Container();

        var app = App.getApplication(container, new Config());

        assertInstanceOf(Valkyrja.class, app);
        assertSame(container, app.getContainer());
    }

    @Test
    void appBindsCoreSingletonsAndContainerData() {
        var config = new Config();

        var app = App.app(config);
        var container = app.getContainer();

        assertSame(app, container.getSingleton(ApplicationContract.class));
        assertSame(config, container.getSingleton(ConfigContract.class));
        assertEquals(config.timezone(), TimeZone.getDefault().getID());
        assertTrue(container.isSingleton(ContainerDataContract.class));
    }

    @Test
    void bootstrapServicesBindsCliConfig() {
        var container = new Container();
        var config = new CliConfig();
        var app = App.getApplication(container, config);

        App.bootstrapServices(app, container, config);

        assertTrue(container.isSingleton(CliConfigContract.class));
        assertSame(config, container.getSingleton(CliConfigContract.class));
    }

    @Test
    void bootstrapServicesBindsHttpConfig() {
        var container = new Container();
        var config = new HttpConfig();
        var app = App.getApplication(container, config);

        App.bootstrapServices(app, container, config);

        assertTrue(container.isSingleton(HttpConfigContract.class));
        assertSame(config, container.getSingleton(HttpConfigContract.class));
    }

    @Test
    void startWithDebugModeEnabled() {
        var app = App.start(debugConfig(true));

        assertTrue(app.getDebugMode());
        assertSame(app, app.getContainer().getSingleton(ApplicationContract.class));
    }

    @Test
    void startWithDebugModeDisabled() {
        var app = App.start(debugConfig(false));

        assertTrue(App.getAppStart() > 0);
        assertSame(app, app.getContainer().getSingleton(ApplicationContract.class));
    }

    @Test
    void loadContainerDataSkipsPublishWhenAlreadyPresent() {
        var app = App.app(new Config());
        var container = app.getContainer();

        // ContainerData is already bound from the first app() call; this exercises the
        // branch that skips republishing and re-imports the existing snapshot.
        App.loadContainerData(container);

        assertTrue(container.isSingleton(ContainerDataContract.class));
    }

    @Test
    void bootstrapThrowableHandlerAndDefaultExceptionHandlerAreNoOps() {
        var container = new Container();
        var app = App.getApplication(container, new Config());

        // No-op hooks — invoked for coverage, must not throw.
        App.bootstrapThrowableHandler(app, container);
        App.defaultExceptionHandler();
    }

    @Test
    void abstractClassIsInstantiableBySubclass() {
        var instance = new App() {};

        assertInstanceOf(App.class, instance);
    }
}
