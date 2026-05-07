/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.data.contract;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;
import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import java.util.List;

public interface ArgumentParameterContract extends ParameterContract {

    ArgumentMode getMode();

    ArgumentParameterContract withMode(ArgumentMode mode);

    ArgumentValueMode getValueMode();

    ArgumentParameterContract withValueMode(ArgumentValueMode valueMode);

    List<ArgumentContract> getArguments();

    ArgumentParameterContract withArguments(ArgumentContract... arguments);

    ArgumentParameterContract withAddedArguments(ArgumentContract... arguments);
}
