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

public interface ResponseFactoryContract {

    ResponseContract createResponse(String content, StatusCode statusCode, HeaderCollectionContract headers);

    TextResponseContract createTextResponse(String content, StatusCode statusCode, HeaderCollectionContract headers);

    JsonResponseContract createJsonResponse(Map<String, Object> data, StatusCode statusCode, HeaderCollectionContract headers);

    JsonResponseContract createJsonpResponse(String callback, Map<String, Object> data, StatusCode statusCode, HeaderCollectionContract headers);

    RedirectResponseContract createRedirectResponse(String uri, StatusCode statusCode, HeaderCollectionContract headers);
}