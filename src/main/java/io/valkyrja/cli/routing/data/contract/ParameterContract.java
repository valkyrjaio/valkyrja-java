/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.data.contract;

import java.util.List;

public interface ParameterContract {

    String getName();

    ParameterContract withName(String name);

    String getDescription();

    ParameterContract withDescription(String description);

    boolean hasFirstValue();

    String getFirstValue();

    List<String> getCastValues();

    boolean areValuesValid();

    ParameterContract validateValues();
}
