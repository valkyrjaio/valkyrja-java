/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.data;

import io.valkyrja.application.data.contract.GrpcConfigContract;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.GrpcApplicationComponentProvider;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.grpc.middleware.contract.CallReceivedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import java.util.List;
import java.util.function.Consumer;

public record GrpcConfig(
        String namespace,
        String dir,
        String version,
        String environment,
        boolean debugMode,
        String timezone,
        String key,
        String dataPath,
        String dataNamespace,
        Integer port,
        Integer maxInboundMessages,
        List<ComponentProviderContract> providers,
        List<Consumer<ApplicationContract>> callbacks,
        List<Class<? extends CallReceivedMiddlewareContract>> callReceivedMiddleware,
        List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware,
        List<Class<? extends RouteNotMatchedMiddlewareContract>> routeNotMatchedMiddleware,
        List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware,
        List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware,
        List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware,
        List<Class<? extends ResponseSentMiddlewareContract>> responseSentMiddleware)
        implements GrpcConfigContract {

    public GrpcConfig {
        maxInboundMessages =
                maxInboundMessages == null
                        ? Integer.valueOf(GrpcConfigContract.DEFAULT_MAX_INBOUND_MESSAGES)
                        : maxInboundMessages;
        providers = List.copyOf(providers);
        callbacks = List.copyOf(callbacks);
        callReceivedMiddleware = List.copyOf(callReceivedMiddleware);
        routeMatchedMiddleware = List.copyOf(routeMatchedMiddleware);
        routeNotMatchedMiddleware = List.copyOf(routeNotMatchedMiddleware);
        routeDispatchedMiddleware = List.copyOf(routeDispatchedMiddleware);
        throwableCaughtMiddleware = List.copyOf(throwableCaughtMiddleware);
        sendingResponseMiddleware = List.copyOf(sendingResponseMiddleware);
        responseSentMiddleware = List.copyOf(responseSentMiddleware);
    }

    public GrpcConfig() {
        this(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                false,
                "UTC",
                "secret_app_key",
                "app/grpc/provider/data",
                "app.grpc.provider.data",
                50051,
                GrpcConfigContract.DEFAULT_MAX_INBOUND_MESSAGES,
                List.of(new GrpcApplicationComponentProvider()),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }
}
