/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.constant;

/**
 * @see <a href="https://www.iana.org/assignments/media-types/media-types.xhtml">IANA Media
 *     Types</a>
 */
public final class ContentTypeValue {

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JAVASCRIPT = "application/javascript";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_XML_UTF8 = APPLICATION_XML + "; charset=utf-8";
    public static final String APPLICATION_X_WWW_FORM = "application/x-www-form-urlencoded";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_HTML_UTF8 = TEXT_HTML + "; charset=utf-8";
    public static final String TEXT_JAVASCRIPT = "text/javascript";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_PLAIN_UTF8 = TEXT_PLAIN + "; charset=utf-8";

    private ContentTypeValue() {}
}
