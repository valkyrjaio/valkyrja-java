/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.data.contract;

import java.util.Map;

public interface EventDataContract {
    Map<Class<?>, Map<String, String>> events();

    Map<String, ListenerContract> listeners();
}
