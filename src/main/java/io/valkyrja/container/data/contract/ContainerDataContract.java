/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.data.contract;

import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface ContainerDataContract {
    Map<Class<?>, Class<?>> aliases();

    Map<Class<?>, Consumer<ContainerContract>> callbacks();

    Map<Class<?>, BiFunction<ContainerContract, Map<String, Object>, Object>> services();

    Map<Class<?>, Class<?>> singletons();
}
