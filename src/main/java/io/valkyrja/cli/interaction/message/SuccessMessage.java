/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.formatter.SuccessFormatter;

public class SuccessMessage extends Message {

    public SuccessMessage(String text) {
        super(text, new SuccessFormatter());
    }
}