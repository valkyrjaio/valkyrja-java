/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.throwable.exception.abstract_;

import io.valkyrja.grpc.throwable.contract.GrpcThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class GrpcInvalidArgumentException extends InvalidArgumentException
        implements GrpcThrowable {

    protected GrpcInvalidArgumentException(String message) {
        super(message);
    }

    protected GrpcInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
