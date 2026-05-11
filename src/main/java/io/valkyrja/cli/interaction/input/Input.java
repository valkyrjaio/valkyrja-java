/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.input;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.option.contract.OptionContract;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Input implements InputContract {

    protected String caller;
    protected String commandName;
    protected List<ArgumentContract> arguments;
    protected List<OptionContract> options;

    public Input() {
        this("valkyrja", "list", new ArrayList<>(), new ArrayList<>());
    }

    public Input(String caller, String commandName, List<ArgumentContract> arguments, List<OptionContract> options) {
        this.caller = caller;
        this.commandName = commandName;
        this.arguments = new ArrayList<>(arguments);
        this.options = new ArrayList<>(options);
    }

    protected Input copy() {
        return new Input(caller, commandName, arguments, options);
    }

    @Override
    public String getCaller() {
        return caller;
    }

    @Override
    public InputContract withCaller(String caller) {
        Input copy = copy();
        copy.caller = caller;
        return copy;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public InputContract withCommandName(String commandName) {
        Input copy = copy();
        copy.commandName = commandName;
        return copy;
    }

    @Override
    public List<ArgumentContract> getArguments() {
        return arguments;
    }

    @Override
    public InputContract withArguments(ArgumentContract... arguments) {
        Input copy = copy();
        copy.arguments = new ArrayList<>(Arrays.asList(arguments));
        return copy;
    }

    @Override
    public InputContract withAddedArgument(ArgumentContract argument) {
        Input copy = copy();
        copy.arguments = new ArrayList<>(arguments);
        copy.arguments.add(argument);
        return copy;
    }

    @Override
    public InputContract withoutArgument(String value) {
        Input copy = copy();
        copy.arguments = arguments.stream()
            .filter(a -> !a.getValue().equals(value))
            .collect(Collectors.toList());
        return copy;
    }

    @Override
    public InputContract withoutArguments() {
        Input copy = copy();
        copy.arguments = new ArrayList<>();
        return copy;
    }

    @Override
    public List<OptionContract> getOptions() {
        return options;
    }

    @Override
    public List<OptionContract> getOption(String name) {
        return options.stream()
            .filter(o -> o.getName().equals(name))
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasOption(String name) {
        return !getOption(name).isEmpty();
    }

    @Override
    public InputContract withOptions(OptionContract... options) {
        Input copy = copy();
        copy.options = new ArrayList<>(Arrays.asList(options));
        return copy;
    }

    @Override
    public InputContract withAddedOption(OptionContract option) {
        Input copy = copy();
        copy.options = new ArrayList<>(this.options);
        copy.options.add(option);
        return copy;
    }

    @Override
    public InputContract withoutOption(String name) {
        Input copy = copy();
        copy.options = options.stream()
            .filter(o -> !o.getName().equals(name))
            .collect(Collectors.toList());
        return copy;
    }

    @Override
    public InputContract withoutOptions() {
        Input copy = copy();
        copy.options = new ArrayList<>();
        return copy;
    }
}
