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

public class Referer extends Header {

    public Referer(Object... values) {
        super(HeaderName.REFERER, values);
    }
}