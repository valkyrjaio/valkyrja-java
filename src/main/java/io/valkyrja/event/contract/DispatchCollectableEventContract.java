/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.contract;

/** Contract for events that collect the return values of their dispatched listeners. */
public interface DispatchCollectableEventContract {

    /**
     * Add a dispatch result from a listener invocation.
     *
     * @param result the return value of a listener
     */
    void addDispatch(Object result);
}
