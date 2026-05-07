/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.data;

import io.valkyrja.cli.interaction.option.contract.OptionContract;
import io.valkyrja.cli.routing.data.abstract_.Parameter;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.ParameterContract;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidOptionWithValueException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingOptionValuesValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OptionParameter extends Parameter implements OptionParameterContract {

    protected String valueDisplayName;
    protected String defaultValue;
    protected List<String> shortNames;
    protected List<String> validValues;
    protected List<OptionContract> options;
    protected OptionMode mode;
    protected OptionValueMode valueMode;

    public OptionParameter(String name, String description) {
        this(name, description, "", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), OptionMode.OPTIONAL, OptionValueMode.DEFAULT);
    }

    public OptionParameter(
            String name,
            String description,
            String valueDisplayName,
            String defaultValue,
            List<String> shortNames,
            List<String> validValues,
            List<OptionContract> options,
            OptionMode mode,
            OptionValueMode valueMode) {
        super(name, description);
        this.valueDisplayName = valueDisplayName;
        this.defaultValue = defaultValue;
        this.shortNames = new ArrayList<>(shortNames);
        this.validValues = new ArrayList<>(validValues);
        this.options = new ArrayList<>(options);
        this.mode = mode;
        this.valueMode = valueMode;
    }

    @Override
    protected OptionParameter copy() {
        return new OptionParameter(name, description, valueDisplayName, defaultValue, shortNames, validValues, options, mode, valueMode);
    }

    @Override
    public List<String> getShortNames() {
        return shortNames;
    }

    @Override
    public OptionParameterContract withShortNames(String... shortNames) {
        OptionParameter copy = copy();
        copy.shortNames = new ArrayList<>(Arrays.asList(shortNames));
        return copy;
    }

    @Override
    public OptionParameterContract withAddedShortNames(String... shortNames) {
        OptionParameter copy = copy();
        copy.shortNames = new ArrayList<>(this.shortNames);
        for (String s : shortNames) {
            if (!copy.shortNames.contains(s)) {
                copy.shortNames.add(s);
            }
        }
        return copy;
    }

    @Override
    public OptionMode getMode() {
        return mode;
    }

    @Override
    public OptionParameterContract withMode(OptionMode mode) {
        OptionParameter copy = copy();
        copy.mode = mode;
        return copy;
    }

    @Override
    public OptionValueMode getValueMode() {
        return valueMode;
    }

    @Override
    public OptionParameterContract withValueMode(OptionValueMode valueMode) {
        OptionParameter copy = copy();
        copy.valueMode = valueMode;
        return copy;
    }

    @Override
    public boolean hasValueDisplayName() {
        return !valueDisplayName.isEmpty();
    }

    @Override
    public String getValueDisplayName() {
        return valueDisplayName;
    }

    @Override
    public OptionParameterContract withValueDisplayName(String valueName) {
        OptionParameter copy = copy();
        copy.valueDisplayName = valueName;
        return copy;
    }

    @Override
    public List<String> getValidValues() {
        return validValues;
    }

    @Override
    public OptionParameterContract withValidValues(String... validValues) {
        OptionParameter copy = copy();
        copy.validValues = new ArrayList<>(Arrays.asList(validValues));
        return copy;
    }

    @Override
    public OptionParameterContract withAddedValidValues(String... validValues) {
        OptionParameter copy = copy();
        copy.validValues = new ArrayList<>(this.validValues);
        for (String v : validValues) {
            if (!copy.validValues.contains(v)) {
                copy.validValues.add(v);
            }
        }
        return copy;
    }

    @Override
    public boolean hasDefaultValue() {
        return !defaultValue.isEmpty();
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public OptionParameterContract withDefaultValue(String defaultValue) {
        OptionParameter copy = copy();
        copy.defaultValue = defaultValue;
        return copy;
    }

    @Override
    public List<OptionContract> getOptions() {
        return options;
    }

    @Override
    public OptionParameterContract withOptions(OptionContract... options) {
        OptionParameter copy = copy();
        copy.options = new ArrayList<>();
        for (OptionContract option : options) {
            if (valueMode == OptionValueMode.NONE && option.hasValue()) {
                throw new CliRoutingInvalidOptionWithValueException(name + " should have no value");
            }
            copy.options.add(option);
        }
        return copy;
    }

    @Override
    public OptionParameterContract withAddedOptions(OptionContract... options) {
        OptionParameter copy = copy();
        copy.options = new ArrayList<>(this.options);
        for (OptionContract option : options) {
            if (valueMode == OptionValueMode.NONE && option.hasValue()) {
                throw new CliRoutingInvalidOptionWithValueException(name + " should have no value");
            }
            copy.options.add(option);
        }
        return copy;
    }

    @Override
    public List<String> getCastValues() {
        return options.stream().map(OptionContract::getValue).collect(Collectors.toList());
    }

    @Override
    public boolean hasFirstValue() {
        return !options.isEmpty();
    }

    @Override
    public String getFirstValue() {
        return options.isEmpty() ? "" : options.get(0).getValue();
    }

    @Override
    public boolean areValuesValid() {
        boolean valid = true;
        if (mode == OptionMode.REQUIRED) {
            valid = !options.isEmpty();
        }
        if (valueMode == OptionValueMode.DEFAULT) {
            valid = valid && options.size() <= 1;
        }
        return valid;
    }

    @Override
    public ParameterContract validateValues() {
        if (!areValuesValid()) {
            throw new CliRoutingOptionValuesValidationException(name + " is invalid");
        }
        return this;
    }
}