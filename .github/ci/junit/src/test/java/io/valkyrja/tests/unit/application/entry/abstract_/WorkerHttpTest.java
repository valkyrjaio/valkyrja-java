/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.entry.abstract_.WorkerHttp;
import io.valkyrja.application.kernel.ChildApplication;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

/**
 * Test the {@link WorkerHttp} entry point.
 *
 * <p>Java static methods are not polymorphic, so the PHP {@code WorkerHttpClass} subclass-override
 * fixture does not translate. Instead the decomposed static lifecycle methods are exercised
 * directly, with the request handler and sending-response handler stubbed as contracts.
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
    void dispatchHandlesRunsSendingResponseEmitsThenTerminates() {
        var app = WorkerHttp.bootstrap(new HttpConfig());
        var handler = mock(RequestHandlerContract.class);
        var sendingResponseHandler = mock(SendingResponseHandlerContract.class);
        var request = mock(ServerRequestContract.class);
        var handled = mock(ResponseContract.class);
        var sent = mock(ResponseContract.class);
        when(handler.handle(request)).thenReturn(handled);
        when(sendingResponseHandler.sendingResponse(request, handled)).thenReturn(sent);
        app.getContainer().setSingleton(RequestHandlerContract.class, handler);
        app.getContainer()
                .setSingleton(SendingResponseHandlerContract.class, sendingResponseHandler);
        var data = (ContainerData) app.getContainer().getData();

        AtomicReference<ResponseContract> emitted = new AtomicReference<>();
        WorkerHttp.dispatch(app, data, request, emitted::set);

        // The response emitted through the runtime is the one returned by the SendingResponse
        // stage.
        assertSame(sent, emitted.get());
        // Order: handle, then SendingResponse, then emit (implicit via emitted), then ResponseSent.
        InOrder order = inOrder(handler, sendingResponseHandler);
        order.verify(handler).handle(request);
        order.verify(sendingResponseHandler).sendingResponse(request, handled);
        order.verify(handler).terminate(request, sent);
        // The parent application is never replaced by the request-scoped child.
        assertSame(app, app.getContainer().getSingleton(ApplicationContract.class));
    }

    @Test
    void dispatchRunsResponseSentEvenWhenTheEmitterThrows() {
        var app = WorkerHttp.bootstrap(new HttpConfig());
        var handler = mock(RequestHandlerContract.class);
        var sendingResponseHandler = mock(SendingResponseHandlerContract.class);
        var request = mock(ServerRequestContract.class);
        var handled = mock(ResponseContract.class);
        var sent = mock(ResponseContract.class);
        when(handler.handle(request)).thenReturn(handled);
        when(sendingResponseHandler.sendingResponse(request, handled)).thenReturn(sent);
        app.getContainer().setSingleton(RequestHandlerContract.class, handler);
        app.getContainer()
                .setSingleton(SendingResponseHandlerContract.class, sendingResponseHandler);
        var data = (ContainerData) app.getContainer().getData();

        // A native emit that blows up must not skip ResponseSent, or per-request resources leak and
        // observers never see the request complete.
        assertThrows(
                IllegalStateException.class,
                () ->
                        WorkerHttp.dispatch(
                                app,
                                data,
                                request,
                                response -> {
                                    throw new IllegalStateException("emit failed");
                                }));

        verify(handler).terminate(request, sent);
    }

    @Test
    void requestBuildsServerRequestWithMethodQueryAndBody() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", "example.com");

        var request =
                WorkerHttp.request(
                        "GET", "/path?a=1", "a=1", "HTTP/1.1", "1.2.3.4", headers, "hello");

        assertEquals(RequestMethod.GET, request.getMethod());
        assertEquals("/path", request.getUri().getPath());
        assertEquals("1", request.getQueryParams().get("a"));
        assertEquals("hello", request.getBody().getContents());
    }

    @Test
    void serverParamsFoldsHeadersAndOptionalParts() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Host", "example.com");
        headers.put("Content-Type", "text/plain");
        headers.put("Content-Length", "5");

        var server =
                WorkerHttp.serverParams("POST", "/p?x=1", "x=1", "HTTP/1.1", "1.2.3.4", headers);

        assertEquals("POST", server.get("REQUEST_METHOD"));
        assertEquals("/p?x=1", server.get("REQUEST_URI"));
        assertEquals("HTTP/1.1", server.get("SERVER_PROTOCOL"));
        assertEquals("x=1", server.get("QUERY_STRING"));
        assertEquals("1.2.3.4", server.get("REMOTE_ADDR"));
        assertEquals("example.com", server.get("HTTP_HOST"));
        assertEquals("text/plain", server.get("CONTENT_TYPE"));
        assertEquals("5", server.get("CONTENT_LENGTH"));
    }

    @Test
    void serverParamsOmitsBlankQueryString() {
        var server = WorkerHttp.serverParams("GET", "/", "", "HTTP/1.1", "1.2.3.4", Map.of());

        assertFalse(server.containsKey("QUERY_STRING"));
        assertTrue(server.containsKey("REMOTE_ADDR"));
    }

    @Test
    void serverParamsOmitsNullQueryStringAndRemoteAddr() {
        var server = WorkerHttp.serverParams("GET", "/", null, "HTTP/1.1", null, Map.of());

        assertFalse(server.containsKey("QUERY_STRING"));
        assertFalse(server.containsKey("REMOTE_ADDR"));
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
