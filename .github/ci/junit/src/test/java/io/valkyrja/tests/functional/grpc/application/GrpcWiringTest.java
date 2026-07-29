/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.functional.grpc.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.entry.Grpc;
import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.tests.fixtures.grpc.GreeterComponentProvider;
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
                List.of(new GreeterComponentProvider()),
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
