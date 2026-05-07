/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.formatter.contract;

import io.valkyrja.cli.interaction.format.contract.FormatContract;
import java.util.List;

public interface FormatterContract {

    List<FormatContract> getFormats();

    FormatterContract withFormats(FormatContract... formats);

    String formatText(String text);
}
