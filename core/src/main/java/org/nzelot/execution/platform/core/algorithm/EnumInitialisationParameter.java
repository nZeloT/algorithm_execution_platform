package org.nzelot.execution.platform.core.algorithm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumInitialisationParameter {
    String name();
    Class<? extends Enum>  valueDomain();
}
