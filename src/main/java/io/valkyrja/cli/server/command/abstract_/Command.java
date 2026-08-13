/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.server.command.abstract_;

import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import org.jspecify.annotations.Nullable;

public abstract class Command {

    protected RouteContract route;

    protected Command(RouteContract route) {
        this.route = route;
    }

    /** Determines if the input spelled an option that the route declares. */
    protected boolean hasSpelledOption(String name) {
        return spelledOption(name) != null;
    }

    /** Returns the declared option where the input spelled it, and null where it did not. */
    protected @Nullable OptionParameterContract spelledOption(String name) {
        if (!route.hasOption(name)) {
            return null;
        }

        OptionParameterContract option = route.getOption(name);

        return option.hasFirstValue() ? option : null;
    }

    /** Returns the declared argument where the input spelled it, and null where it did not. */
    protected @Nullable ArgumentParameterContract spelledArgument(String name) {
        if (!route.hasArgument(name)) {
            return null;
        }

        ArgumentParameterContract argument = route.getArgument(name);

        return argument.hasFirstValue() ? argument : null;
    }
}
