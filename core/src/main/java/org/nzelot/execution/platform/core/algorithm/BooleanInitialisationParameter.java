package org.nzelot.execution.platform.core.algorithm;

public @interface BooleanInitialisationParameter {
    String name();
    boolean defaultValue() default false;
}
