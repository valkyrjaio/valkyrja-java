/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.handler.ResponseSentHandler;
import io.valkyrja.tests.fixtures.http.middleware.PassThroughHttpMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link ResponseSentHandler}. */
final class ResponseSentHandlerTest {

    @Test
    void runsWithAndWithoutMiddleware() {
        var container = new Container();
        container.setSingleton(
                PassThroughHttpMiddlewareFixture.class, new PassThroughHttpMiddlewareFixture());

        assertDoesNotThrow(
                () ->
                        new ResponseSentHandler(new Container())
                                .responseSent(
                                        mock(ServerRequestContract.class),
                                        mock(ResponseContract.class)));
        assertDoesNotThrow(
                () ->
                        new ResponseSentHandler(container, PassThroughHttpMiddlewareFixture.class)
                                .responseSent(
                                        mock(ServerRequestContract.class),
                                        mock(ResponseContract.class)));
    }
}
