/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.container;

import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;

/** Testable service whose only constructor requires the container. */
public final class ServiceFixture {

    private final ContainerContract container;

    public ServiceFixture(ContainerContract container) {
        this.container = container;
    }

    public static ServiceFixture make(ContainerContract container, Map<String, Object> arguments) {
        return new ServiceFixture(container);
    }

    public ContainerContract getContainer() {
        return container;
    }
}
