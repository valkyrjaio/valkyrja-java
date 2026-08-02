/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.argument;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;

public class Argument implements ArgumentContract {

    protected String value;

    public Argument(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ArgumentContract withValue(String value) {
        Argument copy = new Argument(this.value);
        copy.value = value;
        return copy;
    }
}
