/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.dispatch;

import io.valkyrja.dispatch.data.contract.DispatchContract;
import java.util.Map;

/** A dispatch type the dispatcher does not recognize — triggers the unknown-type branch. */
public final class UnknownDispatchClass implements DispatchContract {

    @Override
    public Map<String, Object> toMap() {
        return Map.of();
    }

    @Override
    public String toString() {
        return "unknown";
    }
}
