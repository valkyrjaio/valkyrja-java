/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.input.contract;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;
import io.valkyrja.cli.interaction.option.contract.OptionContract;
import java.util.List;

public interface InputContract {

    String getCaller();

    InputContract withCaller(String caller);

    String getCommandName();

    InputContract withCommandName(String commandName);

    List<ArgumentContract> getArguments();

    InputContract withArguments(ArgumentContract... arguments);

    InputContract withAddedArgument(ArgumentContract argument);

    InputContract withoutArgument(String value);

    InputContract withoutArguments();

    List<OptionContract> getOptions();

    List<OptionContract> getOption(String name);

    boolean hasOption(String name);

    InputContract withOptions(OptionContract... options);

    InputContract withAddedOption(OptionContract option);

    InputContract withoutOption(String name);

    InputContract withoutOptions();
}
