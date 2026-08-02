/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.throwable.contract;

/**
 * Contract for all Valkyrja throwables.
 *
 * <p>Extends the standard Java {@link Throwable} interface to add framework-specific functionality
 * such as a stable trace code that uniquely identifies an exception type.
 */
public interface ThrowableContract {

    /**
     * Get a stable trace code unique to this throwable's type and stack trace.
     *
     * @return an MD5-based trace code string
     */
    String getTraceCode();
}
