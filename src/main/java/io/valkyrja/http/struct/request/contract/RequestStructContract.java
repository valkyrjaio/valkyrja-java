/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.struct.request.contract;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.contract.StructContract;
import io.valkyrja.validation.rule.contract.RuleContract;
import io.valkyrja.validation.validator.contract.ValidatorContract;
import java.util.List;
import java.util.Map;

public interface RequestStructContract extends StructContract {

    Map<String, List<RuleContract>> getValidationRules(ServerRequestContract request);

    ValidatorContract validate(ServerRequestContract request);

    Map<String, Object> getDataFromRequest(ServerRequestContract request);

    boolean determineIfRequestContainsExtraData(ServerRequestContract request);
}
