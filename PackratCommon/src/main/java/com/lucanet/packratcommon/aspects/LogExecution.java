package com.lucanet.packratcommon.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for executing trace logging of function calls.
 * @see LogExecutionAspect
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {
}
