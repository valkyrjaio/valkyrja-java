/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.cli.middleware;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;

/** Implements only the input-received contract, none of the route-matched/dispatched/etc. ones. */
public final class InputReceivedOnlyMiddleware implements InputReceivedMiddlewareContract {

    @Override
    public Object inputReceived(InputContract input, InputReceivedHandlerContract handler) {
        return handler.inputReceived(input);
    }
}
