/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.data;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;
import io.valkyrja.cli.routing.data.abstract_.Parameter;
import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingArgumentValuesValidationException;
import io.valkyrja.type.data.Cast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArgumentParameter extends Parameter<ArgumentParameter>
        implements ArgumentParameterContract {

    protected ArgumentMode mode;
    protected ArgumentValueMode valueMode;
    protected List<ArgumentContract> arguments;

    public ArgumentParameter(String name, String description) {
        this(
                name,
                description,
                ArgumentMode.OPTIONAL,
                ArgumentValueMode.DEFAULT,
                new ArrayList<>());
    }

    public ArgumentParameter(
            String name,
            String description,
            ArgumentMode mode,
            ArgumentValueMode valueMode,
            List<ArgumentContract> arguments) {
        super(name, description);
        this.mode = mode;
        this.valueMode = valueMode;
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    protected ArgumentParameter createCopy() {
        return new ArgumentParameter(name, description, mode, valueMode, arguments);
    }

    @Override
    public ArgumentMode getMode() {
        return mode;
    }

    @Override
    public ArgumentParameterContract withMode(ArgumentMode mode) {
        ArgumentParameter copy = copy();
        copy.mode = mode;
        return copy;
    }

    @Override
    public ArgumentValueMode getValueMode() {
        return valueMode;
    }

    @Override
    public ArgumentParameterContract withValueMode(ArgumentValueMode valueMode) {
        ArgumentParameter copy = copy();
        copy.valueMode = valueMode;
        return copy;
    }

    @Override
    public List<ArgumentContract> getArguments() {
        return arguments;
    }

    @Override
    public ArgumentParameterContract withArguments(ArgumentContract... arguments) {
        ArgumentParameter copy = copy();
        copy.arguments = new ArrayList<>(Arrays.asList(arguments));
        return copy;
    }

    @Override
    public ArgumentParameterContract withAddedArguments(ArgumentContract... arguments) {
        ArgumentParameter copy = copy();
        copy.arguments = new ArrayList<>(this.arguments);
        copy.arguments.addAll(Arrays.asList(arguments));
        return copy;
    }

    @Override
    public ArgumentParameterContract withCast(Cast cast) {
        ArgumentParameter copy = copy();
        copy.cast = cast;
        return copy;
    }

    @Override
    public ArgumentParameterContract withoutCast() {
        ArgumentParameter copy = copy();
        copy.cast = null;
        return copy;
    }

    @Override
    public List<String> getValues() {
        return arguments.stream().map(ArgumentContract::getValue).toList();
    }

    @Override
    public boolean isProvided() {
        return !arguments.isEmpty();
    }

    @Override
    public boolean hasFirstValue() {
        return !getFirstValue().isEmpty();
    }

    @Override
    public String getFirstValue() {
        return arguments.isEmpty() ? "" : arguments.get(0).getValue();
    }

    @Override
    public boolean areValuesValid() {
        boolean valid = true;
        if (mode == ArgumentMode.REQUIRED) {
            valid = !arguments.isEmpty();
        }
        if (valueMode == ArgumentValueMode.DEFAULT) {
            valid = valid && arguments.size() <= 1;
        }
        return valid;
    }

    @Override
    public ArgumentParameterContract validateValues() {
        if (!areValuesValid()) {
            throw new CliRoutingArgumentValuesValidationException(name + " is invalid");
        }
        return this;
    }
}
