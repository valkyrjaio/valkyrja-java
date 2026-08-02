/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header;

import io.valkyrja.http.message.constant.HeaderName;

public class ContentType extends Header {

    public ContentType(Object... values) {
        super(HeaderName.CONTENT_TYPE, values);
    }
}
