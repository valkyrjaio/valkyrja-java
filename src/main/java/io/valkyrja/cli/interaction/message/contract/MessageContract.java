/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message.contract;

import io.valkyrja.cli.interaction.formatter.contract.FormatterContract;

public interface MessageContract {

    String getText();

    String getFormattedText();

    MessageContract withText(String text);

    boolean hasFormatter();

    FormatterContract getFormatter();

    MessageContract withFormatter(FormatterContract formatter);

    MessageContract withoutFormatter();
}
