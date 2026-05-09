/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.value;

import io.valkyrja.http.message.enum_.SameSite;
import io.valkyrja.http.message.header.value.component.Component;
import io.valkyrja.http.message.header.value.component.contract.ComponentContract;
import io.valkyrja.http.message.header.value.contract.CookieContract;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Cookie extends Value implements CookieContract {

    private static final DateTimeFormatter COOKIE_DATE_FORMATTER = DateTimeFormatter
        .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
        .withZone(ZoneOffset.UTC);

    protected String name;
    protected String value;
    protected int expire;
    protected String path;
    protected String domain;
    protected boolean secure;
    protected boolean httpOnly;
    protected boolean raw;
    protected SameSite sameSite;
    protected boolean delete;

    public Cookie(String name) {
        this(name, "", 0, "/", "", false, true, false, SameSite.LAX, false);
    }

    public Cookie(String name, String value) {
        this(name, value, 0, "/", "", false, true, false, SameSite.LAX, false);
    }

    public Cookie(
        String name,
        String value,
        int expire,
        String path,
        String domain,
        boolean secure,
        boolean httpOnly,
        boolean raw,
        SameSite sameSite,
        boolean delete
    ) {
        super();
        this.name = name;
        this.value = value;
        this.expire = expire;
        this.path = path;
        this.domain = domain;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.raw = raw;
        this.sameSite = sameSite;
        this.delete = delete;
    }

    @Override
    public String toString() {
        String cookieValue = this.value;
        int cookieExpire = this.expire;
        int maxAge = getMaxAge();

        if (this.delete) {
            cookieExpire = (int) Instant.now().getEpochSecond() - 31536001;
            maxAge = -31536001;
            cookieValue = "delete";
        }

        List<String> parts = new ArrayList<>();

        parts.add(new Component(
            URLEncoder.encode(this.name, StandardCharsets.UTF_8),
            URLEncoder.encode(cookieValue, StandardCharsets.UTF_8)
        ).toString());

        if (cookieExpire != 0) {
            String expires = COOKIE_DATE_FORMATTER.format(Instant.ofEpochSecond(cookieExpire));
            parts.add(new Component("expires", expires).toString());
            parts.add(new Component("max-age", String.valueOf(maxAge)).toString());
        }

        parts.add(new Component("path", this.path).toString());

        String domainComponent = getDomainComponentString();

        if (!domainComponent.isEmpty()) {
            parts.add(domainComponent);
        }

        String secureComponent = getSecureComponentString();

        if (!secureComponent.isEmpty()) {
            parts.add(secureComponent);
        }

        String httpOnlyComponent = getHttpOnlyComponentString();

        if (!httpOnlyComponent.isEmpty()) {
            parts.add(httpOnlyComponent);
        }

        parts.add(new Component("samesite", this.sameSite.getValue()).toString());

        return parts.stream()
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining("; "));
    }

    @Override
    public CookieContract delete() {
        Cookie newCookie = copy();
        newCookie.delete = true;
        return newCookie;
    }

    @Override
    public int getMaxAge() {
        return this.expire > 0
            ? this.expire - (int) Instant.now().getEpochSecond()
            : 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CookieContract withName(String name) {
        Cookie newCookie = copy();
        newCookie.name = name;
        return newCookie;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public CookieContract withValue(String value) {
        Cookie newCookie = copy();
        newCookie.value = value;
        return newCookie;
    }

    @Override
    public int getExpire() {
        return expire;
    }

    @Override
    public CookieContract withExpire(int expire) {
        Cookie newCookie = copy();
        newCookie.expire = expire;
        return newCookie;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public CookieContract withPath(String path) {
        Cookie newCookie = copy();
        newCookie.path = path;
        return newCookie;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public CookieContract withDomain(String domain) {
        Cookie newCookie = copy();
        newCookie.domain = domain;
        return newCookie;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public CookieContract withSecure(boolean secure) {
        Cookie newCookie = copy();
        newCookie.secure = secure;
        return newCookie;
    }

    @Override
    public boolean isHttpOnly() {
        return httpOnly;
    }

    @Override
    public CookieContract withHttpOnly(boolean httpOnly) {
        Cookie newCookie = copy();
        newCookie.httpOnly = httpOnly;
        return newCookie;
    }

    @Override
    public boolean isRaw() {
        return raw;
    }

    @Override
    public CookieContract withRaw(boolean raw) {
        Cookie newCookie = copy();
        newCookie.raw = raw;
        return newCookie;
    }

    @Override
    public SameSite getSameSite() {
        return sameSite;
    }

    @Override
    public CookieContract withSameSite(SameSite sameSite) {
        Cookie newCookie = copy();
        newCookie.sameSite = sameSite;
        return newCookie;
    }

    protected String getDomainComponentString() {
        return !this.domain.isEmpty()
            ? new Component("domain", this.domain).toString()
            : "";
    }

    protected String getSecureComponentString() {
        return this.secure
            ? new Component("secure").toString()
            : "";
    }

    protected String getHttpOnlyComponentString() {
        return this.httpOnly
            ? new Component("httponly").toString()
            : "";
    }

    protected Cookie copy() {
        Cookie newCookie = new Cookie(
            this.name,
            this.value,
            this.expire,
            this.path,
            this.domain,
            this.secure,
            this.httpOnly,
            this.raw,
            this.sameSite,
            this.delete
        );
        newCookie.components = new ArrayList<>(this.components);
        newCookie.position = this.position;
        return newCookie;
    }
}