/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.controller;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;

public abstract class Controller {

    protected InputContract input;
    protected OutputFactoryContract outputFactory;

    public Controller(InputContract input, OutputFactoryContract outputFactory) {
        this.input = input;
        this.outputFactory = outputFactory;
    }
}
