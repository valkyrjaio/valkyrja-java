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
import io.valkyrja.cli.interaction.message.contract.ProgressContract;

public class Progress extends Message implements ProgressContract {

    protected boolean isComplete;
    protected int percentage;

    public Progress(String text) {
        this(text, false, 0, null);
    }

    public Progress(String text, boolean isComplete, int percentage, FormatterContract formatter) {
        super(text, formatter);
        this.isComplete = isComplete;
        this.percentage = percentage;
    }

    protected Progress copy() {
        return new Progress(text, isComplete, percentage, formatter);
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public ProgressContract withIsComplete(boolean isComplete) {
        Progress copy = copy();
        copy.isComplete = isComplete;
        return copy;
    }

    @Override
    public int getPercentage() {
        return percentage;
    }

    @Override
    public ProgressContract withPercentage(int percentage) {
        Progress copy = copy();
        copy.percentage = percentage;
        return copy;
    }
}
