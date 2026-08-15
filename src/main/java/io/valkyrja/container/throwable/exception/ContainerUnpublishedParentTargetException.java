/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.throwable.exception;

import io.valkyrja.container.throwable.exception.abstract_.ContainerRuntimeException;

public class ContainerUnpublishedParentTargetException extends ContainerRuntimeException {

    public ContainerUnpublishedParentTargetException(String id) {
        super(
                "`"
                        + id
                        + "` is registered in the parent container and its publish callback has not"
                        + " run. Resolve or publish it in bootstrapParentServices(), or give the"
                        + " child the publish callbacks.");
    }
}
