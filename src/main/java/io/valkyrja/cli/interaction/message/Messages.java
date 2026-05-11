/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.message.contract.MessageContract;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Messages extends Message {

    protected List<MessageContract> messages;

    public Messages(MessageContract... messages) {
        super("");
        this.messages = Arrays.asList(messages);
    }

    @Override
    public String getText() {
        return messages.stream()
            .map(MessageContract::getText)
            .collect(Collectors.joining());
    }

    @Override
    public String getFormattedText() {
        return messages.stream()
            .map(MessageContract::getFormattedText)
            .collect(Collectors.joining());
    }
}
