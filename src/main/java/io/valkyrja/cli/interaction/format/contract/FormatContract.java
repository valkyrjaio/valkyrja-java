/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.format.contract;

public interface FormatContract {

    String getSetCode();

    FormatContract withSetCode(String setCode);

    String getUnsetCode();

    FormatContract withUnsetCode(String unsetCode);
}