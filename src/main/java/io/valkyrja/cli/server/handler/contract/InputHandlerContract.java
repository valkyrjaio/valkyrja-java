/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.server.handler.contract;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;

public interface InputHandlerContract {

    OutputContract handle(InputContract input);

    void exit(InputContract input, OutputContract output);

    void run(InputContract input);
}
