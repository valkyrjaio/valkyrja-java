/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.fixtures.http.struct;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.request.abstract_.ParsedBodyRequestStruct;
import io.valkyrja.validation.rule.contract.RuleContract;
import java.util.List;
import java.util.Map;

/** A request struct that declares validation rules, forcing rule evaluation. */
public final class ValidatingStructClass extends ParsedBodyRequestStruct {

    @Override
    public Map<String, List<RuleContract>> getValidationRules(ServerRequestContract request) {
        return Map.of("name", List.of());
    }

    @Override
    public Map<String, String> asMap() {
        return Map.of("name", "name");
    }

    @Override
    public List<String> values() {
        return List.of("name");
    }
}
