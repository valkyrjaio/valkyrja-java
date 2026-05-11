/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.value.contract;

import io.valkyrja.http.message.enum_.SameSite;

public interface CookieContract extends ValueContract {

    CookieContract delete();

    int getMaxAge();

    String getName();

    CookieContract withName(String name);

    String getValue();

    CookieContract withValue(String value);

    int getExpire();

    CookieContract withExpire(int expire);

    String getPath();

    CookieContract withPath(String path);

    String getDomain();

    CookieContract withDomain(String domain);

    boolean isSecure();

    CookieContract withSecure(boolean secure);

    boolean isHttpOnly();

    CookieContract withHttpOnly(boolean httpOnly);

    boolean isRaw();

    CookieContract withRaw(boolean raw);

    SameSite getSameSite();

    CookieContract withSameSite(SameSite sameSite);
}
