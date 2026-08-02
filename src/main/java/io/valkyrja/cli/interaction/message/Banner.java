/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.message;

public class Banner extends Message {

    protected Messages messages;

    public Banner(Message message) {
        super(message.getText());

        String text = "    " + this.text + "    ";
        int textLength = text.length();
        String spaces = " ".repeat(textLength);

        this.messages =
                new Messages(
                        new NewLine(),
                        (Message) message.withText(spaces),
                        new NewLine(),
                        (Message) message.withText(text),
                        new NewLine(),
                        (Message) message.withText(spaces),
                        new NewLine());
    }

    @Override
    public String getText() {
        return messages.getText();
    }

    @Override
    public String getFormattedText() {
        return messages.getFormattedText();
    }
}
