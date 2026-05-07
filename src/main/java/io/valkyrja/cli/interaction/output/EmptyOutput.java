/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.output;

import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.EmptyOutputContract;

public class EmptyOutput extends Output implements EmptyOutputContract {

    @Override
    protected void outputMessage(MessageContract message) {}

    @Override
    protected Output newInstance() {
        return new EmptyOutput();
    }
}
