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

/** A request struct with no no-arg constructor, so reflective instantiation fails. */
public final class FailingRequestStructClass extends ParsedBodyRequestStruct {

    @SuppressWarnings("unused")
    public FailingRequestStructClass(String required) {}

    @Override
    public Map<String, String> asMap() {
        return Map.of();
    }

    @Override
    public List<String> values() {
        return List.of();
    }
}
