/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.response.factory.contract;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.JsonResponseContract;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.response.contract.TextResponseContract;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public interface ResponseFactoryContract {

    ResponseContract createResponse(
            @Nullable String content,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers);

    TextResponseContract createTextResponse(
            @Nullable String content,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers);

    JsonResponseContract createJsonResponse(
            @Nullable Map<String, Object> data,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers);

    JsonResponseContract createJsonpResponse(
            String callback,
            @Nullable Map<String, Object> data,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers);

    RedirectResponseContract createRedirectResponse(
            @Nullable String uri,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers);
}
