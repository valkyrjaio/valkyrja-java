/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.contract;

/**
 * Contract for events that can halt listener propagation.
 *
 * <p>Equivalent to PSR-14's {@code StoppableEventInterface}.
 */
public interface StoppableEventContract {

    /**
     * Whether the event's propagation should stop.
     *
     * @return true if no further listeners should be called
     */
    boolean isPropagationStopped();
}
