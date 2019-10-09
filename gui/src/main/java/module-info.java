module nzelot.execution.platform.gui {
    exports org.nzelot.execution.platform.gui.initialisation;
    exports org.nzelot.execution.platform.gui.rendering;
    exports org.nzelot.execution.platform.gui.playback;
    exports org.nzelot.execution.platform.gui;

    opens org.nzelot.execution.platform.gui.controls.flightrecorder to nzelot.execution.platform.core;
    opens org.nzelot.execution.platform.gui.rendering to javafx.graphics;
    opens org.nzelot.execution.platform.gui to javafx.graphics;

    requires nzelot.execution.platform.core;
    requires javafx.controls;
    requires javafx.graphics;
    requires reflections;
}