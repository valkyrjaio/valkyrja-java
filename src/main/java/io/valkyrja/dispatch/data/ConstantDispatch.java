/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data;

import io.valkyrja.dispatch.data.abstract_.Dispatch;
import io.valkyrja.dispatch.data.contract.ConstantDispatchContract;
import io.valkyrja.dispatch.throwable.exception.DispatchNoClassException;
import java.util.HashMap;
import java.util.Map;

/** Dispatch targeting a Java static final field constant. */
public class ConstantDispatch extends Dispatch implements ConstantDispatchContract {

    protected String constant;
    protected Class<?> className;

    public ConstantDispatch(String constant) {
        this(constant, null);
    }

    public ConstantDispatch(String constant, Class<?> className) {
        this.constant = constant;
        this.className = className;
    }

    @Override
    public String getConstant() {
        return constant;
    }

    @Override
    public ConstantDispatchContract withConstant(String constant) {
        return new ConstantDispatch(constant, className);
    }

    @Override
    public boolean hasClassName() {
        return className != null;
    }

    @Override
    public Class<?> getClassName() {
        if (className == null) {
            throw new DispatchNoClassException("No class set on ConstantDispatch");
        }
        return className;
    }

    @Override
    public ConstantDispatchContract withClassName(Class<?> className) {
        return new ConstantDispatch(constant, className);
    }

    @Override
    public ConstantDispatchContract withoutClassName() {
        return new ConstantDispatch(constant, null);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("constant", constant);
        if (className != null) {
            map.put("class", className.getName());
        }
        return map;
    }

    @Override
    public String toString() {
        return (className != null ? className.getName() + "::" : "") + constant;
    }
}
