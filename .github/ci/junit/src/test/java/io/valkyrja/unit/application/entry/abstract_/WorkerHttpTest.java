/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.entry.abstract_;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.kernel.ChildApplication;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link WorkerHttp} entry point.
 *
 * <p>Java static methods are not polymorphic, so the PHP {@code WorkerHttpClass} subclass-override
 * fixture does not translate. Instead the decomposed static lifecycle methods are exercised
 * directly, and request dispatch is verified with a mocked {@link RequestHandlerContract}.
 */
final class WorkerHttpTest {

    @Test
    void bootstrapReturnsApplicationWithItselfAsSingleton() {
        var app = WorkerHttp.bootstrap(new HttpConfig());

        assertNotNull(app);
        assertSame(app, app.getContainer().getSingleton(ApplicationContract.class));
    }

    @Test
    void getChildContainerReturnsIsolatedChildContainer() {
        var app = WorkerHttp.bootstrap(new HttpConfig());
        var data = (ContainerData) app.getContainer().getData();

        var child = WorkerHttp.getChildContainer(app, data);

        assertInstanceOf(ChildContainer.class, child);
        assertNotSame(app.getContainer(), child);
    }

    @Test
    void getChildApplicationWrapsChildContainer() {
        var app = WorkerHttp.bootstrap(new HttpConfig());
        var data = (ContainerData) app.getContainer().getData();
        var child = WorkerHttp.getChildContainer(app, data);

        var childApp = WorkerHttp.getChildApplication(app, child);

        assertInstanceOf(ChildApplication.class, childApp);
        assertSame(child, childApp.getContainer());
        assertNotSame(app, childApp);
    }

    @Test
    void bootstrapChildContainerBindsRequestScopedSingletons() {
        var app = WorkerHttp.bootstrap(new HttpConfig());
        var data = (ContainerData) app.getContainer().getData();
        var child = WorkerHttp.getChildContainer(app, data);
        var childApp = WorkerHttp.getChildApplication(app, child);

        WorkerHttp.bootstrapChildContainer(childApp, child);

        assertSame(childApp, child.getSingleton(ApplicationContract.class));
        assertSame(child, child.getSingleton(ContainerContract.class));
    }

    @Test
    void handleRequestDispatchesToRequestHandler() {
        var app = WorkerHttp.bootstrap(new HttpConfig());
        var container = app.getContainer();
        var handler = mock(RequestHandlerContract.class);
        container.setSingleton(RequestHandlerContract.class, handler);
        var request = mock(ServerRequestContract.class);

        WorkerHttp.handleRequest(container, request);

        verify(handler).run(request);
    }

    @Test
    void handleCreatesIsolatedChildAndDispatchesRequest() {
        var app = WorkerHttp.bootstrap(new HttpConfig());
        var handler = mock(RequestHandlerContract.class);
        app.getContainer().setSingleton(RequestHandlerContract.class, handler);
        var data = (ContainerData) app.getContainer().getData();
        var request = mock(ServerRequestContract.class);

        WorkerHttp.handle(app, data, request);

        verify(handler).run(request);
        // Parent application is never replaced by the request-scoped child.
        assertSame(app, app.getContainer().getSingleton(ApplicationContract.class));
    }

    @Test
    void getRequestReturnsServerRequest() {
        assertNotNull(WorkerHttp.getRequest());
    }

    @Test
    void bootstrapParentServicesIsNoOp() {
        var app = WorkerHttp.bootstrap(new HttpConfig());

        // Base implementation is a no-op — invoked for coverage, must not throw.
        WorkerHttp.bootstrapParentServices(app);
    }

    @Test
    void abstractClassIsInstantiableBySubclass() {
        var instance = new WorkerHttp() {};

        assertInstanceOf(WorkerHttp.class, instance);
    }
}