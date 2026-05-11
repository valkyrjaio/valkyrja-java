/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.type.enum_.contract;

import java.util.List;
import java.util.Map;

public interface ArrayableContract {

    Map<String, String> asMap();

    List<String> values();
}
