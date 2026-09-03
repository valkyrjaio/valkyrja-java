/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.throwable.exception;

import io.valkyrja.container.throwable.exception.abstract_.ContainerInvalidArgumentException;

/** Thrown when a chain of parent aliases returns to a type it already reached. */
public class ContainerCyclicAliasException extends ContainerInvalidArgumentException {

    /**
     * Construct a new exception.
     *
     * @param alias the alias the walk started from
     * @param from the type that closes the cycle
     * @param to the type that the cycle returns to
     */
    public ContainerCyclicAliasException(String alias, String from, String to) {
        super(
                "Alias `"
                        + alias
                        + "` follows a cyclic chain. `"
                        + from
                        + "` points back to `"
                        + to
                        + "`.");
    }
}
