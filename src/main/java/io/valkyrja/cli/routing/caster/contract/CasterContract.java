/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.caster.contract;

import io.valkyrja.cli.routing.data.contract.ParameterContract;
import java.util.List;
import org.jspecify.annotations.Nullable;

public interface CasterContract {

    List<@Nullable Object> getCastValues(ParameterContract parameter);
}
