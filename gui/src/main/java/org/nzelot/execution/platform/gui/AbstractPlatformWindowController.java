package org.nzelot.execution.platform.gui;

import org.nzelot.execution.platform.core.Platform;
import org.nzelot.execution.platform.core.algorithm.Algorithm;
import org.nzelot.execution.platform.core.algorithm.AlgorithmExecutionEngineControlEvent;
import org.nzelot.execution.platform.core.algorithm.AlgorithmRunnerEvent;
import org.nzelot.execution.platform.core.algorithm.Generator;
import org.nzelot.execution.platform.core.eventbus.EventSink;
import org.nzelot.execution.platform.core.flightrecorder.FlightRecorderControlEvent;
import org.nzelot.execution.platform.gui.initialisation.InitializationDialog;
import org.nzelot.execution.platform.gui.result.ResultDialog;

import java.util.Map;

public abstract class AbstractPlatformWindowController {

    protected AbstractPlatformWindowView view;

    void initializeController(AbstractPlatformWindowView view) {
        this.view = view;
        Platform.registerSink(this);
    }

    void clearInstance() {
        Platform.distributeEvent(AlgorithmExecutionEngineControlEvent.CLEAR());
        Platform.distributeEvent(FlightRecorderControlEvent.CLEAR_RECORDING);
        clearContent();
        updateContent();
    }

    void resetInstance() {
        Platform.distributeEvent(FlightRecorderControlEvent.CLEAR_RECORDING);
        resetToCleanInstance();
        updateContent();
    }

    void initializeAlgorithm(Class<?> algorithmClass) {
        if (!algorithmClass.isAnnotationPresent(Generator.class) && !algorithmClass.isAnnotationPresent(Algorithm.class))
            throw new IllegalArgumentException(algorithmClass.getCanonicalName() + " is not annotated with "
                    + Generator.class.getCanonicalName() + " or " + Algorithm.class.getCanonicalName());

        new InitializationDialog(algorithmClass)
                .showAndWait()
                .ifPresentOrElse(result -> {
                    //ok button on dialog was clicked
                    if(algorithmClass.isAnnotationPresent(Generator.class))
                        clearInstance();
                    else
                        Platform.distributeEvent(FlightRecorderControlEvent.CLEAR_RECORDING);

                    if(result.getKey())
                        Platform.distributeEvent(FlightRecorderControlEvent.START_RECORDING);
                    Platform.distributeEvent(AlgorithmExecutionEngineControlEvent.START(
                            algorithmClass,
                            result.getValue()));
                }, () -> {
                    //the dialog was cancelled
                    System.out.println("Dialog cancelled.");
                });
    }

    private void showAlgorithmResultDialog(String algoName, Map<String, Object> results){
        new ResultDialog(algoName, results).showAndWait();
    }

    @EventSink(AlgorithmRunnerEvent.class)
    public void checkAlgorithmRunning(AlgorithmRunnerEvent event) {
        javafx.application.Platform.runLater(() -> {
            if(event.isStarted())
                this.view.setAlgorithmRunning(true);
            else {
                Platform.distributeEvent(FlightRecorderControlEvent.END_RECORDING);
                if(event.getResults() != null && event.getResults().size() > 0)
                    this.showAlgorithmResultDialog(event.getAlgoName(), event.getResults());
                this.view.setAlgorithmRunning(false);
            }
        });
    }

    protected abstract void resetToCleanInstance();
    protected abstract void updateContent();
    protected abstract void clearContent();
}
