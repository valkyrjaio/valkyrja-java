/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.constant;

/**
 * @see <a href="https://www.iana.org/assignments/media-types/media-types.xhtml">IANA Media Types</a>
 */
public final class ContentTypeValue {

    public static final String APPLICATION_JSON       = "application/json";
    public static final String APPLICATION_JAVASCRIPT = "application/javascript";
    public static final String APPLICATION_XML        = "application/xml";
    public static final String APPLICATION_XML_UTF8   = APPLICATION_XML + "; charset=utf-8";
    public static final String APPLICATION_X_WWW_FORM = "application/x-www-form-urlencoded";
    public static final String MULTIPART_FORM_DATA    = "multipart/form-data";
    public static final String TEXT_HTML              = "text/html";
    public static final String TEXT_HTML_UTF8         = TEXT_HTML + "; charset=utf-8";
    public static final String TEXT_JAVASCRIPT        = "text/javascript";
    public static final String TEXT_PLAIN             = "text/plain";
    public static final String TEXT_PLAIN_UTF8        = TEXT_PLAIN + "; charset=utf-8";

    private ContentTypeValue() {}
}
