/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.request.contract;

import io.valkyrja.http.message.param.contract.ParsedJsonParamCollectionContract;

public interface JsonServerRequestContract extends ServerRequestContract {

    ParsedJsonParamCollectionContract getParsedJson();

    JsonServerRequestContract withParsedJson(ParsedJsonParamCollectionContract params);
}
