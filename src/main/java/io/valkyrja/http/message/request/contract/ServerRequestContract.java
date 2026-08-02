/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
