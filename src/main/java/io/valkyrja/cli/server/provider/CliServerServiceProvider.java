/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.server.provider;

import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.server.handler.InputHandler;
import io.valkyrja.cli.server.handler.contract.InputHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

public class CliServerServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(InputHandlerContract.class, CliServerServiceProvider::publishInputHandler);
    }

    public static void publishInputHandler(ContainerContract container) {
        container.setSingleton(
                InputHandlerContract.class,
                new InputHandler(
                        container,
                        container.getSingleton(RouterContract.class),
                        container.getSingleton(InputReceivedHandlerContract.class),
                        container.getSingleton(ThrowableCaughtHandlerContract.class),
                        container.getSingleton(ProcessExitingHandlerContract.class),
                        container.getSingleton(CliInteractionConfigContract.class)));
    }
}
