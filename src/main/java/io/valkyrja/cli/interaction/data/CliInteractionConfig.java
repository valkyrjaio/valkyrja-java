/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
