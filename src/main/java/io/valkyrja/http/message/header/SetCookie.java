/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.header.value.contract.CookieContract;

public class SetCookie extends Header {

    public SetCookie(CookieContract... cookies) {
        super(HeaderName.SET_COOKIE, (Object[]) cookies);
    }
}