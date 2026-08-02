/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.data;

import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;

public class CliInteractionConfig implements CliInteractionConfigContract {

    protected boolean isQuiet;
    protected boolean isInteractive;
    protected boolean isSilent;

    public CliInteractionConfig() {
        this(false, true, false);
    }

    public CliInteractionConfig(boolean isQuiet, boolean isInteractive, boolean isSilent) {
        this.isQuiet = isQuiet;
        this.isInteractive = isInteractive;
        this.isSilent = isSilent;
    }

    @Override
    public boolean isQuiet() {
        return isQuiet;
    }

    public void setQuiet(boolean isQuiet) {
        this.isQuiet = isQuiet;
    }

    @Override
    public boolean isInteractive() {
        return isInteractive;
    }

    public void setInteractive(boolean isInteractive) {
        this.isInteractive = isInteractive;
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public void setSilent(boolean isSilent) {
        this.isSilent = isSilent;
    }
}
