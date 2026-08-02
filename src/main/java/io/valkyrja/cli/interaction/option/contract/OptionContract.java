/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.option.contract;

import io.valkyrja.cli.interaction.enum_.OptionType;

public interface OptionContract {

    String getName();

    OptionContract withName(String name);

    OptionType getType();

    OptionContract withType(OptionType type);

    boolean hasValue();

    String getValue();

    OptionContract withValue(String value);

    OptionContract withoutValue();
}
