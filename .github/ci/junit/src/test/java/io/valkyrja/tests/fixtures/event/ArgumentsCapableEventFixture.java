/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.event;

import io.valkyrja.event.contract.ArgumentsCapableEventContract;
import java.util.Map;

/** Event that captures the arguments passed when resolved from its binding key. */
public final class ArgumentsCapableEventFixture implements ArgumentsCapableEventContract {

    private Map<String, Object> arguments = Map.of();

    @Override
    public ArgumentsCapableEventContract setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
        return this;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }
}
