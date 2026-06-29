/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.fixtures.http.struct;

import io.valkyrja.http.struct.request.abstract_.QueryRequestStruct;
import java.util.List;
import java.util.Map;

/** Concrete query request struct selecting the page field. */
public final class QueryStructClass extends QueryRequestStruct {

    @Override
    public Map<String, String> asMap() {
        return Map.of("page", "page");
    }

    @Override
    public List<String> values() {
        return List.of("page");
    }
}
