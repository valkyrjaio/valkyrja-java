/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request.contract;

import io.valkyrja.http.message.file.collection.contract.UploadedFileCollectionContract;
import io.valkyrja.http.message.param.contract.AttributeParamCollectionContract;
import io.valkyrja.http.message.param.contract.CookieParamCollectionContract;
import io.valkyrja.http.message.param.contract.ParsedBodyParamCollectionContract;
import io.valkyrja.http.message.param.contract.QueryParamCollectionContract;
import io.valkyrja.http.message.param.contract.ServerParamCollectionContract;

public interface ServerRequestContract extends RequestContract {

    ServerParamCollectionContract getServerParams();

    ServerRequestContract withServerParams(ServerParamCollectionContract server);

    CookieParamCollectionContract getCookieParams();

    ServerRequestContract withCookieParams(CookieParamCollectionContract cookies);

    QueryParamCollectionContract getQueryParams();

    ServerRequestContract withQueryParams(QueryParamCollectionContract query);

    UploadedFileCollectionContract getUploadedFiles();

    ServerRequestContract withUploadedFiles(UploadedFileCollectionContract uploadedFiles);

    ParsedBodyParamCollectionContract getParsedBody();

    ServerRequestContract withParsedBody(ParsedBodyParamCollectionContract params);

    AttributeParamCollectionContract getAttributes();

    ServerRequestContract withAttributes(AttributeParamCollectionContract attributes);

    boolean isXmlHttpRequest();
}
