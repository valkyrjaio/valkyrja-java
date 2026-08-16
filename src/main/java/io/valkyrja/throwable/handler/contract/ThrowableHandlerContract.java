/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.throwable.handler.contract;

public interface ThrowableHandlerContract {

    /**
     * Get a trace code for a throwable.
     *
     * @param throwable the throwable
     * @return a stable trace code string
     */
    static String getTraceCode(Throwable throwable) {
        throw new UnsupportedOperationException("Implement in subclass");
    }
}
