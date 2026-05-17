/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.option.factory;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidEmptyValueException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidNonEmptyValueException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidOptionNameException;
import java.util.ArrayList;
import java.util.List;

public abstract class OptionFactory {

    public static List<Option> fromArg(String arg) {
        validateArgIsOption(arg);

        OptionType type = getOptionType(arg);

        String[] parts = arg.split("=", 2);
        String name = parts[0].replaceAll("^-+", "").trim();
        String value = parts.length > 1 ? parts[1] : "";

        validateNonEmptyName(name);

        if (type == OptionType.SHORT && name.length() > 1) {
            validateValueIsEmpty(value);
            return splitCombinedShortOptions(type, name);
        }

        List<Option> options = new ArrayList<>();
        options.add(new Option(name, value, type));
        return options;
    }

    protected static void validateArgIsOption(String arg) {
        if (!arg.startsWith("-")) {
            throw new CliInteractionInvalidOptionNameException(
                    "Options must begin with either a `-` or `--`");
        }
    }

    protected static void validateNonEmptyName(String name) {
        if (name.isEmpty()) {
            throw new CliInteractionInvalidNonEmptyValueException("Option name cannot be empty");
        }
    }

    protected static OptionType getOptionType(String arg) {
        return arg.startsWith("--") ? OptionType.LONG : OptionType.SHORT;
    }

    protected static void validateValueIsEmpty(String value) {
        if (!value.isEmpty()) {
            throw new CliInteractionInvalidEmptyValueException(
                    "Cannot combine multiple options and include a value");
        }
    }

    protected static List<Option> splitCombinedShortOptions(OptionType type, String name) {
        List<Option> options = new ArrayList<>();
        for (char c : name.toCharArray()) {
            options.add(new Option(String.valueOf(c), type));
        }
        return options;
    }
}
