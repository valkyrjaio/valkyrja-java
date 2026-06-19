/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.server.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.cli.server.support.Exiter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Test the {@link Exiter}. */
final class ExiterTest {

    private final PrintStream originalOut = System.out;

    @AfterEach
    void cleanup() {
        System.setOut(originalOut);
        Exiter.unfreeze();
    }

    private String captureExit(int code) {
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        Exiter.exit(code);
        System.setOut(originalOut);
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void frozenExitInvokesCallbackInsteadOfExiting() {
        Exiter.freeze();

        assertEquals("5", captureExit(5));
    }

    @Test
    void frozenNoArgExitUsesZero() {
        Exiter.freeze();
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));

        Exiter.exit();
        System.setOut(originalOut);

        assertEquals("0", buffer.toString(StandardCharsets.UTF_8));
    }

    @Test
    void frozenCallbackPrintsCode() {
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));

        Exiter.frozenCallback(7);
        System.setOut(originalOut);

        assertEquals("7", buffer.toString(StandardCharsets.UTF_8));
    }

    @Test
    void isInstantiable() {
        assertNotNull(new Exiter());
    }
}
