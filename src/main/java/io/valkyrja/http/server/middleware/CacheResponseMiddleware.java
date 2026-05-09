/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.server.middleware;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RequestReceivedMiddlewareContract;
import io.valkyrja.http.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.http.server.generator.ResponseFileGenerator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CacheResponseMiddleware implements RequestReceivedMiddlewareContract, TerminatedMiddlewareContract {

    protected String filePath;
    protected boolean debug;
    protected int ttl = 1800;

    public CacheResponseMiddleware(String filePath) {
        this(filePath, false);
    }

    public CacheResponseMiddleware(String filePath, boolean debug) {
        this.filePath = filePath;
        this.debug = debug;
    }

    @Override
    public RequestReceivedResult requestReceived(ServerRequestContract request, RequestReceivedHandlerContract handler) {
        String cacheFilePath = getCachePathForRequest(request);
        File cacheFile = new File(cacheFilePath);

        if (shouldLoadCachedResponse(cacheFile)) {
            if (isCachedResponseFileExpired(cacheFile)) {
                cacheFile.delete();
                return handler.requestReceived(request);
            }
        }

        return handler.requestReceived(request);
    }

    @Override
    public void terminated(ServerRequestContract request, ResponseContract response, TerminatedHandlerContract handler) {
        if (!shouldNotCache(request, response)) {
            String cacheFilePath = getCachePathForRequest(request);
            ResponseFileGenerator generator = new ResponseFileGenerator(response, cacheFilePath);
            generator.generateFile();
        }

        handler.terminated(request, response);
    }

    protected boolean shouldNotCache(ServerRequestContract request, ResponseContract response) {
        return response.getStatusCode().getValue() >= StatusCode.INTERNAL_SERVER_ERROR.getValue()
            || new File(getCachePathForRequest(request)).exists();
    }

    protected boolean shouldLoadCachedResponse(File cacheFile) {
        return !debug && cacheFile.exists();
    }

    protected boolean isCachedResponseFileExpired(File cacheFile) {
        long lastModified = cacheFile.lastModified();
        return lastModified > 0 && (System.currentTimeMillis() / 1000L) - (lastModified / 1000L) > ttl;
    }

    protected String getHashedPath(ServerRequestContract request) {
        String input = request.getUri().getPath() + request.getMethod().getValue();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }

    protected String getCachePathForRequest(ServerRequestContract request) {
        return "/" + filePath.replaceAll("^/+|/+$", "") + "/" + getHashedPath(request);
    }
}