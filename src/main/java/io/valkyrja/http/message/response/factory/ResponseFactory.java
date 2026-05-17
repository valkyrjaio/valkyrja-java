/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.response.factory;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.JsonResponse;
import io.valkyrja.http.message.response.RedirectResponse;
import io.valkyrja.http.message.response.Response;
import io.valkyrja.http.message.response.TextResponse;
import io.valkyrja.http.message.response.contract.JsonResponseContract;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.response.contract.TextResponseContract;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.message.uri.factory.UriFactory;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class ResponseFactory implements ResponseFactoryContract {

    @Override
    public ResponseContract createResponse(
            @Nullable String content,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers) {
        return Response.create(content, statusCode, headers);
    }

    @Override
    public TextResponseContract createTextResponse(
            @Nullable String content,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers) {
        return TextResponse.create(content, statusCode, headers);
    }

    @Override
    public JsonResponseContract createJsonResponse(
            @Nullable Map<String, Object> data,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers) {
        return JsonResponse.createFromData(data, statusCode, headers);
    }

    @Override
    public JsonResponseContract createJsonpResponse(
            String callback,
            @Nullable Map<String, Object> data,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers) {
        return createJsonResponse(data, statusCode, headers).withCallback(callback);
    }

    @Override
    public RedirectResponseContract createRedirectResponse(
            @Nullable String uri,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers) {
        return RedirectResponse.createFromUri(
                UriFactory.fromString(uri != null ? uri : "/"), statusCode, headers);
    }
}
