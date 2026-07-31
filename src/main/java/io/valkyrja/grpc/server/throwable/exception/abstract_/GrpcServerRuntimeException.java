/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.server.throwable.exception.abstract_;

import io.valkyrja.grpc.server.throwable.contract.GrpcServerThrowable;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;

public abstract class GrpcServerRuntimeException extends GrpcRuntimeException
        implements GrpcServerThrowable {

    protected GrpcServerRuntimeException(String message) {
        super(message);
    }

    protected GrpcServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
