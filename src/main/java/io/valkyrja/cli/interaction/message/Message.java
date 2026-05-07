/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.formatter.contract.FormatterContract;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionNoFormatterException;

public class Message implements MessageContract {

    protected String text;
    protected FormatterContract formatter;

    public Message(String text) {
        this(text, null);
    }

    public Message(String text, FormatterContract formatter) {
        this.text = text;
        this.formatter = formatter;
    }

    protected Message copy() {
        return new Message(text, formatter);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getFormattedText() {
        String t = getText();
        if (formatter == null) {
            return t;
        }
        return formatter.formatText(t);
    }

    @Override
    public MessageContract withText(String text) {
        Message copy = copy();
        copy.text = text;
        return copy;
    }

    @Override
    public boolean hasFormatter() {
        return formatter != null;
    }

    @Override
    public FormatterContract getFormatter() {
        if (formatter == null) {
            throw new CliInteractionNoFormatterException("No formatter has been set");
        }
        return formatter;
    }

    @Override
    public MessageContract withFormatter(FormatterContract formatter) {
        Message copy = copy();
        copy.formatter = formatter;
        return copy;
    }

    @Override
    public MessageContract withoutFormatter() {
        Message copy = copy();
        copy.formatter = null;
        return copy;
    }
}