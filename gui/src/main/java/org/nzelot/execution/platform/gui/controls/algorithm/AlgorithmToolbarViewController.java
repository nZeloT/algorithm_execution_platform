package org.nzelot.execution.platform.gui.controls.algorithm;

import javafx.beans.property.BooleanProperty;
import org.nzelot.execution.platform.core.Platform;
import org.nzelot.execution.platform.core.algorithm.AlgorithmExecutionEngineControlEvent;

public class AlgorithmToolbarViewController {

    private AlgorithmToolbarView view;

    public AlgorithmToolbarViewController(AlgorithmToolbarView view, BooleanProperty visibleProperty) {
        this.view = view;
        this.view.setup(visibleProperty);
        setup();
    }

    private void setup() {
        this.view.btnCancle.setOnAction(evt -> Platform.distributeEvent(AlgorithmExecutionEngineControlEvent.CANCLE()));
    }
}
