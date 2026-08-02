/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
        return messages.stream().map(MessageContract::getText).collect(Collectors.joining());
    }

    @Override
    public String getFormattedText() {
        return messages.stream()
                .map(MessageContract::getFormattedText)
                .collect(Collectors.joining());
    }
}
