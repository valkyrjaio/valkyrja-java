/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.contract;

import io.valkyrja.http.message.uri.enum_.Scheme;
import org.jspecify.annotations.Nullable;

/**
 * @see <a href="http://tools.ietf.org/html/rfc3986">RFC 3986 — URI specification</a>
 */
public interface UriContract {

    Scheme getScheme();

    boolean isSecure();

    String getAuthority();

    String getUsername();

    String getPassword();

    String getUserInfo();

    String getHost();

    boolean hasPort();

    int getPort();

    String getHostPort();

    String getSchemeHostPort();

    String getPath();

    String getQuery();

    String getFragment();

    UriContract withScheme(Scheme scheme);

    UriContract withUsername(String username);

    UriContract withPassword(String password);

    UriContract withUserInfo(String user, @Nullable String password);

    UriContract withHost(String host);

    UriContract withPort(int port);

    UriContract withPath(String path);

    UriContract withQuery(String query);

    UriContract withFragment(String fragment);

    String toString();
}
