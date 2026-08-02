/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.header.value.contract.CookieContract;

public class SetCookie extends Header {

    public SetCookie(CookieContract... cookies) {
        super(HeaderName.SET_COOKIE, (Object[]) cookies);
    }
}
