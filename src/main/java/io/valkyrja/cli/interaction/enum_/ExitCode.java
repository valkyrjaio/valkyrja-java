/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.enum_;

public enum ExitCode {
    SUCCESS(0),
    ERROR(1),
    USAGE_ERROR(64),
    DATA_ERROR(65),
    NO_INPUT(67),
    NO_USER(68),
    UNAVAILABLE(69),
    SOFTWARE_ERROR(70),
    OS_ERROR(71),
    OS_FILE_ERROR(72),
    CANT_CREATE(73),
    IO_ERROR(74),
    TEMP_FAIL(75),
    PROTOCOL_ERROR(76),
    NO_PERMISSION(77),
    CONFIG_ERROR(78),
    AUTO_EXIT(255),
    ;

    public final int value;

    ExitCode(int value) {
        this.value = value;
    }
}
