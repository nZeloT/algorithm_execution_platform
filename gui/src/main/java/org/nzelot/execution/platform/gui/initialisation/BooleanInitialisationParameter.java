package org.nzelot.execution.platform.gui.initialisation;

public @interface BooleanInitialisationParameter {
    String name();
    boolean defaultValue() default false;
}
