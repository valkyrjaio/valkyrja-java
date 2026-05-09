/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri;

import io.valkyrja.http.message.uri.contract.UriContract;
import io.valkyrja.http.message.uri.enum_.Scheme;
import io.valkyrja.http.message.uri.factory.UriFactory;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPathException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPortException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidQueryException;

public class Uri implements UriContract {

    protected Scheme scheme;
    protected String username;
    protected String password;
    protected String userInfo;
    protected String host;
    protected int    port;
    protected String path;
    protected String query;
    protected String fragment;

    private String uriString = null;

    public Uri() {
        this(Scheme.EMPTY, "", "", "", 0, "", "", "");
    }

    public Uri(String path) {
        this(Scheme.EMPTY, "", "", "", 0, path, "", "");
    }

    public Uri(
        Scheme scheme,
        String username,
        String password,
        String host,
        int port,
        String path,
        String query,
        String fragment
    ) {
        String userInfo = username;

        if (!password.isEmpty()) {
            userInfo += ":" + password;
        }

        if (port == 0) {
            port = getPortFromScheme(scheme);
        } else {
            UriFactory.validatePort(port);
        }

        this.scheme   = scheme;
        this.username = username;
        this.password = password;
        this.port     = port;
        this.userInfo = UriFactory.filterUserInfo(userInfo);
        this.host     = host.toLowerCase();
        this.path     = UriFactory.filterPath(path);
        this.query    = UriFactory.filterQuery(query);
        this.fragment = UriFactory.filterFragment(fragment);
    }

    @Override
    public boolean isSecure() {
        return scheme == Scheme.HTTPS;
    }

    @Override
    public Scheme getScheme() {
        return scheme;
    }

    @Override
    public String getAuthority() {
        if (host.isEmpty()) {
            return "";
        }

        String authority = host;

        if (!userInfo.isEmpty()) {
            authority = userInfo + "@" + authority;
        }

        if (!UriFactory.isStandardPort(scheme, host, port)) {
            authority += ":" + port;
        }

        return authority;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUserInfo() {
        return userInfo;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public boolean hasPort() {
        return port != 0;
    }

    @Override
    public int getPort() {
        return UriFactory.isStandardPort(scheme, host, port) ? 0 : port;
    }

    @Override
    public String getHostPort() {
        String h    = host;
        int    p    = getPort();

        if (!h.isEmpty() && p != 0) {
            h += ":" + p;
        }

        return h;
    }

    @Override
    public String getSchemeHostPort() {
        String hostPort = getHostPort();

        return !hostPort.isEmpty() && scheme != Scheme.EMPTY
            ? scheme.getValue() + "://" + hostPort
            : hostPort;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public String getFragment() {
        return fragment;
    }

    @Override
    public UriContract withScheme(Scheme scheme) {
        Uri newUri = copy();

        newUri.scheme = scheme;

        if (this.port == 0) {
            newUri.port = getPortFromScheme(scheme);
        }

        return newUri;
    }

    @Override
    public UriContract withUsername(String username) {
        return withUserInfo(username, this.password);
    }

    @Override
    public UriContract withPassword(String password) {
        return withUserInfo(this.username, password);
    }

    @Override
    public UriContract withUserInfo(String user, String password) {
        String info = user;

        if (user.isEmpty()) {
            password = "";
        }

        if (!password.isEmpty()) {
            info += ":" + password;
        }

        Uri newUri = copy();

        newUri.userInfo = info;
        newUri.username = user;
        newUri.password = password;

        return newUri;
    }

    @Override
    public UriContract withHost(String host) {
        Uri newUri = copy();

        newUri.host = host;

        return newUri;
    }

    @Override
    public UriContract withPort(int port) {
        UriFactory.validatePort(port);

        Uri newUri = copy();

        newUri.port = port;

        return newUri;
    }

    @Override
    public UriContract withPath(String path) {
        path = UriFactory.filterPath(path);

        Uri newUri = copy();

        newUri.path = path;

        return newUri;
    }

    @Override
    public UriContract withQuery(String query) {
        query = UriFactory.filterQuery(query);

        Uri newUri = copy();

        newUri.query = query;

        return newUri;
    }

    @Override
    public UriContract withFragment(String fragment) {
        fragment = UriFactory.filterFragment(fragment);

        Uri newUri = copy();

        newUri.fragment = fragment;

        return newUri;
    }

    @Override
    public String toString() {
        if (uriString == null) {
            uriString = UriFactory.toString(this);
        }

        return uriString;
    }

    protected int getPortFromScheme(Scheme scheme) {
        if (scheme == Scheme.HTTPS) {
            return 443;
        }

        if (scheme == Scheme.HTTP) {
            return 80;
        }

        return 0;
    }

    private Uri copy() {
        Uri newUri = new Uri();

        newUri.scheme   = this.scheme;
        newUri.username = this.username;
        newUri.password = this.password;
        newUri.userInfo = this.userInfo;
        newUri.host     = this.host;
        newUri.port     = this.port;
        newUri.path     = this.path;
        newUri.query    = this.query;
        newUri.fragment = this.fragment;
        newUri.uriString = null;

        return newUri;
    }
}