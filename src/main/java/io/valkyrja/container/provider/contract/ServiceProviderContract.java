/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.provider.contract;

import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;
import java.util.function.Consumer;

public interface ServiceProviderContract {

    /** Custom publish callbacks for services provided by this provider. */
    Map<Class<?>, Consumer<ContainerContract>> publishers();
}
