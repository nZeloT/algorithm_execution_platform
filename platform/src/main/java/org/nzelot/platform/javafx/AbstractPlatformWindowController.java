package org.nzelot.platform.javafx;

import org.nzelot.platform.Platform;
import org.nzelot.platform.algorithm.Algorithm;
import org.nzelot.platform.algorithm.AlgorithmExecutionEngineControlEvent;
import org.nzelot.platform.algorithm.AlgorithmRunnerEvent;
import org.nzelot.platform.algorithm.Generator;
import org.nzelot.platform.eventbus.EventSink;
import org.nzelot.platform.flightrecorder.FlightRecorderControlEvent;
import org.nzelot.platform.javafx.initialisation.InitializationDialog;
import org.nzelot.platform.javafx.result.ResultDialog;

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
