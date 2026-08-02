/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.contract;

import java.util.Map;

/** Contract for events that accept runtime arguments after construction. */
public interface ArgumentsCapableEventContract {

    /**
     * Set the event arguments.
     *
     * @param arguments the arguments
     * @return this event (for fluent use)
     */
    ArgumentsCapableEventContract setArguments(Map<String, Object> arguments);
}
