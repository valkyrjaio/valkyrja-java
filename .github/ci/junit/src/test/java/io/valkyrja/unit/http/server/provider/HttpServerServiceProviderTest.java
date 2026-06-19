/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.server.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;
import io.valkyrja.http.server.middleware.CacheResponseMiddleware;
import io.valkyrja.http.server.middleware.routematched.RequestStructMiddleware;
import io.valkyrja.http.server.middleware.routematched.ResponseStructMiddleware;
import io.valkyrja.http.server.middleware.throwablecaught.LogThrowableCaughtMiddleware;
import io.valkyrja.log.logger.contract.LoggerContract;

import io.valkyrja.http.server.provider.HttpServerServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpServerServiceProvider}. */
final class HttpServerServiceProviderTest {

    @Test
    void publishersExposesFiveBindings() {
        assertEquals(5, new HttpServerServiceProvider().publishers().size());
    }

    @Test
    void publishMethodsBindEachService() {
        var container = new Container();
        container.setSingleton(ApplicationContract.class, mock(ApplicationContract.class));
        container.setSingleton(
                ThrowableCaughtHandlerContract.class, mock(ThrowableCaughtHandlerContract.class));
        container.setSingleton(RouterContract.class, mock(RouterContract.class));
        container.setSingleton(
                RequestReceivedHandlerContract.class, mock(RequestReceivedHandlerContract.class));
        container.setSingleton(
                SendingResponseHandlerContract.class, mock(SendingResponseHandlerContract.class));
        container.setSingleton(
                TerminatedHandlerContract.class, mock(TerminatedHandlerContract.class));
        container.setSingleton(LoggerContract.class, mock(LoggerContract.class));

        HttpServerServiceProvider.publishLogThrowableCaughtMiddleware(container);
        HttpServerServiceProvider.publishRequestStructMiddleware(container);
        HttpServerServiceProvider.publishResponseStructMiddleware(container);
        HttpServerServiceProvider.publishCacheResponseMiddleware(container);
        HttpServerServiceProvider.publishRequestHandler(container);

        assertNotNull(container.getSingleton(RequestHandlerContract.class));
        assertNotNull(container.getSingleton(LogThrowableCaughtMiddleware.class));
        assertNotNull(container.getSingleton(RequestStructMiddleware.class));
        assertNotNull(container.getSingleton(ResponseStructMiddleware.class));
        assertNotNull(container.getSingleton(CacheResponseMiddleware.class));
    }
}
