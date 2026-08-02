/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.provider;

import io.valkyrja.application.data.contract.ConfigContract;
import io.valkyrja.cli.interaction.data.CliInteractionConfig;
import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.interaction.output.factory.OutputFactory;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

public class CliInteractionServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                CliInteractionConfigContract.class, CliInteractionServiceProvider::publishConfig,
                OutputFactoryContract.class, CliInteractionServiceProvider::publishOutputFactory);
    }

    public static void publishConfig(ContainerContract container) {
        ConfigContract config = container.getSingleton(ConfigContract.class);
        if (config instanceof CliInteractionConfigContract interactionConfig) {
            container.setSingleton(CliInteractionConfigContract.class, interactionConfig);
            return;
        }
        container.setSingleton(CliInteractionConfigContract.class, new CliInteractionConfig());
    }

    public static void publishOutputFactory(ContainerContract container) {
        CliInteractionConfigContract config =
                container.getSingleton(CliInteractionConfigContract.class);
        container.setSingleton(OutputFactoryContract.class, new OutputFactory(config));
    }
}
