/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.collector.contract;

import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.List;

/** Builds {@code Route}s from annotated gRPC service controller classes. */
public interface RouteCollectorContract {

    List<RouteContract> getRoutes(Class<?>... controllerClasses);
}
