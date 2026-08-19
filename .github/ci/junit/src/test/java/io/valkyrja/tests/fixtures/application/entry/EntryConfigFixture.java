/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.application.entry;

import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import java.util.List;
import java.util.function.Consumer;

/** Framework default config for the worker entry adapters, rebound to a chosen port. */
public final class EntryConfigFixture {

    private EntryConfigFixture() {}

    /** An {@link HttpConfig} with the framework defaults, bound to {@code port}. */
    public static HttpConfig httpOnPort(int port) {
        return httpOnPort(port, null);
    }

    /**
     * An {@link HttpConfig} with the framework defaults, bound to {@code port}, that runs {@code
     * onBootstrap} against the application during bootstrap (via the config callbacks). Lets a
     * smoke test capture the bootstrapped application so it can bind an observable request handler
     * before driving a request.
     */
    public static HttpConfig httpOnPort(int port, Consumer<ApplicationContract> onBootstrap) {
        HttpConfig base = new HttpConfig();

        return new HttpConfig(
                base.namespace(),
                base.dir(),
                base.version(),
                base.environment(),
                base.debugMode(),
                base.timezone(),
                base.key(),
                base.dataPath(),
                base.dataNamespace(),
                port,
                base.providers(),
                onBootstrap == null ? base.callbacks() : List.of(onBootstrap),
                base.requestReceivedMiddleware(),
                base.routeMatchedMiddleware(),
                base.routeNotMatchedMiddleware(),
                base.routeDispatchedMiddleware(),
                base.throwableCaughtMiddleware(),
                base.sendingResponseMiddleware(),
                base.responseSentMiddleware());
    }

    /** A {@link GrpcConfig} with the framework defaults, bound to {@code port}. */
    public static GrpcConfig grpcOnPort(int port) {
        GrpcConfig base = new GrpcConfig();

        return new GrpcConfig(
                base.namespace(),
                base.dir(),
                base.version(),
                base.environment(),
                base.debugMode(),
                base.timezone(),
                base.key(),
                base.dataPath(),
                base.dataNamespace(),
                port,
                base.maxInboundMessages(),
                base.providers(),
                base.callbacks(),
                base.callReceivedMiddleware(),
                base.routeMatchedMiddleware(),
                base.routeNotMatchedMiddleware(),
                base.routeDispatchedMiddleware(),
                base.throwableCaughtMiddleware(),
                base.sendingResponseMiddleware(),
                base.responseSentMiddleware());
    }
}
