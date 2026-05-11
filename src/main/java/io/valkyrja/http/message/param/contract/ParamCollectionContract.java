/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.param.contract;

import java.util.Map;

public interface ParamCollectionContract {

    boolean has(String key);

    Object get(String key);

    Map<String, Object> getAll();

    Map<String, Object> getOnly(String... keys);

    Map<String, Object> getAllExcept(String... keys);

    ParamCollectionContract with(Map<String, Object> params);

    ParamCollectionContract withAdded(Map<String, Object> params);
}
