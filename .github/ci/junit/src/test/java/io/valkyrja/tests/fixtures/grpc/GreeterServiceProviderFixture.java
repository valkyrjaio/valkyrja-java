/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Fixture service provider registering the {@link GreeterControllerFixture} per-route middleware.
 */
public final class GreeterServiceProviderFixture implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                GreeterControllerFixture.MatchedMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterControllerFixture.MatchedMiddleware.class,
                                        new GreeterControllerFixture.MatchedMiddleware()),
                GreeterControllerFixture.DispatchedMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterControllerFixture.DispatchedMiddleware.class,
                                        new GreeterControllerFixture.DispatchedMiddleware()),
                GreeterControllerFixture.CaughtMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterControllerFixture.CaughtMiddleware.class,
                                        new GreeterControllerFixture.CaughtMiddleware()),
                GreeterControllerFixture.SendingMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterControllerFixture.SendingMiddleware.class,
                                        new GreeterControllerFixture.SendingMiddleware()),
                GreeterControllerFixture.ResponseSentMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterControllerFixture.ResponseSentMiddleware.class,
                                        new GreeterControllerFixture.ResponseSentMiddleware()));
    }
}
