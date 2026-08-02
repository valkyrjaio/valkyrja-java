/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.message.contract;

public interface ProgressContract extends MessageContract {

    boolean isComplete();

    ProgressContract withIsComplete(boolean isComplete);

    int getPercentage();

    ProgressContract withPercentage(int percentage);
}
