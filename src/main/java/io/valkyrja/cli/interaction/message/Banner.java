/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
