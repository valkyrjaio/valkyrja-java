/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.functional.grpc.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.Grpc;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.tests.fixtures.grpc.GreeterComponentProviderFixture;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * End-to-end wiring: a {@code GrpcConfig} whose component provider registers a {@code @Service}
 * controller boots the full stack (providers → container → service map → Router → ServiceHandler)
 * and dispatches a call.
 */
final class GrpcWiringTest {

    private GrpcConfig config() {
        return new GrpcConfig(
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
                1000,
                List.of(new GreeterComponentProviderFixture()),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    @Test
    void dispatchesRegisteredGrpcServiceRoute() {
        ServiceResponseContract response =
                Grpc.handle(config(), ServiceCall.unary("/pkg.Greeter/SayHello", "req"));

        assertTrue(response.getStatus().isOk());
        assertEquals("hello", response.getMessages().iterator().next());
    }

    @Test
    void unknownMethodReturnsUnimplemented() {
        ServiceResponseContract response =
                Grpc.handle(config(), ServiceCall.unary("/pkg.Greeter/Missing", "req"));

        assertEquals(StatusCode.UNIMPLEMENTED, response.getStatus().getCode());
    }

    @Test
    void streamingRouteIsRegisteredWithFlags() {
        ServiceResponseContract response =
                Grpc.handle(config(), ServiceCall.unary("/pkg.Greeter/StreamHellos", "req"));

        assertTrue(response.getStatus().isOk());
    }
}
