/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.struct.request.abstract_;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import io.valkyrja.validation.rule.contract.RuleContract;
import io.valkyrja.validation.validator.contract.ValidatorContract;

import java.util.List;
import java.util.Map;

public abstract class RequestStruct implements RequestStructContract {

    @Override
    public Map<String, Object> getDataFromRequest(ServerRequestContract request) {
        return getOnlyParamsFromRequest(request, values().toArray(new String[0]));
    }

    @Override
    public boolean determineIfRequestContainsExtraData(ServerRequestContract request) {
        return !getExceptParamsFromRequest(request, values().toArray(new String[0])).isEmpty();
    }

    @Override
    public Map<String, List<RuleContract>> getValidationRules(ServerRequestContract request) {
        return Map.of();
    }

    @Override
    public ValidatorContract validate(ServerRequestContract request) {
        Map<String, List<RuleContract>> rules = getValidationRules(request);
        return () -> rules.isEmpty() || validateAllRules(rules);
    }

    protected boolean validateAllRules(Map<String, List<RuleContract>> rules) {
        return true;
    }

    protected abstract Map<String, Object> getOnlyParamsFromRequest(ServerRequestContract request, String... values);

    protected abstract Map<String, Object> getExceptParamsFromRequest(ServerRequestContract request, String... values);
}