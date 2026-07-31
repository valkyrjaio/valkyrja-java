/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.throwable.exception;

import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;

/**
 * Thrown when a streaming handler pushes a message while another push is still in flight. Sends are
 * serialized and the transport is not safe against interleaving, so an overlapping push fails fast
 * rather than silently corrupting the wire framing.
 */
public class GrpcConcurrentSendException extends GrpcRuntimeException {

    public GrpcConcurrentSendException(String message) {
        super(message);
    }
}
