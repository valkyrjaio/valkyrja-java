/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.data.abstract_;

import io.valkyrja.cli.routing.data.contract.ParameterContract;

public abstract class Parameter implements ParameterContract {

    protected String name;
    protected String description;

    public Parameter(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ParameterContract withName(String name) {
        Parameter copy = copy();
        copy.name = name;
        return copy;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ParameterContract withDescription(String description) {
        Parameter copy = copy();
        copy.description = description;
        return copy;
    }

    protected abstract Parameter copy();
}
