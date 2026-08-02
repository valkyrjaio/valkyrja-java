/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.dispatch.data.contract;

import java.util.Map;

/**
 * Contract for all dispatch data objects.
 *
 * <p>A dispatch encapsulates what to invoke (a method, callable, class, property, etc.) along with
 * any arguments and dependencies needed at invocation time.
 */
public interface DispatchContract {

    /**
     * Serialize this dispatch to a map (equivalent to PHP's {@code jsonSerialize()}).
     *
     * @return a map representation of this dispatch
     */
    Map<String, Object> toMap();

    /**
     * Get a string representation of this dispatch.
     *
     * @return string form
     */
    String toString();
}
