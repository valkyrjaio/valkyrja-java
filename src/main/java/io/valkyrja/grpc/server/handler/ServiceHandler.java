/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.server.handler;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.message.status.Status;
import io.valkyrja.grpc.middleware.data.CallReceivedResult;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.grpc.routing.dispatcher.contract.RouterContract;
import io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract;
import io.valkyrja.grpc.support.Cancellation;
import io.valkyrja.grpc.throwable.exception.CancelledException;

/**
 * The gRPC kernel entry point.
 *
 * <p>Modeled on HTTP's {@code RequestHandler}: {@link #handle} dispatches the router inside a
 * top-level try/catch that maps a thrown throwable to a response and runs it through {@code
 * ThrowableCaught}. The one gRPC-specific addition is the entry-point cancellation pre-check in
 * {@link #dispatchRouter} — the only location where no response yet exists.
 *
 * <p>The {@code SendingResponse} and {@code Terminated} handlers are shared with the {@code Router}
 * (both resolve the same container singletons) so per-route middleware the router registers onto
 * those stages actually fires here.
 */
public class ServiceHandler implements ServiceHandlerContract {

    protected final ContainerContract container;
    protected final RouterContract router;
    protected final CallReceivedHandlerContract callReceivedHandler;
    protected final ThrowableCaughtHandlerContract throwableCaughtHandler;
    protected final SendingResponseHandlerContract sendingResponseHandler;
    protected final TerminatedHandlerContract terminatedHandler;
    protected final boolean debug;

    public ServiceHandler(
            ContainerContract container,
            RouterContract router,
            CallReceivedHandlerContract callReceivedHandler,
            ThrowableCaughtHandlerContract throwableCaughtHandler,
            SendingResponseHandlerContract sendingResponseHandler,
            TerminatedHandlerContract terminatedHandler,
            boolean debug) {
        this.container = container;
        this.router = router;
        this.callReceivedHandler = callReceivedHandler;
        this.throwableCaughtHandler = throwableCaughtHandler;
        this.sendingResponseHandler = sendingResponseHandler;
        this.terminatedHandler = terminatedHandler;
        this.debug = debug;
    }

    @Override
    public ServiceResponseContract handle(ServiceCallContract call) {
        ServiceResponseContract response;

        try {
            response = dispatchRouter(call);
        } catch (Throwable throwable) {
            response = getResponseFromThrowable(throwable);
            response = throwableCaughtHandler.throwableCaught(call, response, throwable);
        }

        container.setSingleton(ServiceResponseContract.class, response);

        return response;
    }

    @Override
    public ServiceResponseContract sending(
            ServiceCallContract call, ServiceResponseContract response) {
        ServiceResponseContract sent = sendingResponseHandler.sendingResponse(call, response);
        container.setSingleton(ServiceResponseContract.class, sent);
        return sent;
    }

    @Override
    public void terminate(ServiceCallContract call, ServiceResponseContract response) {
        terminatedHandler.terminated(call, response);
    }

    @Override
    public ServiceResponseContract run(ServiceCallContract call) {
        return sending(call, handle(call));
    }

    protected ServiceResponseContract dispatchRouter(ServiceCallContract call) {
        container.setSingleton(ServiceCallContract.class, call);

        ServiceResponseContract cancelled = Cancellation.checkAndFinalize(call, null);
        if (cancelled != null) {
            return cancelled;
        }

        CallReceivedResult received = callReceivedHandler.callReceived(call);
        if (received.response() != null) {
            return received.response();
        }

        ServiceCallContract processedCall = received.call();
        container.setSingleton(ServiceCallContract.class, processedCall);

        return router.dispatch(processedCall);
    }

    protected ServiceResponseContract getResponseFromThrowable(Throwable throwable) {
        if (debug) {
            throw new RuntimeException(throwable);
        }

        if (throwable instanceof CancelledException cancelled) {
            return ServiceResponse.cancelled(cancelled.getReason());
        }

        return ServiceResponse.of(Status.internal(null));
    }
}
