/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.grpc.message.status;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.status.Status;
import io.valkyrja.grpc.message.status.contract.StatusContract;
import org.junit.jupiter.api.Test;

/** Test the {@link Status} value type. */
final class StatusTest {

    @Test
    void defaultsMessageFromCode() {
        Status status = new Status(StatusCode.NOT_FOUND);
        assertEquals(StatusCode.NOT_FOUND, status.getCode());
        assertEquals(StatusCode.NOT_FOUND.getDefaultMessage(), status.getMessage());
        assertNull(status.getDetails());
        assertFalse(status.hasDetails());
    }

    @Test
    void okFactory() {
        Status status = Status.ok();
        assertTrue(status.isOk());
        assertFalse(status.isCancellation());
    }

    @Test
    void cancelledFactoriesCarryMessages() {
        assertEquals("gone", Status.cancelled("gone").getMessage());
        assertTrue(Status.cancelled(null).isCancellation());
        assertTrue(Status.deadlineExceeded(null).isCancellation());
    }

    @Test
    void ofWithCodeOnlyUsesDefaultMessage() {
        Status status = Status.of(StatusCode.OK);
        assertEquals(StatusCode.OK, status.getCode());
        assertEquals(StatusCode.OK.getDefaultMessage(), status.getMessage());
    }

    @Test
    void ofWithNullMessageUsesDefault() {
        Status status = Status.of(StatusCode.INTERNAL, null);
        assertEquals(StatusCode.INTERNAL.getDefaultMessage(), status.getMessage());
    }

    @Test
    void ofWithMessageOverridesDefault() {
        Status status = Status.of(StatusCode.INTERNAL, "boom");
        assertEquals("boom", status.getMessage());
    }

    @Test
    void internalWithDetails() {
        byte[] details = {1, 2, 3};
        Status status = Status.internal("boom", details);
        assertEquals("boom", status.getMessage());
        assertTrue(status.hasDetails());
        assertArrayEquals(details, status.getDetails());
    }

    @Test
    void internalWithNullMessageUsesDefault() {
        Status status = Status.internal(null, null);
        assertEquals(StatusCode.INTERNAL.getDefaultMessage(), status.getMessage());
    }

    @Test
    void detailsAreDefensivelyCopied() {
        byte[] details = {9, 9};
        Status status = new Status(StatusCode.INTERNAL, "x", details);
        details[0] = 0;
        assertArrayEquals(new byte[] {9, 9}, status.getDetails());

        byte[] returned = status.getDetails();
        assert returned != null;
        returned[0] = 0;
        assertArrayEquals(new byte[] {9, 9}, status.getDetails());
    }

    @Test
    void withCodePreservesMessageAndDetails() {
        Status base = Status.internal("boom", new byte[] {7});
        StatusContract updated = base.withCode(StatusCode.UNAVAILABLE);
        assertEquals(StatusCode.UNAVAILABLE, updated.getCode());
        assertEquals("boom", updated.getMessage());
        assertArrayEquals(new byte[] {7}, updated.getDetails());
    }

    @Test
    void withMessage() {
        StatusContract updated = Status.ok().withMessage("done");
        assertEquals("done", updated.getMessage());
        assertEquals(StatusCode.OK, updated.getCode());
    }

    @Test
    void withDetails() {
        StatusContract updated = Status.ok().withDetails(new byte[] {4, 5});
        assertArrayEquals(new byte[] {4, 5}, updated.getDetails());
        assertTrue(updated.hasDetails());
    }

    @Test
    void everyFactoryProducesItsCode() {
        assertEquals(StatusCode.UNKNOWN, Status.unknown(null).getCode());
        assertEquals(StatusCode.INVALID_ARGUMENT, Status.invalidArgument(null).getCode());
        assertEquals(StatusCode.ALREADY_EXISTS, Status.alreadyExists(null).getCode());
        assertEquals(StatusCode.PERMISSION_DENIED, Status.permissionDenied(null).getCode());
        assertEquals(StatusCode.RESOURCE_EXHAUSTED, Status.resourceExhausted(null).getCode());
        assertEquals(StatusCode.FAILED_PRECONDITION, Status.failedPrecondition(null).getCode());
        assertEquals(StatusCode.ABORTED, Status.aborted(null).getCode());
        assertEquals(StatusCode.OUT_OF_RANGE, Status.outOfRange(null).getCode());
        assertEquals(StatusCode.UNIMPLEMENTED, Status.unimplemented(null).getCode());
        assertEquals(StatusCode.INTERNAL, Status.internal(null).getCode());
        assertEquals(StatusCode.UNAVAILABLE, Status.unavailable(null).getCode());
        assertEquals(StatusCode.DATA_LOSS, Status.dataLoss(null).getCode());
        assertEquals(StatusCode.UNAUTHENTICATED, Status.unauthenticated(null).getCode());
        assertEquals(StatusCode.NOT_FOUND, Status.notFound(null).getCode());
    }
}
