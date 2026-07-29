/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Fixture service provider registering the {@link GreeterControllerFixture} per-route middleware.
 */
public class GreeterServiceProviderFixture implements ServiceProviderContract {

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
