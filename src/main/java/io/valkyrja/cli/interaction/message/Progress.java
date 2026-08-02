/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.formatter.contract.FormatterContract;
import io.valkyrja.cli.interaction.message.contract.ProgressContract;
import org.jspecify.annotations.Nullable;

public class Progress extends Message implements ProgressContract {

    protected boolean isComplete;
    protected int percentage;

    public Progress(String text) {
        this(text, false, 0, null);
    }

    public Progress(
            String text,
            boolean isComplete,
            int percentage,
            @Nullable FormatterContract formatter) {
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
