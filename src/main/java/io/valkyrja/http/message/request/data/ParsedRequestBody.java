/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.request.data;

import io.valkyrja.http.message.file.contract.UploadedFileContract;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The result of parsing a raw request body: the parsed form fields and the uploaded files.
 *
 * <p>Produced by {@link io.valkyrja.http.message.request.factory.RequestBodyFactory} and consumed
 * by {@link io.valkyrja.http.message.request.factory.RequestFactory} to populate a request's parsed
 * body and uploaded-file collections — the raw-runtime analog of the values PHP's SAPI exposes as
 * {@code $_POST} and {@code $_FILES}.
 *
 * @param parsedBody the parsed form fields (empty when the body carries none)
 * @param files the uploaded files (empty when the body carries none)
 */
public record ParsedRequestBody(
        Map<String, Object> parsedBody, Map<String, UploadedFileContract> files) {

    public ParsedRequestBody {
        // Copied through a LinkedHashMap rather than Map.copyOf so the order the fields and files
        // were spelled in the body survives onto the request; Map.copyOf makes no ordering
        // guarantee, and every collection these feed preserves insertion order.
        parsedBody = Collections.unmodifiableMap(new LinkedHashMap<>(parsedBody));
        files = Collections.unmodifiableMap(new LinkedHashMap<>(files));
    }
}
