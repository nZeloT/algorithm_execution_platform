package org.nzelot.platform.javafx.initialisation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntInitialisationParameter {
    String name();
    int defaultValue() default 0;
    int lowerBound() default Integer.MIN_VALUE;
    int upperBound() default Integer.MAX_VALUE;
    int stepWidth() default 1;
}
