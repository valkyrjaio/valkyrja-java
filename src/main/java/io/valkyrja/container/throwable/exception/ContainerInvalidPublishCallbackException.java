/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.throwable.exception;

import io.valkyrja.container.throwable.exception.abstract_.ContainerRuntimeException;

public class ContainerInvalidPublishCallbackException extends ContainerRuntimeException {

    public ContainerInvalidPublishCallbackException(String message) {
        super(message);
    }

    public ContainerInvalidPublishCallbackException(String message, Throwable cause) {
        super(message, cause);
    }
}