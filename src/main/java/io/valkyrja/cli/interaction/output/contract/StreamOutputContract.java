/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.output.contract;

import java.io.OutputStream;

public interface StreamOutputContract extends OutputContract {

    OutputStream getStream();

    StreamOutputContract withStream(OutputStream stream);
}
