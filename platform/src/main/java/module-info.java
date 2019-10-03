module nzelot.platform {
    exports org.nzelot.platform;
    exports org.nzelot.platform.algorithm;
    exports org.nzelot.platform.eventbus;
    exports org.nzelot.platform.flightrecorder;
    exports org.nzelot.platform.javafx;
    exports org.nzelot.platform.javafx.initialisation;
    exports org.nzelot.platform.javafx.rendering;
    exports org.nzelot.platform.javafx.playback;
    exports org.nzelot.platform.util;
    opens org.nzelot.platform.javafx.rendering to javafx.graphics;
    opens org.nzelot.platform.javafx to javafx.graphics;
    requires javafx.controls;
    requires javafx.graphics;
    requires reflections;
}