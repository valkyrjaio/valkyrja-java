/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.event;

import io.valkyrja.event.contract.ArgumentsCapableEventContract;
import java.util.Map;

/** Event that captures the arguments passed when constructed from its id. */
public final class ArgumentsCapableEventFixture implements ArgumentsCapableEventContract {

    private Map<String, Object> arguments = Map.of();

    @Override
    public ArgumentsCapableEventContract setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
        return this;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }
}
