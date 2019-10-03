package org.nzelot.execution.platform.core;

import org.nzelot.execution.platform.core.algorithm.AlgorithmExecutionEngine;
import org.nzelot.execution.platform.core.eventbus.EventBroker;
import org.nzelot.execution.platform.core.flightrecorder.FlightRecorder;

public enum Platform {
    INSTANCE;

    private final EventBroker broker;
    private final FlightRecorder blackBox;
    private final AlgorithmExecutionEngine executionEngine;

    Platform() {
        broker = new EventBroker();
        blackBox = new FlightRecorder(broker);
        executionEngine = new AlgorithmExecutionEngine(broker);
    }

    public static <T> void registerSink(Object sink) {
        Platform.INSTANCE.broker.registerSink(sink);
    }

    public static <T> void distributeEvent(T event) {
        Platform.INSTANCE.broker.distributeEvent(event);
    }

    public static boolean isRecording() {
        return Platform.INSTANCE.blackBox.isRecording();
    }

    public static void activateFlightRecorderFullDeltaPlayback() {
        Platform.INSTANCE.blackBox.setFullDeltaPlayback(true);
    }

}
