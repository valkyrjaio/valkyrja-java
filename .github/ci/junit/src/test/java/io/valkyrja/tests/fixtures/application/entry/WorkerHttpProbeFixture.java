/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.application.entry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.Response;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Binds a known-response handler into a worker application so a smoke test can assert both halves
 * of the worker I/O.
 */
public final class WorkerHttpProbeFixture {

    /** The distinctive status the stubbed handler responds with (HTTP 418). */
    public static final int STATUS = StatusCode.I_AM_A_TEAPOT.getValue();

    /** The distinctive body the stubbed handler responds with. */
    public static final String BODY = "Hello from the Valkyrja worker!";

    private final AtomicReference<ServerRequestContract> captured = new AtomicReference<>();

    private WorkerHttpProbeFixture() {}

    /**
     * Bind the stubbed handlers into {@code app}'s container and return the probe.
     *
     * @param app the bootstrapped worker application
     * @return the probe, capturing the request the adapter marshaled
     */
    public static WorkerHttpProbeFixture bind(ApplicationContract app) {
        WorkerHttpProbeFixture probe = new WorkerHttpProbeFixture();

        ResponseContract response = Response.create(BODY, StatusCode.I_AM_A_TEAPOT, null);

        RequestHandlerContract handler = mock(RequestHandlerContract.class);
        when(handler.handle(any()))
                .thenAnswer(
                        invocation -> {
                            probe.captured.set(invocation.getArgument(0));
                            return response;
                        });

        SendingResponseHandlerContract sendingResponseHandler =
                mock(SendingResponseHandlerContract.class);
        when(sendingResponseHandler.sendingResponse(any(), any())).thenReturn(response);

        app.getContainer().setSingleton(RequestHandlerContract.class, handler);
        app.getContainer()
                .setSingleton(SendingResponseHandlerContract.class, sendingResponseHandler);

        return probe;
    }

    /**
     * The request the adapter marshaled from the native request and passed to the handler.
     *
     * @return the captured request, or {@code null} if no request has been dispatched yet
     */
    public ServerRequestContract capturedRequest() {
        return captured.get();
    }
}
