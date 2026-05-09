/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.constant;

public abstract class Regex {

    public static final String PATH = "\\/";
    public static final String ANY  = ".*";
    public static final String NUM  = "\\d+";

    public static final String ID = NUM;

    public static final String SLUG = "[a-zA-Z0-9-]+";

    public static final String UUID    = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    public static final String UUID_V1 = "[0-9a-f]{8}-[0-9a-f]{4}-1[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    public static final String UUID_V3 = "[0-9a-f]{8}-[0-9a-f]{4}-3[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    public static final String UUID_V4 = "[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    public static final String UUID_V5 = "[0-9a-f]{8}-[0-9a-f]{4}-5[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    public static final String UUID_V6 = "[0-9a-f]{8}-[0-9a-f]{4}-6[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    public static final String UUID_V7 = "[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    public static final String UUID_V8 = "[0-9a-f]{8}-[0-9a-f]{4}-8[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

    public static final String ULID = "[0-7][0-9A-HJKMNP-TV-Z]{25}";

    public static final String VLID    = "[0-9A-Z]{26}";
    public static final String VLID_V1 = "[0-9A-Z]{26}";
    public static final String VLID_V2 = "[0-9A-Z]{26}";
    public static final String VLID_V3 = "[0-9A-Z]{26}";
    public static final String VLID_V4 = "[0-9A-Z]{26}";

    public static final String ALPHA                = "[a-zA-Z]+";
    public static final String ALPHA_LOWERCASE      = "[a-z]+";
    public static final String ALPHA_UPPERCASE      = "[A-Z]+";
    public static final String ALPHA_NUM            = "[a-zA-Z0-9]+";
    public static final String ALPHA_NUM_UNDERSCORE = "\\w+";

    public static final String START = "/^";
    public static final String END   = "$/";

    public static final String START_CAPTURE_GROUP          = "(";
    public static final String START_NON_CAPTURE_GROUP      = "(?:";
    public static final String END_CAPTURE_GROUP            = ")";
    public static final String END_OPTIONAL_CAPTURE_GROUP   = ")?";
    public static final String START_OPTIONAL_CAPTURE_GROUP = START_NON_CAPTURE_GROUP + PATH + END_OPTIONAL_CAPTURE_GROUP;

    public static final String START_CAPTURE_GROUP_NAME = "?<";
    public static final String END_CAPTURE_GROUP_NAME   = ">";
}