/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.throwable.exception;

import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;

public class ContainerCyclicAliasException extends ContainerInvalidArgumentException {

    /**
     * Construct a new exception.
     *
     * @param alias the type the chain leaves from
     * @param id the type it points at, from which the chain returns
     */
    public ContainerCyclicAliasException(String alias, String id) {
        super(
                "Alias `"
                        + alias
                        + "` cannot reach `"
                        + id
                        + "`, because the chain from `"
                        + id
                        + "` returns to `"
                        + alias
                        + "`.");
    }
}
