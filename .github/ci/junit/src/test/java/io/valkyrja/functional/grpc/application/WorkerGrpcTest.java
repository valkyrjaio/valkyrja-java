/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.grpc.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.abstract_.WorkerGrpc;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.fixtures.grpc.GreeterComponentProvider;
import io.valkyrja.fixtures.grpc.GreeterController;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/** Test the {@link WorkerGrpc} persistent-worker entry point. */
final class WorkerGrpcTest {

    private GrpcConfig config() {
        return new GrpcConfig(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                false,
                "UTC",
                "secret_app_key",
                "app/grpc/provider/data",
                "app.grpc.provider.data",
                50051,
                List.of(new GreeterComponentProvider()),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    @Test
    void bootstrapThenDispatchWritesResponseBeforeTerminate() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        AtomicReference<ServiceResponseContract> written = new AtomicReference<>();
        WorkerGrpc.dispatch(
                app, data, ServiceCall.unary("/pkg.Greeter/SayHello", "req"), written::set);

        ServiceResponseContract response = written.get();
        assertNotNull(response);
        assertTrue(response.getStatus().isOk());
        assertEquals("hello", response.getMessages().iterator().next());
    }

    @Test
    void dispatchRunsResponseSentEvenWhenTheWriterThrows() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        // A wire write that blows up must not skip the ResponseSent stage, or per-call resources
        // leak and observers never see the call complete.
        GreeterController.ResponseSentMiddleware.calls.set(0);
        assertThrows(
                IllegalStateException.class,
                () ->
                        WorkerGrpc.dispatch(
                                app,
                                data,
                                ServiceCall.unary("/pkg.Greeter/StreamHellos", "req"),
                                response -> {
                                    throw new IllegalStateException("wire write failed");
                                }));

        assertEquals(1, GreeterController.ResponseSentMiddleware.calls.get());
    }

    @Test
    void dispatchIsolatesEachCallInAChildContainer() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        AtomicReference<ServiceResponseContract> first = new AtomicReference<>();
        AtomicReference<ServiceResponseContract> second = new AtomicReference<>();
        WorkerGrpc.dispatch(app, data, ServiceCall.unary("/pkg.Greeter/SayHello", "a"), first::set);
        WorkerGrpc.dispatch(app, data, ServiceCall.unary("/pkg.Greeter/Missing", "b"), second::set);

        assertTrue(first.get().getStatus().isOk());
        assertEquals(StatusCode.UNIMPLEMENTED, second.get().getStatus().getCode());
    }

    @Test
    void isInstantiableViaSubclass() {
        assertNotNull(new WorkerGrpc() {});
    }

    @Test
    void lifecycleHelpersAreReusableStandalone() {
        ApplicationContract app = WorkerGrpc.bootstrap(config());
        ContainerData data = (ContainerData) app.getContainer().getData();

        var child = WorkerGrpc.getChildContainer(app, data);
        var childApp = WorkerGrpc.getChildApplication(app, child);
        WorkerGrpc.bootstrapChildContainer(childApp, child);

        assertNotNull(child.getSingleton(io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract.class));
    }
}
