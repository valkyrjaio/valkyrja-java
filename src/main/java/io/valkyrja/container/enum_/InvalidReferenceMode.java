/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.enum_;

/** Mode for handling invalid service references in the container. */
public enum InvalidReferenceMode {

    /** Attempt to create a new instance of the class, or throw an exception if not possible. */
    NEW_INSTANCE_OR_THROW_EXCEPTION,

    /** Always throw an exception when the service is not found. */
    THROW_EXCEPTION,
}
