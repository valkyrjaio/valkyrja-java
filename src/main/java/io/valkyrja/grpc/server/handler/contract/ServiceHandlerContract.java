/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.server.handler.contract;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;

/**
 * The gRPC kernel entry point, analogous to HTTP's {@code RequestHandler} and CLI's {@code
 * InputHandler}.
 *
 * <p>Orchestrates the pipeline — {@code CallReceived} → {@code Router} → {@code ThrowableCaught}
 * (via {@link #handle}), then {@code SendingResponse} (via {@link #sending}) and {@code Terminated}
 * (via {@link #terminate}). The wire write itself is the adapter's job and happens between {@link
 * #sending} and {@link #terminate}; {@link #run} bundles handle+sending so the adapter can write
 * the returned response and then call {@link #terminate}.
 */
public interface ServiceHandlerContract {

    /**
     * Run {@code CallReceived} → {@code Router}, converting any thrown throwable via {@code
     * ThrowableCaught}. Includes the entry-point cancellation pre-check.
     *
     * @param call the inbound call
     * @return the response
     */
    ServiceResponseContract handle(ServiceCallContract call);

    /**
     * Run the {@code SendingResponse} stage over a response. Always runs, including on error and
     * cancellation paths.
     *
     * @param call the inbound call
     * @param response the response produced by {@link #handle}
     * @return the response to write to the wire
     */
    ServiceResponseContract sending(ServiceCallContract call, ServiceResponseContract response);

    /**
     * Run the {@code Terminated} stage after the response has been written to the wire.
     *
     * @param call the inbound call
     * @param response the response that was written
     */
    void terminate(ServiceCallContract call, ServiceResponseContract response);

    /**
     * Convenience: {@link #handle} then {@link #sending}. The adapter writes the returned response
     * to the wire, then calls {@link #terminate}.
     *
     * @param call the inbound call
     * @return the response to write to the wire
     */
    ServiceResponseContract run(ServiceCallContract call);
}
