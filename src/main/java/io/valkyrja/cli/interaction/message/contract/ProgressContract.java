/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message.contract;

public interface ProgressContract extends MessageContract {

    boolean isComplete();

    ProgressContract withIsComplete(boolean isComplete);

    int getPercentage();

    ProgressContract withPercentage(int percentage);
}