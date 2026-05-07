/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.middleware.throwable.exception.abstract_;

import io.valkyrja.cli.middleware.throwable.contract.CliMiddlewareThrowable;
import io.valkyrja.cli.throwable.exception.abstract_.CliInvalidArgumentException;

public abstract class CliMiddlewareInvalidArgumentException extends CliInvalidArgumentException
        implements CliMiddlewareThrowable {

    protected CliMiddlewareInvalidArgumentException(String message) {
        super(message);
    }

    protected CliMiddlewareInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
