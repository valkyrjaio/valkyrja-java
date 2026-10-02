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
     * @param alias the type the chain returns to
     * @param id the type the chain runs from. The message names one type when the two are equal.
     */
    public ContainerCyclicAliasException(String alias, String id) {
        super(message(alias, id));
    }

    private static String message(String alias, String id) {
        if (alias.equals(id)) {
            return "Alias `" + alias + "` cannot point at itself.";
        }

        return "Alias `"
                + alias
                + "` cannot reach `"
                + id
                + "`, because the chain from `"
                + id
                + "` returns to `"
                + alias
                + "`.";
    }
}
