/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.input.factory;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;
import io.valkyrja.cli.interaction.argument.factory.ArgumentFactory;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.interaction.option.contract.OptionContract;
import io.valkyrja.cli.interaction.option.factory.OptionFactory;
import java.util.ArrayList;
import java.util.List;

public abstract class InputFactory {

    public static InputContract fromGlobals(
            String[] args, String applicationName, String commandName) {
        return inputWithProperties(new Input(), args, applicationName, commandName);
    }

    protected static InputContract inputWithProperties(
            InputContract input, String[] args, String applicationName, String commandName) {
        List<ArgumentContract> arguments = new ArrayList<>();
        List<OptionContract> options = new ArrayList<>();

        // args carry no leading program name, so the caller is never spelled here — it comes from
        // the application name — and the first slot is the command name. Options are matched first
        // so an option spelled in that slot is parsed as one rather than swallowed as the command
        // name; the default command name then stands.
        boolean endOfOptions = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!endOfOptions && arg.equals("--")) {
                // POSIX end-of-options marker: the `--` itself is consumed, and every arg after
                // it is an operand — never an option, however many dashes it starts with. A
                // second `--` is therefore an ordinary operand.
                endOfOptions = true;
            } else if (!endOfOptions && !arg.equals("-") && arg.startsWith("-")) {
                // A lone `-` is an operand by convention (it names standard input), not an option.
                List<Option> parsed = OptionFactory.fromArg(arg);
                options.addAll(parsed);
            } else if (i == 0) {
                commandName = arg;
            } else {
                arguments.add(ArgumentFactory.fromArg(arg));
            }
        }

        return input.withCaller(applicationName)
                .withCommandName(commandName)
                .withArguments(arguments.toArray(new ArgumentContract[0]))
                .withOptions(options.toArray(new OptionContract[0]));
    }
}
