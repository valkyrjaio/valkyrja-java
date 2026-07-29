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

/** Fixture service provider registering the {@link GreeterController} per-route middleware. */
public class GreeterServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                GreeterController.MatchedMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterController.MatchedMiddleware.class,
                                        new GreeterController.MatchedMiddleware()),
                GreeterController.DispatchedMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterController.DispatchedMiddleware.class,
                                        new GreeterController.DispatchedMiddleware()),
                GreeterController.CaughtMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterController.CaughtMiddleware.class,
                                        new GreeterController.CaughtMiddleware()),
                GreeterController.SendingMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterController.SendingMiddleware.class,
                                        new GreeterController.SendingMiddleware()),
                GreeterController.ResponseSentMiddleware.class,
                        c ->
                                c.setSingleton(
                                        GreeterController.ResponseSentMiddleware.class,
                                        new GreeterController.ResponseSentMiddleware()));
    }
}
