/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.stream;

import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;

/**
 * The transport-side primitive a streaming-model (bidirectional) call writes to. Supplied by the
 * worker adapter and driven by the framework's streaming dispatch: headers are committed once at
 * stream open, messages are pushed as the handler emits them, and the terminal status and trailing
 * metadata are written at close.
 *
 * <p>All three methods are invoked from the single handler thread, so implementations need not be
 * thread-safe against each other.
 */
public interface OutboundStream {

    /**
     * Commit the initial response headers. Called exactly once, at stream open (the first emit, or
     * the close if the handler emitted nothing).
     *
     * @param initialMetadata the initial metadata to send as headers
     */
    void sendHeaders(MetadataContract initialMetadata);

    /**
     * Push one outbound message to the wire.
     *
     * @param message the decoded message
     */
    void sendMessage(Object message);

    /**
     * Close the call with the terminal response's status and trailing metadata.
     *
     * @param terminal the terminal response (its message list is unused — messages went through
     *     {@link #sendMessage})
     */
    void close(ServiceResponseContract terminal);
}
