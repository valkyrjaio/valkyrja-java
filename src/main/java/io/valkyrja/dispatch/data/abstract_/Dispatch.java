/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data.abstract_;

import io.valkyrja.dispatch.data.contract.DispatchContract;
import java.util.Map;

/** Abstract base for all dispatch data objects. */
public abstract class Dispatch implements DispatchContract {

    @Override
    public abstract Map<String, Object> toMap();

    @Override
    public abstract String toString();
}
