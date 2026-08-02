/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.data.option;

import io.valkyrja.cli.routing.constant.OptionName;
import io.valkyrja.cli.routing.constant.OptionShortName;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import java.util.ArrayList;
import java.util.List;

public class NoInteractionOptionParameter extends OptionParameter {

    public NoInteractionOptionParameter() {
        super(
                OptionName.NO_INTERACTION,
                "No interactive questions are asked.",
                "",
                "",
                List.of(OptionShortName.NO_INTERACTION),
                List.of(),
                new ArrayList<>(),
                OptionMode.OPTIONAL,
                OptionValueMode.NONE);
    }
}
