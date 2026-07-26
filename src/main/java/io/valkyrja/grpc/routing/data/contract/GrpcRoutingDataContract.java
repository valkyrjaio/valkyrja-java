/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.data.contract;

import java.util.Map;
import java.util.function.Supplier;

/**
 * The cached service map generated ahead of time (by Sindri) from {@code @Service} controllers,
 * keyed by fully-qualified method name. Parallels HTTP's {@code HttpRoutingDataContract} and CLI's
 * {@code CliRoutingDataContract}.
 */
public interface GrpcRoutingDataContract {

    Map<String, Supplier<RouteContract>> routes();
}
