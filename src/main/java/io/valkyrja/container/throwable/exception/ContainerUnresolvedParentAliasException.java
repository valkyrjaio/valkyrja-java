/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.throwable.exception;

import io.valkyrja.container.throwable.exception.abstract_.ContainerRuntimeException;

public class ContainerUnresolvedParentAliasException extends ContainerRuntimeException {

    public ContainerUnresolvedParentAliasException(String alias, String reachedId) {
        super(
                "Alias `"
                        + alias
                        + "` reaches `"
                        + reachedId
                        + "`, which the parent container has not resolved. Resolve or publish it in"
                        + " bootstrapParentServices() before the request loop begins.");
    }
}
