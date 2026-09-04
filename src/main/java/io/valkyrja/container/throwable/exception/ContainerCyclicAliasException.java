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
     * @param alias the alias being bound
     * @param id the type the alias points at
     */
    public ContainerCyclicAliasException(String alias, String id) {
        super(
                "Alias `"
                        + alias
                        + "` cannot point at `"
                        + id
                        + "`, because `"
                        + id
                        + "` already resolves to `"
                        + alias
                        + "`.");
    }
}
