/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.throwable.exception;

import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;

public class ContainerInvalidReferenceException extends ContainerInvalidArgumentException {

    public ContainerInvalidReferenceException(String id) {
        super("Service with `" + id + "` not found");
    }

    public ContainerInvalidReferenceException(String id, Throwable cause) {
        super("Service with `" + id + "` not found", cause);
    }
}