/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.data.contract;

import java.util.List;

public interface ParameterContract {

    String getName();

    ParameterContract withName(String name);

    String getDescription();

    ParameterContract withDescription(String description);

    boolean hasFirstValue();

    String getFirstValue();

    List<String> getCastValues();

    boolean areValuesValid();

    ParameterContract validateValues();
}
