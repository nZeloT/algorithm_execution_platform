package org.nzelot.platform.javafx.initialisation;

public @interface BooleanInitialisationParameter {
    String name();
    boolean defaultValue() default false;
}
