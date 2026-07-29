/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.http.struct;

import io.valkyrja.http.struct.request.abstract_.ParsedBodyRequestStruct;
import java.util.List;
import java.util.Map;

/** Concrete parsed-body request struct selecting name and email fields. */
public final class ParsedBodyStructClass extends ParsedBodyRequestStruct {

    @Override
    public Map<String, String> asMap() {
        return Map.of("name", "name", "email", "email");
    }

    @Override
    public List<String> values() {
        return List.of("name", "email");
    }
}
