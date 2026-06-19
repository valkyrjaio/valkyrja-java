/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.server.middleware;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.Response;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.http.server.middleware.CacheResponseMiddleware;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Test the {@link CacheResponseMiddleware}. */
final class CacheResponseMiddlewareTest {

    /** Exposes the protected cache path so the test can manipulate the cache file directly. */
    private static final class ExposedCache extends CacheResponseMiddleware {
        ExposedCache(String filePath) {
            super(filePath);
        }

        String pathFor(ServerRequestContract request) {
            return getCachePathForRequest(request);
        }
    }

    private static ServerRequestContract request() {
        var request = mock(ServerRequestContract.class);
        when(request.getUri()).thenReturn(new Uri("/page"));
        when(request.getMethod()).thenReturn(RequestMethod.GET);
        return request;
    }

    private static RequestReceivedHandlerContract receivedHandler() {
        var handler = mock(RequestReceivedHandlerContract.class);
        when(handler.requestReceived(any()))
                .thenAnswer(inv -> new RequestReceivedResult(inv.getArgument(0), null));
        return handler;
    }

    @Test
    void terminatedCachesSuccessfulResponse(@TempDir Path dir) {
        var cache = new ExposedCache(dir.toString());
        var request = request();
        var handler = mock(TerminatedHandlerContract.class);

        cache.terminated(request, Response.create("body", StatusCode.OK, new HeaderCollection()), handler);

        assertTrue(new File(cache.pathFor(request)).exists());
        verify(handler).terminated(any(), any());
    }

    @Test
    void terminatedSkipsCachingServerErrors(@TempDir Path dir) {
        var cache = new ExposedCache(dir.toString());
        var request = request();

        cache.terminated(
                request,
                Response.create("err", StatusCode.INTERNAL_SERVER_ERROR, new HeaderCollection()),
                mock(TerminatedHandlerContract.class));

        assertFalse(new File(cache.pathFor(request)).exists());
    }

    @Test
    void requestReceivedWithoutCacheDelegates(@TempDir Path dir) {
        var cache = new ExposedCache(dir.toString());
        var handler = receivedHandler();

        cache.requestReceived(request(), handler);

        verify(handler).requestReceived(any());
    }

    @Test
    void requestReceivedWithFreshCacheDelegates(@TempDir Path dir) throws IOException {
        var cache = new ExposedCache(dir.toString());
        var request = request();
        writeCacheFile(cache.pathFor(request));
        var handler = receivedHandler();

        cache.requestReceived(request, handler);

        verify(handler).requestReceived(any());
    }

    @Test
    void requestReceivedDeletesExpiredCache(@TempDir Path dir) throws IOException {
        var cache = new ExposedCache(dir.toString());
        var request = request();
        var cacheFile = new File(cache.pathFor(request));
        writeCacheFile(cacheFile.getPath());
        // Mark the cache file as older than the TTL (1800s).
        assertTrue(cacheFile.setLastModified(System.currentTimeMillis() - 3_600_000L));

        cache.requestReceived(request, receivedHandler());

        assertFalse(cacheFile.exists());
    }

    @Test
    void requestReceivedIgnoresDeleteFailureOnExpiredCache(@TempDir Path dir) throws IOException {
        var cache = new ExposedCache(dir.toString());
        var request = request();
        var cacheFile = new File(cache.pathFor(request));
        writeCacheFile(cacheFile.getPath());
        assertTrue(cacheFile.setLastModified(System.currentTimeMillis() - 3_600_000L));
        var handler = receivedHandler();

        try (var files = mockStatic(Files.class)) {
            files.when(() -> Files.delete(any())).thenThrow(new IOException("locked"));

            cache.requestReceived(request, handler);
        }

        verify(handler).requestReceived(any());
    }

    @Test
    void hashedPathFallsBackToHashCodeWhenMd5Unavailable(@TempDir Path dir) {
        var cache = new ExposedCache(dir.toString());
        var request = request();

        try (var digest = mockStatic(MessageDigest.class)) {
            digest.when(() -> MessageDigest.getInstance("MD5"))
                    .thenThrow(new NoSuchAlgorithmException("no MD5"));

            assertNotNull(cache.pathFor(request));
        }
    }

    private static void writeCacheFile(String path) throws IOException {
        Path p = Path.of(path);
        Files.createDirectories(p.getParent());
        Files.writeString(p, "cached");
    }
}
